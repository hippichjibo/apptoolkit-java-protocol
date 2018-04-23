package com.jibo.apptoolkit.protocol.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jibo.apptoolkit.protocol.model.EventMessage.EventType.*;

/*
 * Created by Jibo, Inc. on 10.10.17.
 */
/** Information returned with command messages */
public class EventMessage extends BaseResponse {

    /** Enum of events */
    public enum EventType {
        /** `onStart` */
        @SerializedName("onStart")
        Start,
        /** `onStop` */
        @SerializedName("onStop")
        Stop,
        /** `onError` */
        @SerializedName("onError")
        Error,
        /** `onLookAtAchieved` */
        @SerializedName("onLookAtAchieved")
        LookAtAchieved,
        /** `onTrackEntityLost` */
        @SerializedName("onTrackEntityLost")
        TrackEntityLost,
        /** `onVideoReady` */
        @SerializedName("onVideoReady")
        VideoReady,
        /** `onEntityUpdate` */
        @SerializedName("onEntityUpdate")
        TrackUpdate,
        /** `onEntityLost` */
        @SerializedName("onEntityLost")
        TrackLost,
        /** `onEntityGained` */
        @SerializedName("onEntityGained")
        TrackGained,
        /** `onTakePhoto` */
        @SerializedName("onTakePhoto")
        TakePhoto,
        /** `onTap` */
        @SerializedName("onTap")
        Tap,
        /** `onSwipe` */
        @SerializedName("onSwipe")
        Swipe,
        /** `onHotWordHeard` */
        @SerializedName("onHotWordHeard")
        HotWordHeard,
        /** `onListenResult` */
        @SerializedName("onListenResult")
        ListenResult,
        /** `onMotionDetected` */
        @SerializedName("onMotionDetected")
        MotionDetected,
        /** `onAssetFailed` */
        @SerializedName("onAssetFailed")
        AssetFailed,
        /** `onAssetReady` */
        @SerializedName("onAssetReady")
        AssetReady,
        /** `onHeadTouch` */
        @SerializedName("onHeadTouch")
        HeadTouched;
    }

    private EventMessage.EventHeader EventHeader;
    private BaseEvent EventBody;

    /**
     * @hide
     */
    public EventMessage(EventMessage.EventHeader eventHeader, BaseEvent eventBody) {
        EventHeader = eventHeader;
        EventBody = eventBody;
    }
    /** @hide */
    public EventMessage.EventHeader getEventHeader() {
        return EventHeader;
    }
    /** @hide */
    public BaseEvent getEventBody() {
        return EventBody;
    }
    /** @hide */
    public void setEventBody(BaseEvent eventBody) {
        EventBody = eventBody;
    }

    /****************DEFINING EVENT PARTS HERE****************/

    /** @hide */
    public class EventHeader extends Header.ResponseHeader {
        private float Timestamp;
        /** @hide */
        public float getTimestamp() {
            return Timestamp;
        }

    }

    /****************DEFINING EVENT TYPES HERE****************/
    /** Generic event information */
    static public class BaseEvent {
        protected EventType Event;
        /** @hide */
        public EventType getEvent() {
            return Event;
        }
    }
    /** @hide */
    public interface FinalisingEvent {}
    /** @hide */
    static public class StartEvent extends BaseEvent implements FinalisingEvent {}
    /** @hide */
    static public class StopEvent extends BaseEvent implements FinalisingEvent {}
    /** Class for error events */
    static public class ErrorEvent extends BaseEvent implements FinalisingEvent {
        private ErrorData EventError;

        /** Get info for the error that occured
         * @return EventError {@link ErrorData}
         */
        public ErrorData getEventError() {
            return EventError;
        }

        /** Class for error data info */
        static public class ErrorData {
            private int ErrorCode;
            private String ErrorString;

            /** @return Error code string */
            public int getErrorCode() {
                return ErrorCode;
            }

            /** @return  Error description string */
            public String getErrorString() {
                return ErrorString;
            }
        }
    }

    /**
     * Currently unsupported <br />
     * Class for face tracking events. 
     */
    static public class EntityTrackEvent extends BaseEvent {

