package com.jibo.apptoolkit.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jibo.apptoolkit.protocol.model.Acknowledgment;
import com.jibo.apptoolkit.protocol.model.Command;
import com.jibo.apptoolkit.protocol.model.EventMessage;
import com.jibo.apptoolkit.protocol.model.Header;
import com.jibo.apptoolkit.protocol.utils.Commons;
import com.jibo.apptoolkit.protocol.utils.LruCache;
import com.jibo.apptoolkit.protocol.utils.StringUtils;
import com.jibo.apptoolkit.protocol.utils.Util;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import okhttp3.WebSocket;

import static com.jibo.apptoolkit.protocol.ConnectionException.ERROR_CONNECTION_PROBLEMS;

/*
 * Created by alexz on 01.11.17.
 */

/**
 * Main entry point for the Android Command Library
 */
public class CommandRequester {
    private static final String TAG = CommandRequester.class.getSimpleName();

    /** @hide */
    public static Gson sGson = new GsonBuilder().serializeNulls().create();

    private static final int COMMANDS_CACHE_SIZE = 10;

    private Acknowledgment.SessionResponse.SessionInfo mSessionInfo;
    private LruCache<String, Command> mCommands;
    private EventMessage.EventFactory mEventFactory = new EventMessage.EventFactory();
    private Map<String, OnCommandResponseListener> onCommandResponseListeners;

    private SSLContext mSslContext;
    private WebSocket mWebSocket;
    private String mIpAddress;
    private OnConnectionListener mOnConnectionListener;
    private HttpURLConnection mPhotoUrlConnection;
    private HttpURLConnection mVideoUrlConnection;
    private HttpURLConnection mGestureUrlConnection;

    private Assets assets;
    private Display display;
    private Perception perception;
    private Config config;
    private Listen listen;
    private Expression expression;
    private Media media;
    private Session session;

    public CommandRequester(SSLContext sslContext, WebSocket webSocket, String ipAddress, OnConnectionListener onConnectionListener) {
        this.mSslContext = sslContext;
        this.mWebSocket = webSocket;
        mIpAddress = ipAddress;
        this.mOnConnectionListener = onConnectionListener;

        mCommands = new LruCache<>(COMMANDS_CACHE_SIZE);
        onCommandResponseListeners = new HashMap<>();

        assets = new Assets(this);
        display = new Display(this);
        perception = new Perception(this);
        config = new Config(this);
        listen = new Listen(this);
        expression = new Expression(this);
        media = new Media(this);
        session = new Session(this);
    }

    /**
     * Cancel a transaction.
     * @param transactionID ID of the transaction to cancel.
     * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
     */
    public String cancel(String transactionID, OnCommandResponseListener onCommandResponseListener) {

        closeVideoConnection();

        //just making sure we're not getting nulls here
        if (StringUtils.isEmpty(transactionID)) return null;
        mCommands.remove(transactionID);
        onCommandResponseListeners.remove(transactionID);
        return !StringUtils.isEmpty(transactionID) ? sendCommand(new Command.CancelRequest(transactionID), onCommandResponseListener) : "";
    }

    /**
     * @hide */
    public String speech(boolean listen, OnCommandResponseListener onCommandResponseListener){
        return sendCommand(new Command.SpeechRequest(listen), onCommandResponseListener);
    }

    /**
     * Clear all listeners and states
     */
    public void clearListenersAndState() {
        mOnConnectionListener = null;
        mSessionInfo = null;

        if (mCommands != null) {
            mCommands.evictAll();
        }

        if (onCommandResponseListeners != null) {
            onCommandResponseListeners.clear();
        }
    }