        private EventType type;
        private TrackedEntity[] Tracks;

        /**
         * Currently unsupported <br />
         * 
         * When Jibo sees a face, they're either a known loop member or unknown.
         */
        public enum EntityType {
            /** Face seen is a known loop member */
            @SerializedName("person")
            Person,
            /** Face seen is not a loop member */
            @SerializedName("unknown")
            Unknown;
        }


        public EventType getType() {
            return type;
        }

        /**
         * Currently unsupported <br />
         * 
         * Get the tracks in Jibo's perceptual space
         * @return Tracks
         */
        public TrackedEntity[] getTracks() {
            return Tracks;
        }

        /**
         * Currently unsupported <br />
         * Info for tracking a face
         */
        static public class TrackedEntity {
            private Long EntityID;
            private EntityType Type;
            private float Confidence;
            private float[] WorldCoords;
            private int[] ScreenCoords;

            /**
             * Currently unsupported <br />
             * Get the ID of the tracked face
             * @return EntityID
             */
            public Long getEntityID() {
                return EntityID;
            }

            /**
             * Currently unsupported <br />
             * Get the type of track (loop member or unknown)
             * @return Type
             * */
            public EntityType getType() {
                return Type;
            }

            /**
             * Currently unsupported <br />
             * Get Jibo's confidence in his identifcation of the person
             * @return Confidence `int` [0,1]
             */
            public float getConfidence() {
                return Confidence;
            }

            /**
             * Currently unsupported <br />
             * 3-number array in space where the face exists
             * @return WorldCoords `[x: meters forward, y: meters left, z: meters up]`
             */
            public float[] getWorldCoords() {
                return WorldCoords;
            }

            /**
             * Currently unsupported <br />
             * Point in Jibo's field of vision where face currently exists
             * @return ScreenCoords`[x,y]`
             */
            public int[] getScreenCoords() {
                return ScreenCoords;
            }
        }
    }

    /**
     * Jibo achieved his lookat command <br />
     *
     * {@link EventMessage.EventType} = {@code onLookAtAchieved}
     */
    static public class LookAtAchievedEvent extends BaseEvent implements FinalisingEvent {
        private int[] PositionTarget;
        private int[] AngleTarget;

        /**
         * Returns the location that was achieved in absolute space. <br />
         * {@code [x: meters forward, y: meters left, Z: meters forward}
         */
        public int[] getPositionTarget() {
            return PositionTarget;
        }

        /**
         * Returns the location that was achieved in angle space <br />
         *  {@code [theta: horizontal/twist angle, psi: vertical angle}
         */
        public int[] getAngleTarget() {
            return AngleTarget;
        }
    }

    /** Jibo lost sight of the thing he was tracking */
    static public class LookAtTrackLostEvent extends BaseEvent implements FinalisingEvent {
        private int[] PositionTarget;
        private int[] AngleTarget;
        private long EntityTarget;

        /** Returns the absolute position when the track was lost */
        public int[] getPositionTarget() {
            return PositionTarget;
        }

        /** Returns the angle position when the track was lost */
        public int[] getAngleTarget() {
            return AngleTarget;
        }

        /** Returns the entity that was lost */
        public long getEntityTarget() {
            return EntityTarget;
        }
    }

    /** The video stream is ready to capture */
    static public class VideoReadyEvent extends BaseEvent implements FinalisingEvent {
        private String URI;

        /** 
         * Get the location of the video
         * @return  URI The URI to the video
         */
        public String getURI() {
            return URI;
        }
    }

    /**
     * Jibo took a photo
     */
    static public class TakePhotoEvent extends BaseEvent implements FinalisingEvent {
        private String URI;
        private String Name;
        private int[] PositionTarget;
        private int[] AngleTarget;

        /**
         * Get the location of the photo
         * @return URI to the photo
         */
        public String getURI() {
            return URI;
        }

        /**
         * Get the name of the photo
         * @return Name Unique name of the photo in local cache
         */
        public String getName() {
            return Name;
        }