    /**
     * Parse a response from the robot.
     * @param response String to parse
     */
    public void parseJiboResponse(String response) {

        String transactionID = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            //IF THIS IS AN ACKNOWLEDGMENT
            if (jsonObject.has("ResponseHeader")) {
                Acknowledgment acknowledgment = sGson.fromJson(response, Acknowledgment.class);
                transactionID = acknowledgment.getResponseHeader().getTransactionID();
                Command command = mCommands.get(transactionID);

                /****************WE HAVE RECEIVED ERROR FOR THE COMMAND CALL***************/
                if (acknowledgment.getResponse().getValue() == Acknowledgment.ValueResult.Error) {
                    //we have listener for this command
                    //we need to notify it about the error happened
                    if (onCommandResponseListeners.containsKey(transactionID)) {
                        Acknowledgment.ErrorResponseBody errorResponseBody =
                                sGson.fromJson(jsonObject.getJSONObject("Response").toString(),
                                        Acknowledgment.ErrorResponseBody.class);

                        onCommandResponseListeners.get(transactionID).onError(transactionID,
                                acknowledgment.getResponse().getResponseString() + ":" + errorResponseBody.getErrorDetail());

                        //it's an error, removing listener of the event
                        onCommandResponseListeners.remove(transactionID);
                    }
                    //if we find that the error belongs to StartSession command then we perform the disconnect
                    if (command.getCommand() instanceof Command.SessionRequest) {
                        if (mOnConnectionListener != null) {
                            mOnConnectionListener.onConnectionFailed(new ConnectionException(ERROR_CONNECTION_PROBLEMS));
                        }
                        session.end();
                    }
                } else {
                    /****************SUCCESSFULL START SESSION COMMAND***************/
                    //just checking if this command is Session start command
                    if (command.getCommand() instanceof Command.SessionRequest) {
                        //getting proper instance of Session and saving it
                        Acknowledgment.SessionResponse sessionResponse = sGson.fromJson(jsonObject.getJSONObject("Response").toString(), Acknowledgment.SessionResponse.class);
                        mSessionInfo = sessionResponse.getResponseBody();
                        // trigger the onSessionStarted callback after we've finished parsing the session info
                        // this is so if any commands are sent in the onSessionStarted callback, they will have a valid sessionID
                        if (mOnConnectionListener != null) {
                            mOnConnectionListener.onSessionStarted(this);
                        }
                    } else {
                        /****************SUCCESSFULL ANY OTHER COMMAND***************/
                        //we need to send call back saying command has been successfully executed on Jibo
                        if (onCommandResponseListeners.containsKey(transactionID)) {
                            onCommandResponseListeners.get(transactionID).onSuccess(transactionID);

                            //if command that's being executed does not support events then we can release listener
                            if (command.getCommand() instanceof Command.AtomicCommand) {
                                onCommandResponseListeners.remove(transactionID);
                            }
                        }
                    }
                }
            } 
            else {
                /****************THIS IS AN EVENT WE HAVE HERE****************/
                EventMessage eventMessage = mEventFactory.parseEventMessage(response);
                if (eventMessage == null) return;

                transactionID = eventMessage.getEventHeader().getTransactionID();
                Command command = mCommands.get(transactionID);
                //ok, if we have listener for this event lets send events
                if (onCommandResponseListeners.containsKey(transactionID)) {
                    //ok, something odd has happened and we've got error in event
                    if (eventMessage.getEventBody().getEvent() == EventMessage.EventType.Error) {
                        //TODO send proper field once it's updated in SDK
                        onCommandResponseListeners.get(transactionID).onEventError(
                                eventMessage.getEventHeader().getTransactionID(),
                                ((EventMessage.ErrorEvent)eventMessage.getEventBody()).getEventError());
                    } else {
                        onCommandResponseListeners.get(transactionID).onEvent(eventMessage.getEventHeader().getTransactionID(),
                                eventMessage.getEventBody());

                        mPhotoUrlConnection = null;
                        //ok, if it was photo/video request then we need to connect and fetch the stream for the
                        // third-party
                        if (eventMessage.getEventBody() instanceof EventMessage.TakePhotoEvent ||
                                eventMessage.getEventBody() instanceof EventMessage.VideoReadyEvent) {

                            URL requestedUrl;
                            StringBuilder stringBuilder = new StringBuilder(Commons.URLS_PROTOCOL).append(mIpAddress)
                                    .append(":")
                                    .append(Commons.SOCKET_PORT);
                            //getting last part of the URI for the connection
                            if (eventMessage.getEventBody() instanceof EventMessage.TakePhotoEvent) {
                                closePhotoConnection();
                                stringBuilder.append(((EventMessage.TakePhotoEvent) eventMessage.getEventBody()).getURI());
                            } else {
                                closeVideoConnection();
                                stringBuilder.append(((EventMessage.VideoReadyEvent) eventMessage.getEventBody()).getURI());
                            }
                            requestedUrl = new URL(stringBuilder.toString());
                            try {
                                HttpURLConnection urlConnection = (HttpURLConnection) requestedUrl.openConnection();
                                if(urlConnection instanceof HttpsURLConnection && Commons.JIBO_MODE) {
                                    if (mSslContext != null) {
                                        ((HttpsURLConnection) urlConnection)
                                                .setSSLSocketFactory(mSslContext.getSocketFactory());
                                    }

                                    ((HttpsURLConnection) urlConnection).setHostnameVerifier(new HostnameVerifier() {
                                        @Override
                                        public boolean verify(String s, SSLSession sslSession) {
                                            return mIpAddress.equals(s);
                                        }
                                    });
                                }
                                urlConnection.setRequestMethod("GET");
                                urlConnection.setConnectTimeout(30000);
                                urlConnection.setReadTimeout(10000);
                                int lastResponseCode = urlConnection.getResponseCode();
                                if (lastResponseCode < 400) {
                                    if (eventMessage.getEventBody() instanceof EventMessage.TakePhotoEvent) {
                                        mPhotoUrlConnection = urlConnection;
                                        onCommandResponseListeners.get(transactionID).onPhoto(
                                                eventMessage.getEventHeader().getTransactionID(),
                                                (EventMessage.TakePhotoEvent) eventMessage.getEventBody(),
                                                mPhotoUrlConnection.getInputStream());
                                    } else {
                                        mVideoUrlConnection = urlConnection;
                                        onCommandResponseListeners.get(transactionID).onVideo(
                                                eventMessage.getEventHeader().getTransactionID(),
                                                (EventMessage.VideoReadyEvent) eventMessage.getEventBody(),
                                                mVideoUrlConnection.getInputStream());
                                    }

                                }
                            } catch(Exception ex) {
                                System.out.print(TAG + " Error getting photo/video streams " + ex.getMessage());
                            }
                        }

                        // invoke onListen event
                        else if (eventMessage.getEventBody() instanceof EventMessage.ListenResultEvent) {
                            onCommandResponseListeners.get(transactionID).onListen(
                                    eventMessage.getEventHeader().getTransactionID(),
                                    ((EventMessage.ListenResultEvent) eventMessage.getEventBody()).getSpeech()
                            );
                        }
                    }

                    //this is one of the events that mean that we can release the listener
                    if (eventMessage.getEventBody() instanceof EventMessage.FinalisingEvent) {
                        boolean isFinalEvent = true;
                        //if this is VideoRequest, but Duration is not set, then we need to wait for onStop event
                        //to remove listener
                        if (command != null && command.getCommand() instanceof Command.VideoRequest
                                && (((Command.VideoRequest)command.getCommand()).getDuration() == null ||
                                ((Command.VideoRequest)command.getCommand()).getDuration() == 0)) {
                            isFinalEvent = false;
                        }
                        //ok, this is the last event in the chain - removing listener
                        if (isFinalEvent) {
                            onCommandResponseListeners.remove(transactionID);
                        }
                    }
                }

            }

        } catch (Exception e) {
            if (onCommandResponseListeners.containsKey(transactionID)) {
                onCommandResponseListeners.get(transactionID).onError(transactionID, ConnectionException.ERROR_INTERNAL_SYSTEM);
            }
            System.out.print(TAG + " Error parsing Jibo response " + e.getMessage());
        }
    }

    private String generateTransactionID() {
        return Util.md5(String.valueOf(System.currentTimeMillis()));
    }

    private void removeResponseListeners() {
        mSessionInfo = null;

        if (mCommands != null) {
            mCommands.evictAll();
        }

        if (onCommandResponseListeners != null) {
            onCommandResponseListeners.clear();
        }

    }

    private String sendCommand(Command.BaseCommand commandBody, OnCommandResponseListener onCommandResponseListener) {
        String tranID = null;
        if (mWebSocket != null) {
            tranID = generateTransactionID();

            Header.RequestHeader requestHeader = mSessionInfo == null ? new Header.RequestHeader(tranID)
                    : new Header.RequestHeader(tranID, mSessionInfo.getSessionID(), mSessionInfo.getVersion());
            Command command = new Command(requestHeader, commandBody);

            if (mWebSocket.send(sGson.toJson(command))) {
                mCommands.put(tranID, command);
                if (onCommandResponseListener != null) {
                    onCommandResponseListeners.put(tranID, onCommandResponseListener);
                }
                System.out.print(TAG + " Send : " + sGson.toJson(command));
            } else {
                tranID = null;
                System.out.print(TAG + " Not send : " + sGson.toJson(command));
            }

        }
        return tranID;
    }

    private void closePhotoConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPhotoUrlConnection != null) {
                    mPhotoUrlConnection.disconnect();
                    mPhotoUrlConnection = null;
                }

            }
        }).start();
    }

    private void closeVideoConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mVideoUrlConnection != null) {
                    mVideoUrlConnection.disconnect();
                    mVideoUrlConnection = null;
                }
            }
        }).start();
    }

    private void closeGestureConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mGestureUrlConnection != null) {
                    mGestureUrlConnection.disconnect();
                    mGestureUrlConnection = null;
                }
            }
        }).start();
    }

    private void closeMotionConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mGestureUrlConnection != null) {
                    mGestureUrlConnection.disconnect();
                    mGestureUrlConnection = null;
                }
            }
        }).start();
    }

    private void closeSpeechConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mGestureUrlConnection != null) {
                    mGestureUrlConnection.disconnect();
                    mGestureUrlConnection = null;
                }
            }
        }).start();
    }

    /**
     * Callback info
     */
    public interface OnCommandResponseListener {
        /**
         * Emitted on successful transaction
         * @param transactionID ID of the successful transation
         */
        public void onSuccess(String transactionID);

        /**
         * Emitted on error
         * @param transactionID ID of the failed transaction
         * @param errorMessage Description of the error
         */
        public void onError(String transactionID, String errorMessage);

        /**
         * Emitted on event error
         * @param transactionID ID of the failed transaction
         * @param errorData See EventMessage.ErrorEvent.ErrorData
         */
        public void onEventError(String transactionID, EventMessage.ErrorEvent.ErrorData errorData);

        /**
         * Emitted on websocket error
         */
        public void onSocketError();

        /**
         * Emitted on an event
         * @param transactionID ID of the transaction
         * @param event See EventMessage.BaseEvent
         */
        public void onEvent(String transactionID, EventMessage.BaseEvent event);

        /**
         * Emitted when Jibo takes a photo
         * @param transactionID ID of the transaction
         * @param event See EventMessage.TakePhotoEvent
         * @param inputStream Input stream of the photo
         */
        public void onPhoto(String transactionID, EventMessage.TakePhotoEvent event, InputStream inputStream);

        /**
         * Emitted when the video stream is ready
         * @param transactionID ID of the transaction
         * @param event See EventMessage.VideoReadyEvent
         * @param inputStream Input stream of the video recording
         */
        public void onVideo(String transactionID, EventMessage.VideoReadyEvent event, final InputStream inputStream);

        /**
         * Emitted when Jibo listen what we say to it
         * @param transactionID ID of the transaction
         * @param speech what Jibo is understanding
         */

        public void onListen(String transactionID, String speech);

        /**
         * Emitted when there's an error in parsing information from the robot
         */
        public void onParseError();
    }
    /** @hide */
    static public class SimpleOnCommandResponseListenerImpl implements OnCommandResponseListener {

        public void onSuccess(String transactionID) {}

        public void onError(String transactionID, String errorMessage) {}

        public void onEventError(String transactionID, EventMessage.ErrorEvent.ErrorData errorData) {}

        public void onSocketError() {}

        public void onEvent(String transactionID, EventMessage.BaseEvent event) {}

        public void onPhoto(String transactionID, EventMessage.TakePhotoEvent event, InputStream inputStream) {}

        public void onVideo(String transactionID, EventMessage.VideoReadyEvent event, final InputStream inputStream) {}

        public void onListen(String transactionID, String speech) {}

        public void onParseError() {}
    }


    //----------------------------------------------------------------------------------------------


    public Assets getAssets() {
        return assets;
    }

    public Display getDisplay() {
        return display;
    }

    public Perception getPerception() {
        return perception;
    }

    public Config getConfig() {
        return config;
    }

    public Listen getListen() {
        return listen;
    }

    public Expression getExpression() {
        return expression;
    }

    public Media getMedia() {
        return media;
    }

    public Session getSession() {
        return session;
    }


    //----------------------------------------------------------------------------------------------


    abstract static class Base {
        CommandRequester commandRequester;

        Base(CommandRequester commandRequester) {
            this.commandRequester = commandRequester;
        }
    }

    public static class Assets extends Base {

        Assets(CommandRequester commandRequester) {
            super(commandRequester);
        }

        /**
         * Retrieve external asset and store in local cache by name
         * @param uri URI to the asset to be fetched
         * @param name Name the asset will be called by
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
         */
        public String load(String uri, String name, OnCommandResponseListener onCommandResponseListener) {
            return commandRequester.sendCommand(new Command.FetchAssetRequest(uri, name), onCommandResponseListener);
        }
    }

    public static class Display extends Base{

        Subscribe subscribe;

        Display(CommandRequester commandRequester) {
            super(commandRequester);
            subscribe = new Subscribe(commandRequester);
        }

        public Subscribe getSubscribe() {
            return subscribe;
        }

        /**
         * Display something on Jibo's screen
         * @param view What to display onscreen. See Command.DisplayRequest.DisplayView
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
         */
        String display(Command.DisplayRequest.DisplayView view, OnCommandResponseListener onCommandResponseListener) {
            return commandRequester.sendCommand(new Command.DisplayRequest(view), onCommandResponseListener);
        }

        public String text(Command.DisplayRequest.TextView view, OnCommandResponseListener onCommandResponseListener) {
            return display(view, onCommandResponseListener);
        }

        public String image(Command.DisplayRequest.ImageView view, OnCommandResponseListener onCommandResponseListener) {
            return display(view, onCommandResponseListener);
        }

        public String eye(Command.DisplayRequest.EyeView view, OnCommandResponseListener onCommandResponseListener) {
            return display(view, onCommandResponseListener);
        }

        public static class Subscribe extends Base {

            Subscribe(CommandRequester commandRequester) {
                super(commandRequester);
            }

            /**
             * Listen for screen gesture
             * @param filter Options for type of gesture and location of gesture
             * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
             */
            public String gesture(Command.ScreenGestureRequest.ScreenGestureFilter filter, OnCommandResponseListener onCommandResponseListener) {
                commandRequester.closeGestureConnection();

                return commandRequester.sendCommand(new Command.ScreenGestureRequest(filter), onCommandResponseListener);
            }
        }
    }

    public static class Perception {

        Subscribe subscribe;

        Perception(CommandRequester commandRequester) {
            this.subscribe = new Subscribe(commandRequester);
        }

        public Subscribe getSubscribe() {
            return subscribe;
        }

        public static class Subscribe extends Base {

            Subscribe(CommandRequester commandRequester) {
                super(commandRequester);
            }

            /**
             * Get face entity
             * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
             * @return
             */
            public String face(OnCommandResponseListener onCommandResponseListener) {
                return commandRequester.sendCommand(new Command.EntityRequest(), onCommandResponseListener);
            }


            /**
             * Track motion in Jibo's perceptual space. See EventMessage.MotionEvent
             * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
             */
            public String motion(OnCommandResponseListener onCommandResponseListener){
                return commandRequester.sendCommand(new Command.MotionRequest(), onCommandResponseListener);
            }

            /**
             * Listen for head touch
             * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
             * @return
             */
            public String headTouch(OnCommandResponseListener onCommandResponseListener){
                return commandRequester.sendCommand(new Command.HeadTouchRequest(), onCommandResponseListener);
            }
        }

    }

    public static class Config extends Base {

        Config(CommandRequester commandRequester) {
            super(commandRequester);
        }

        /**
         * Set robot configuration data.
         * @param options Options to set. See Command.SetConfigRequest.SetConfigOptions.
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
         * @return
         */
        public String set(Command.SetConfigRequest.SetConfigOptions options, OnCommandResponseListener onCommandResponseListener){
            return commandRequester.sendCommand(new Command.SetConfigRequest(options), onCommandResponseListener);
        }

        /**
         * Get robot configuration data
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
         * @return
         */
        public String get(OnCommandResponseListener onCommandResponseListener){
            return commandRequester.sendCommand(new Command.GetConfigRequest(), onCommandResponseListener);
        }
    }

    public static class Listen extends Base {

        Listen(CommandRequester commandRequester) {
            super(commandRequester);
        }

        /**
         * Listen for speech input
         * @param maxSpeechTimeout Maximum amount of time Jibo should listen to speech. Default = 15. In seconds.
         * @param maxNoSpeechTimeout Maximum amount of time Jibo should wait for speech to begin. Default = 15. In seconds.
         * @param languageCode Language to listen for. Right now only english (`en_US`) is supported.
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)} or {@link OnCommandResponseListener#onParseError()}
         */
        public String start(Long maxSpeechTimeout, Long maxNoSpeechTimeout, String languageCode, OnCommandResponseListener onCommandResponseListener){
            return commandRequester.sendCommand(new Command.ListenRequest(maxSpeechTimeout, maxNoSpeechTimeout, languageCode), onCommandResponseListener);
        }
    }

    public static class Expression extends Base {

        Expression(CommandRequester commandRequester) {
            super(commandRequester);
        }

        /**
         * Make Jibo look toward a specific spot. See EventMessage.LookAtAchievedEvent.
         * @param lookAtTarget Where to make Jibo look. See Command.LookAtRequest
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
         */
        public String look(Command.LookAtRequest.BaseLookAtTarget lookAtTarget, OnCommandResponseListener onCommandResponseListener) {
            if (lookAtTarget == null) return null;
            if (lookAtTarget instanceof Command.LookAtRequest.AngleTarget) {
                return commandRequester.sendCommand(new Command.LookAtRequest(lookAtTarget, false), onCommandResponseListener);
            } else {
                return commandRequester.sendCommand(new Command.LookAtRequestExt(lookAtTarget, false, false), onCommandResponseListener);
            }
        }

        /**
         * Make Jibo speak.
         * @param text Text to speak. Can take plain text or ESML.
         *             See <a href="https://app-toolkit.jibo.com/esml/">App Toolkit Docs</a> for ESML info.
         * @param onCommandResponseListener {@link OnCommandResponseListener#onEvent(String, EventMessage.BaseEvent)}
         */
        public String say(String text, OnCommandResponseListener onCommandResponseListener) {
            if (StringUtils.isEmpty(text)) return null;
            return commandRequester.sendCommand(new Command.SayRequest(text), onCommandResponseListener);
        }
    }

    public static class Media extends Base {

        Capture capture;

        Media(CommandRequester commandRequester) {
            super(commandRequester);
            capture = new Capture(commandRequester);
        }

        public Capture getCapture() {
            return capture;
        }

        public static class Capture extends Base {

            Capture(CommandRequester commandRequester) {
                super(commandRequester);
            }

            /**
             * Take a photo. See EventMessage.TakePhotoEvent
             * @param camera Which camera to use (left or right). Default = left.
             * @param resolution Resolution photo to take. Default = low.
             * @param distortion `true` for regular lense. `false` for fisheye.
             * @param onCommandResponseListener {@link OnCommandResponseListener#onPhoto(String, EventMessage.TakePhotoEvent, InputStream)}
             */
            public String photo(Command.TakePhotoRequest.Camera camera, Command.TakePhotoRequest.CameraResolution
                    resolution, boolean distortion, OnCommandResponseListener onCommandResponseListener) {
                commandRequester.closePhotoConnection();

                return commandRequester.sendCommand(new Command.TakePhotoRequest(camera, resolution, distortion), onCommandResponseListener);
            }

            /**
             * Get a stream of what Jibo's cameras see. See EventMessage.VideoReadyEvent
             * </br> Please note that this option does NOT record a video -- it provides a stream of camera information.
             * @param videoType Use `NORMAL`.
             * @param duration Unsupported. Call `cancel()` to stop the stream.
             * @param onCommandResponseListener {@link OnCommandResponseListener#onVideo(String, EventMessage.VideoReadyEvent, InputStream)}
             */
            public String video(Command.VideoRequest.VideoType videoType, long duration, OnCommandResponseListener onCommandResponseListener) {

                commandRequester.closeVideoConnection();

                if (videoType == null) return null;
                return commandRequester.sendCommand(duration > 0 ? new Command.VideoRequest(videoType, duration)
                        : new Command.VideoRequest(videoType), onCommandResponseListener);
            }
        }
    }

    public static class Session extends Base {

        Session(CommandRequester commandRequester) {
            super(commandRequester);
        }

        /**
         * Start a command session
         */
        public String start() {
            return commandRequester.sendCommand(new Command.SessionRequest(), null);
        }

        /**
         * Get the web socket.
         * @return Web socket
         */
        public WebSocket getSocket() {
            return commandRequester.mWebSocket;
        }

        /**
         * Disconnect from all photo, video, listener, and stream connections.
         */
        public void end() {
            commandRequester.closePhotoConnection();
            commandRequester.closeVideoConnection();
            commandRequester.clearListenersAndState();
        }
    }

}