        /**
         * Get the location of what Jibo photographed relative to Jibo's global position.
         * @return PositionTarget {@code [x: meters forward, y: meters left, z: meters up]}
         */
        public int[] getPositionTarget() {
            return PositionTarget;
        }

        /** 
         * Get the angle location of what Jibo photographed relative to Jibo's orientation.
         * @return AngleTarget {@code [theta: twist/horiz angle, psi: vert angle]}
         */
        public int[] getAngleTarget() {
            return AngleTarget;
        }
    }

    /** 
     * Enum of screen gesture events
     */
    public enum ScreenGestureEvents {
        /** `onTap` for tap events. See {@link JiboTapEvent} */
        @SerializedName("onTap")
        Tap,
        /** `onSwipe` for swipe events. See {@link JiboSwipeEvent} */
        @SerializedName("onSwipe")
        Swipe
    }

    /** Someone tapped Jibo's screen */
    public static class JiboTapEvent extends BaseEvent {

        @SerializedName("Coordinate")
        private int[] coordinate;

        /** 
         * Get location of the tap
         * @return coordinate {@code [x: horz coord, y: vert coord]} in pixels
         */
        public int[] getCoordinate() {
            return coordinate;
        }

    }

    /** Someone swiped on Jibo's screen */
    public static class JiboSwipeEvent extends BaseEvent {

        /** Enum of swipe directions */
        public enum SwipeDirection {
            /** Someone swiped from bottom to top */
            Up,
            /** Someone swiped from top to bottom */
            Down,
            /** Someone swiped from user left to user right */
            Right,
            /** Someone swiped from user right to user left */
            Left;
        }

        @SerializedName("Direction")
        private SwipeDirection direction;

        /**
         * Get the swipe direction
         * @return direction See {@link SwipeDirection}
         */
        public SwipeDirection getSwipeDirection() {
            return direction;
        }

    }

    /** Info for when Jibo stops listening */
    static public class ListenStopEvent extends StopEvent {

        /** Enum of reasons why Jibo stopped listening */
        public enum ListenStopReason {
            /** `maxNoSpeech` means Jibo timed out without hearing any speech */
            @SerializedName("maxNoSpeech")
            MaxNoSpeech,
            /** `maxSpeech` means Jibo timed out while listening.  */
            @SerializedName("maxSpeech")
            MaxSpeech
        }

        @SerializedName("ListenStopReason")
        private ListenStopReason stopReason;

        public ListenStopEvent(ListenStopReason reason){
            this.stopReason = reason;
        }
    }

    /** Info about what Jibo heard */
    public static class ListenResultEvent extends BaseEvent implements FinalisingEvent {

        @SerializedName("LanguageCode")
        private String languageCode;

        @SerializedName("Speech")
        private String speech;

        @SerializedName("Result")
        private String result;

        /** What language Jibo heard. Right now only English is supported */
        public String getLanguageCode() {
            return languageCode;
        }
        /** String of what Jibo heard */
        public String getSpeech() {
            return speech;
        }
        /** @hide */
        public String getResult() {
            return result;
        }
    }

    /** Info about motion Jibo saw */
    static public class MotionEvent extends BaseEvent {

        /** Info for motion tracking */
        public static class MotionEntity {
            /** Intensity of the motion from 0-1 */
            @SerializedName("Intensity")
            public Float intensity;
            /** 3D global position of the motion
             * <br /> {@code [x: meters forward, y: meters left, z: meters up]}
             */
            @SerializedName("WorldCoords")
            public float[] worldCoords;
            /** 2D screen position of the motion
             * <br /> {@code [x: horiz coord, y: vert coord]}
             */
            @SerializedName("ScreenCoords")
            public float[] ScreenCoords;
        }

        @SerializedName("Motions")
        MotionEntity[] motions;

        /** Get into for any motion Jibo sees
         * @return motions {@link MotionEntity} info for all motions seen.*/
        public MotionEntity[] getMotions() {
            return motions;
        }
    }

    /** Currently unsupported */
    public static class HotWordHeardEvent extends BaseEvent {

        /** @hide */
        public static class LPSPosition {
            @SerializedName("Position")
            private int[] position;
            @SerializedName("AngleVector")
            private int[] angleVector;
            @SerializedName("Confidence")
            float confidence;
        }

        /** @hide */
        public static class SpeakerId {
            @SerializedName("Type")
            EntityTrackEvent.EntityType type;
            @SerializedName("Confidence")
            float confidence;
        }

        /** @hide */
        public static class Speaker {
            @SerializedName("LPSPosition")
            LPSPosition lpsPosition;
            @SerializedName("SpeakerID")
            SpeakerId speakerId;
        }

        @SerializedName("Speaker")
        private Speaker speaker;

        /** @hide */
        public Speaker getSpeaker() {
            return speaker;
        }
    }

    /** Info for getting an asset */
    static public class FetchAssetEvent extends BaseEvent {
        /** Enum of events related to getting assets */
        public enum FetchAssetEvents {
            /** `onAssetReady` when the asset is ready to use */
            @SerializedName("onAssetReady")
            AssetReady,
            /** `onAssetFailed` if we fail to load the asset */
            @SerializedName("onAssetFailed")
            AssetFailed
        }

        @SerializedName("Event")
        private FetchAssetEvents event;
        @SerializedName("Detail")
        private String detail;

        /**
         * Get the fetch event
         * @return event See {@link EventMessage.FetchAssetEvent.FetchAssetEvents}
         */
        public FetchAssetEvents getFetchEvent() {
            return event;
        }
        /**
         * Get the details about the asset
         * @return detail
         */
        public String getDetail() {
            return detail;
        }
    }

    /** 
     * Info for head touch events
     * <br /> See <a href="https://app-toolkit.jibo.com/images/JiboHeadSensors.png">Head Touch Sensors</a> for a diagram.
     */
    private static class JiboHeadTouchEvent extends BaseEvent {

        /** Events fired if any one of Jibo's head touch sensors is touched */
        public enum HeadTouchEvents {
            /** `onHeadTouch` */
            @SerializedName("onHeadTouch")
            HeadTouched
        }

        /**
         * There are 6 touch sensors on the back of Jibo’s head. <br />
         * Three run down each side of his head. <br />
         * Left is Jibo’s left and right is Jibo’s right.<br />
         * See <a href="https://app-toolkit.jibo.com/images/JiboHeadSensors.png">Head Touch Sensors</a> for a diagram.
         */
        public enum HeadTouchPads {
            /** front left pad */
            frontLeft,
            /** front right pad */
            middleLeft,
            /** back left pad */
            backLeft,
            /** front right pad */
            frontRight,
            /** middle right pad */
            middleRight,
            /** back right pad */
            backRight
        }

        @SerializedName("Pads")
        private boolean[] pads;

        /**
         * Details about the head touch Jibo got
         * @return pads Array of six head touch pad booleans. `true` if touched.
         */
        public boolean[] getDetail() {
            return pads;
        }
    }


    /** Someone tapped Jibo's screen */
    public static class TapEvent extends BaseEvent {

        @SerializedName("gesture")
        private JiboTapEvent gesture;

        private TapEvent(JiboTapEvent event) {
            gesture = event;
            Event = event.Event;
            gesture.Event = null;
        }

        public JiboTapEvent getGesture() {
            return gesture;
        }

    }

    /** Someone swiped on Jibo's screen */
    public static class SwipeEvent extends BaseEvent {

        @SerializedName("gesture")
        private JiboSwipeEvent gesture;

        private SwipeEvent(JiboSwipeEvent event) {
            gesture = event;
            Event = gesture.Event;
            gesture.Event = null;
        }

        public JiboSwipeEvent getGesture() {
            return gesture;
        }

    }

    /** Info about what Jibo heard */
    public static class ListenEvent extends BaseEvent implements FinalisingEvent {

        private ListenResultEvent listen;

        private ListenEvent(ListenResultEvent listen) {
            this.listen = listen;
            Event = listen.Event;
            listen.Event = null;
        }

        public ListenResultEvent getListen() {
            return listen;
        }
    }

    /** Info for head touch events */
    public static class HeadTouchEvent extends BaseEvent {

        @SerializedName("HeadSensors")
        private HeadSensors headSensors;

        private HeadTouchEvent(JiboHeadTouchEvent event) {
            Event = event.Event;

            boolean[] sensors = event.getDetail();
            headSensors = new HeadSensors();
            headSensors.leftFront = sensors[0];
            headSensors.leftMiddle = sensors[1];
            headSensors.leftBack = sensors[2];
            headSensors.rightFront = sensors[3];
            headSensors.rightMiddle = sensors[4];
            headSensors.rightBack = sensors[5];
        }

        public HeadSensors getHeadSensors() {
            return headSensors;
        }

        public static class HeadSensors {

            @SerializedName("leftFront")
            private Boolean leftFront;

            @SerializedName("leftMiddle")
            private Boolean leftMiddle;

            @SerializedName("leftBack")
            private Boolean leftBack;

            @SerializedName("rightFront")
            private Boolean rightFront;

            @SerializedName("rightMiddle")
            private Boolean rightMiddle;

            @SerializedName("rightBack")
            private Boolean rightBack;

        }

    }

    /****************THIS IS THE CLASS THAT MAPS RECEIVED JSON INTO FINAL EVENTMESSAGE****************/
    /** @hide */
    static public class EventFactory {

        private static final String TAG = EventFactory.class.getSimpleName();

        static private Map<EventType, Class> mEventsMap = new HashMap<>();

        static {
            mEventsMap.put(Start, StartEvent.class);
            mEventsMap.put(Stop, StopEvent.class);
            mEventsMap.put(Error, ErrorEvent.class);
            mEventsMap.put(LookAtAchieved, LookAtAchievedEvent.class);
            mEventsMap.put(TrackEntityLost, LookAtTrackLostEvent.class);
            mEventsMap.put(VideoReady, VideoReadyEvent.class);
            mEventsMap.put(TrackUpdate, EntityTrackEvent.class);
            mEventsMap.put(TrackGained, EntityTrackEvent.class);
            mEventsMap.put(TrackLost, EntityTrackEvent.class);
            mEventsMap.put(TakePhoto, TakePhotoEvent.class);
            mEventsMap.put(Tap, JiboTapEvent.class);
            mEventsMap.put(Swipe, JiboSwipeEvent.class);
            mEventsMap.put(ListenResult, ListenResultEvent.class);
            mEventsMap.put(MotionDetected, MotionEvent.class);
            mEventsMap.put(HotWordHeard, HotWordHeardEvent.class);
            mEventsMap.put(AssetReady, FetchAssetEvent.class);
            mEventsMap.put(AssetFailed, FetchAssetEvent.class);
            mEventsMap.put(HeadTouched, JiboHeadTouchEvent.class);
        }

        public EventMessage parseEventMessage(String json) {
            EventMessage eventMessage = null;
            Gson gson = new GsonBuilder().serializeNulls().create();

            try {
                JSONObject jsonObject = new JSONObject(json);
                eventMessage = gson.fromJson(json, EventMessage.class);

                if (mEventsMap.containsKey(eventMessage.EventBody.Event)) {

                    BaseEvent event = (BaseEvent) gson.fromJson(jsonObject.getJSONObject("EventBody").toString(),
                            mEventsMap.get(eventMessage.EventBody.Event));

                    event = convert(event);

                    eventMessage.setEventBody(event);

                    return eventMessage;
                }
            } catch (Exception e) {
              System.out.print(e.getLocalizedMessage());
            }

            return eventMessage;
        }

        private BaseEvent convert(BaseEvent event) {

            if (event instanceof JiboSwipeEvent) return new SwipeEvent((JiboSwipeEvent) event);

            if (event instanceof JiboTapEvent) return new TapEvent((JiboTapEvent) event);
            
            if (event instanceof ListenResultEvent) return new ListenEvent((ListenResultEvent) event);

            if (event instanceof JiboHeadTouchEvent) return new HeadTouchEvent((JiboHeadTouchEvent) event);

            if (event instanceof EntityTrackEvent) {
                ((EntityTrackEvent) event).type = event.Event;
            }

            return event;
        }
    }

}
