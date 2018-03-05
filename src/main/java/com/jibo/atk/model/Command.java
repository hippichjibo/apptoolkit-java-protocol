package com.jibo.atk.model;

import com.google.gson.annotations.SerializedName;

/*
 * Created by Jibo, Inc. on 13.10.17.
 */

/**
 * Classes in the Jibo Command Protocol. 
 */
public class Command {

    /** 
     * Types of commands available. Please note that some are not currently supported.
     */
    public enum CommandType {
        @SerializedName("StartSession")
        StartSession,
        @SerializedName("GetConfig")
        GetConfig,
        @SerializedName("SetConfig")
        SetConfig,
        @SerializedName("Cancel")
        Cancel,
        @SerializedName("SetAttention")
        SetAttention,
        @SerializedName("Say")
        Say,
        @SerializedName("Listen")
        Listen,
        @SerializedName("LookAt")
        LookAt,
        @SerializedName("TakePhoto")
        TakePhoto,
        @SerializedName("Video")
        Video,
        @SerializedName("Display")
        Display,
        @SerializedName("FetchAsset")
        FetchAsset,
        @SerializedName("Motion")
        Motion,
        @SerializedName("ScreenGesture")
        ScreenGesture,
        @SerializedName("Entity")
        Entity,
        @SerializedName("Subscribe")
        Subscribe;
    }

    private Header.RequestHeader ClientHeader;
    private BaseCommand Command;

    /** HELP */
    public Command(Header.RequestHeader clientHeader, BaseCommand command) {
        ClientHeader = clientHeader;
        Command = command;
    }

    /** HELP */
    public Header.RequestHeader getClientHeader() {
        return ClientHeader;
    }

    /** HELP */
    public BaseCommand getCommand() {
        return Command;
    }

    /** Base class for command */
    static public class BaseCommand {
        private CommandType Type;

        private BaseCommand(CommandType type) {
            this.Type = type;
        }
        
        /** 
         * What type of command is this
         * @type {CommandType}
         */
        public CommandType getType() {
            return Type;
        }
    }
    /**
     * Types of streams
     */
    public enum StreamTypes {
        /**
         * `Entity` for face tracking
         */
        @SerializedName("Entity")
        Entity,
        /**
         * `Motion` for motion tracking
         */
        @SerializedName("Motion")
        Motion,
        /**
         * `Speech` for speech input
         */
        @SerializedName("Speech")
        Speech,
        /**
         * `HeadTouch` for head touch input
         */
        @SerializedName("HeadTouch")
        HeadTouch,
        /**
         * `ScreenGesture` for screen touch input
         */
        @SerializedName("ScreenGesture")
        ScreenGesture;
    }

    /** 
     * @hide
     */
    static public class BaseSubscribeCommand extends BaseCommand {

        protected StreamTypes StreamType;
        protected String StreamFilter;

        public BaseSubscribeCommand() {
            super(CommandType.Subscribe);
        }

        public StreamTypes getStreamType() {
            return StreamType;
        }

        public String getStreamFilter() {
            return StreamFilter;
        }

        public void setStreamType(StreamTypes type) {
            this.StreamType = type;
        }

        public void setStreamFilter(String filter) {
            this.StreamFilter = filter;
        }
    }

    /** Track motion */
    static public class MotionRequest extends BaseSubscribeCommand {
        /** Request to track motion in Jibo's perceptual space. */
        public MotionRequest() {
            this.StreamType = StreamTypes.Motion;
        }
    }

    /** Currently unsupported */
    static public class SpeechRequest extends BaseSubscribeCommand {
        @SerializedName("Listen")
        boolean listen;

        public SpeechRequest(boolean listen) {
            this.StreamType = StreamTypes.Speech;
            this.listen = listen;
        }
    }

    /** Class for head touch input */
    static public class HeadTouchRequest extends BaseSubscribeCommand {
        /** Request for Jibo to listen for a head touch. */
        public HeadTouchRequest() {
            this.StreamType = StreamTypes.HeadTouch;
        }
    }

    /** @hide */
    public interface AtomicCommand {}

    /** Currently unsupported */
    static public class AttentionRequest extends BaseCommand implements AtomicCommand {

        public enum AttentionMode {
            @SerializedName("OFF")
            Off,
            @SerializedName("IDLE")
            Idle,
            @SerializedName("DISENGAGE")
            Disengage,
            @SerializedName("ENGAGED")
            Engaged,
            @SerializedName("SPEAKING")
            Speaking,
            @SerializedName("FIXATED")
            Fixated,
            @SerializedName("ATTRACTABLE")
            Attractable,
            @SerializedName("MENU")
            Menu,
            @SerializedName("COMMAND")
            Command;
        }

        private AttentionMode Mode;

        public AttentionRequest(CommandType type, AttentionMode mode) {
            super(CommandType.SetAttention);
            Mode = mode;
        }

        public AttentionMode getMode() {
            return Mode;
        }
    }

    /** Cancel a command */
    static public class CancelRequest extends BaseCommand implements AtomicCommand {
        private String ID;

        /** 
         * Cancel a command. 
         * @param {string} id ID of the command to cancel 
         */
        public CancelRequest(String id) {
            super(CommandType.Cancel);
            this.ID = id;
        }

        /**
         * Get the ID of the command to cancel.
         */
        public String getID() {
            return ID;
        }
    }

    /** Currently unsupported */
    static public class EntityRequest extends BaseSubscribeCommand {

        public EntityRequest() {
            super();
            StreamType = StreamTypes.Entity;
        }
    }

    /** Get robot configuration info */
    static public class GetConfigRequest extends BaseCommand implements AtomicCommand {

        /** Request for Jibo's current configurations. */
        public GetConfigRequest() {
            super(CommandType.GetConfig);
        }
    }

    /** Set robot options */
    static public class SetConfigRequest extends BaseCommand implements AtomicCommand {

        /** Class for robot config options */
        public static class SetConfigOptions {
            @SerializedName("Mixer")
            private float mixer;

            /**
             * Robot configuration options that can be set by your app
             * @param mixer Robot volume between 0 (mute) and 1 (loudest) 
             */
            public SetConfigOptions(float mixer){
                this.mixer = mixer;
            }
        }

        @SerializedName("Options")
        private SetConfigOptions options;

        /** 
         * Request for setting config options
         * @param options {SetConfigOptions} Options to set. 
         */
        public SetConfigRequest(SetConfigOptions options) {
            super(CommandType.SetConfig);
            this.options = options;
        }
    }

    /** Listen for speech */
    static public class ListenRequest extends BaseCommand {

        @SerializedName("MaxSpeechTimeout")
        Long maxSpeechTimeout;
        @SerializedName("MaxNoSpeechTimeout")
        Long maxNoSpeechTimeout;
        @SerializedName("LanguageCode")
        String languageCode;

        /** 
         * Request for Jibo to listen for speech input.
         * @param {Long} maxSpeechTimeout Maximum amount of time Jibo should listen to speech. Default = 15. In seconds.
         * @param {Long} maxNoSpeechTimeout Maximum amount of time Jibo should wait for speech to begin. Default = 15. In seconds.
         * @param {string} languageCode Language to listen for. Right now only english (`en_US`) is supported. */
        public ListenRequest(Long maxSpeechTimeout, Long maxNoSpeechTimeout, String languageCode) {
            super(CommandType.Listen);
            this.maxSpeechTimeout = maxSpeechTimeout;
            this.maxNoSpeechTimeout = maxNoSpeechTimeout;
            this.languageCode = languageCode;
        }
    }

    /** Move Jibo */
    static public class LookAtRequest extends BaseCommand {
        private BaseLookAtTarget LookAtTarget;
        private Boolean TrackFlag;

        /** 
         * Request Jibo to look toward a specific point. 
         * @param {LookAtRequest.BaseLookAtTarget} lookAtTarget 	Where to make Jibo look
         * @param {boolean} trackFlag Currently unsupported.
         */
        public LookAtRequest(LookAtRequest.BaseLookAtTarget lookAtTarget, Boolean trackFlag) {
            super(CommandType.LookAt);
            LookAtTarget = lookAtTarget;
            TrackFlag = trackFlag;
        }

        /** Get the target to make Jibo look toward */
        public BaseLookAtTarget getLookAtTarget() {
            return LookAtTarget;
        }

        /** Currently unsupported */
        public Boolean getTrackFlag() {
            return TrackFlag;
        }

        /** Base class for LookAt Targets */
        static public class BaseLookAtTarget {
        }

        /**
         * Class for 3D position targets
         */
        static public class PositionTarget extends BaseLookAtTarget{
            private int[] Position;

            /**
             * Location for the base coordinate frame of the robot.
             *
             * Defined as {@code [x: meters forward, y: meters left, z: meters up]}
             */
            public PositionTarget(int[] position) {
                Position = position;
            }

            /** Get Jibo's current position */
            public int[] getPosition() {
                return Position;
            }
        }

        /**
         * Class for 2D angle targets
         */
        static public class AngleTarget extends BaseLookAtTarget{
            private int[] Angle;

            /**
             * Angles relative to Jibo’s current orientation.
             * Defined as {@code [theta: twist/horiz angle, psi: vert angle]}
             */
            public AngleTarget(int[] angle) {
                Angle = angle;
            }

            /** Get Jibo's current angle */
            public int[] getAngle() {
                return Angle;
            }
        }

        /**
         * Class for entity (face) targets. Currently unsupported
         */
        static public class EntityTarget extends BaseLookAtTarget{
            private Long Entity;

            /**
             * Currently unsupported.
             * An integer that refers to an entity that is known and available in Jibo’s LPS
             * system. An error will be returned if that entity is no longer being tracked.
             */
            public EntityTarget(Long entity) {
                Entity = entity;
            }

            /**
             * Currently unsupported
             */
            public Long getEntity() {
                return Entity;
            }
        }

        /**
         * Class for camera targets
         */
        static public class CameraTarget extends BaseLookAtTarget{
            private int[] ScreenCoords;

            /**
             * Location on Jibo's screen that the camera is targeting
             */
            public CameraTarget(int[] screenCoords) {
                ScreenCoords = screenCoords;
            }

            /**
             * Get the screen coordinate of where Jibo is targeting
             */
            public int[] getScreenCoords() {
                return ScreenCoords;
            }
        }

    }

    /** @hide */
    static public class LookAtRequestExt extends LookAtRequest {
        private Boolean LevelHeadFlag;

        public LookAtRequestExt(LookAtRequest.BaseLookAtTarget lookAtTarget, Boolean trackFlag, Boolean levelHeadFlag) {
            super(lookAtTarget, trackFlag);
            LevelHeadFlag = levelHeadFlag;
        }

        public Boolean getLevelHeadFlag() {
            return LevelHeadFlag;
        }
    }

    /** 
     * Making Jibo speak and play animations via embodied speech [(ESML)](https://app-toolkit.jibo.com/esml/).
    */
    static public class SayRequest extends BaseCommand {
        private String ESML;

        /** Make Jibo speak.
         * @param {string} ESML Straight string to speak or 
         * [ESML](https://app-toolkit.jibo.com/esml/) markup.
         */
        public SayRequest(String esml) {
            super(CommandType.Say);
            this.ESML = esml;
        }

        /** Get the string Jibo is speak */
        public String getESML() {
            return ESML;
        }
    }

    /** Start a command session */
    static public class SessionRequest extends BaseCommand implements AtomicCommand {

        /** Start a command session */
        public SessionRequest() {
            super(CommandType.StartSession);
        }
    }

    /** Take a photo */
    static public class TakePhotoRequest extends BaseCommand {

        /**
         * Camera options
         */
        public enum Camera {
            /**
             * Use `left` for photo-taking
             */
            @SerializedName("left")
            Left,
            /**
             * `right` Unsupported
             */
            @SerializedName("right")
            Right;
        }

        /**
         * Camera resolution options
         */
        public enum CameraResolution {
            /**
             * Currently unsupported.
             */
            @SerializedName("highRes")
            HighRes,
            /**
             * Better quality than default
             */
            @SerializedName("medRes")
            MedRes,
            /**
             * Default
             */
            @SerializedName("lowRes")
            LowRes,
            /**
             * Lower quality than default
             */
            @SerializedName("microRes")
            MicroRes;
        }

        private Camera Camera;
        private CameraResolution Resolution;
        private Boolean Distortion;

        /** Take a photo
         * @param {TakePhotoRequest.Camera} camera Which camera to use (left or right). Default = left.
         * @param {TakePhotoRequest.CameraResolution} resolution Resolution photo to take. Default = low.
         * @param {boolean} distortion 	`true` for regular lense. `false` for fisheye.
         */
        public TakePhotoRequest(TakePhotoRequest.Camera camera, TakePhotoRequest.CameraResolution resolution, Boolean distortion) {
            super(CommandType.TakePhoto);
            Camera = camera;
            Resolution = resolution;
            Distortion = distortion;
        }

        /** 
         * Which camera is being used (right or left). 
         * Should always be left.
         */
        public TakePhotoRequest.Camera getCamera() {
            return Camera;
        }

        /** Which resolution of photo is taken. */
        public TakePhotoRequest.CameraResolution getResolution() {
            return Resolution;
        }

        /** `true` for regular lense. `false` for fisheye.  */
        public Boolean getDistortion() {
            return Distortion;
        }
    }

    /** Capture a video */
    static public class VideoRequest extends BaseCommand {

        /**
         * Video type options
         */
        public enum VideoType {
            /**
             * Default
             */
            @SerializedName("NORMAL")
            Normal,
            /**
             * Currently unsupported
             */
            @SerializedName("DEBUG")
            Debug;
        }

        private VideoType VideoType;
        private Long Duration;

        /** @hide */
        public VideoRequest(VideoRequest.VideoType videoType) {
            super(CommandType.Video);
            VideoType = videoType;
            Duration = 0L;
        }

        /** 
         * Get a stream of what Jibo’s cameras see. 
         * Please note that this option does NOT record a video -- 
         * it provides a stream of camera information. 
         * @param {VideoRequest.VideoType} videoType Use `NORMAL`.
         * @param {Long} [duration Unsupported. Call `cancel()` to stop the stream.]
         */
        public VideoRequest(VideoRequest.VideoType videoType, Long duration) {
            super(CommandType.Video);
            VideoType = videoType;
            Duration = duration;
        }

        /** 
         * Should always be `NORMAL`.
         * @returns {VideoType}
         */
        public VideoRequest.VideoType getVideoType() {
            return VideoType;
        }

        /** Currently unsupported */
        public Long getDuration() {
            return Duration;
        }
    }

    /** Display something on Jibo's screen */
    static public class DisplayRequest extends BaseCommand {

        /**
         * What to display on Jibo's screen
         */
        public enum DisplayViewType {
            /**
             * Display Jibo's eye.
             * See {@link EyeView}
             */
            @SerializedName("Eye")
            Eye,
            /**
             * Display text.
             * See {@link TextView}
             */
            @SerializedName("Text")
            Text,
            /**
             * Display an image.
             * See {@link ImageView}
             */
            @SerializedName("Image")
            Image;
        }

        /**
         * Data object for image info
         */
        static public class ImageData {

            /** Name of asset in local cache */
            @SerializedName("name")
            public String name;
            /** URL to the image */
            @SerializedName("src")
            public String src;
            @SerializedName("set")
            String set;
        }


        /**
         * View to display on Jibo's screen
         */
        public static class DisplayView {

            @SerializedName("Type")
            DisplayViewType type;
            @SerializedName("Name")
            String name;

           /**
            * Display information
            * @param type See {@link DisplayViewType}
            * @param name Unique name of view
            */
            public DisplayView(DisplayViewType type, String name){
                this.type = type;
                this.name = name;
            }
        }

        /** Display text on Jibo's screen */
        static public class TextView extends DisplayView {
            @SerializedName("Text")
            String text;

            /**
             * Info for displaying text on Jibo's screen
             * @param name Unique name of view
             * @param text Text to display
             */
            public TextView(String name, String text) {
                super(DisplayViewType.Text, name);
                this.text = text;
            }
        }

        /** Display an image on Jibo's screen */
        static public class ImageView extends DisplayView {
            @SerializedName("Image")
            ImageData image;

            /**
             * Info for displaying an image on Jibo's screen
             * @param name Unique name of view
             * @param data See {@link ImageData}
             */
            public ImageView(String name, ImageData data) {
                super(DisplayViewType.Image, name);
                this.image = data;
            }
        }

        /** Display the eye on Jibo's screen */
        static public class EyeView extends DisplayView {
            /**
             * Info for displaying Jibo's eye
             * @param name Unique name of view
             */
            public EyeView(String name) {
                super(DisplayViewType.Eye, name);
                this.type = DisplayViewType.Eye;
            }
        }

        @SerializedName("View")
        private DisplayView view;

        /** Get the current view */
        public DisplayView getView() {
            return view;
        }

        /** 
         * Request something on Jibo's screen.
         * @param displayView {DisplayView} What to display onscreen.
         */
        public DisplayRequest(DisplayView displayView) {
            super(CommandType.Display);
            this.view = displayView;
        }
    }

    /** Fetch an asset */
    static public class FetchAssetRequest extends BaseCommand {

        @SerializedName("URI")
        private  String uri;
        @SerializedName("Name")
        private String name;

        /** 
         * Retrieve external asset and store in local cache by name
         * @param {string} uri URI to the asset to be fetched
         * @param {string} name Name the asset will be called by
         */
        public FetchAssetRequest(String uri, String name) {
            super(CommandType.FetchAsset);
            this.uri = uri;
            this.name = name;
        }

        /** Get the URI of the asset. */
        public String getUri() {
            return uri;
        }
        /** Get the name of the asset */
        public String getName() {
            return name;
        }

    }

    /** Listen for screen touch input **/
    static public class ScreenGestureRequest extends BaseCommand {

        /** Filters options for screen gestures */
        public static class ScreenGestureFilter {

            /**
             * Type of screen gesture
             */
            public enum ScreenGestureType {
                /**
                 * Tap
                 */
                Tap,
                /**
                 * Swipe from top to bottom
                 */
                SwipeDown,
                /**
                 * Swipe from bottom to top
                 */
                SwipeUp,
                /**
                 * Swipe from left to right
                 */
                SwipeRight,
                /**
                 * Swipe from right to left
                 */
                SwipeLeft;
            }

            /** Define an area on Jibo's screen. See {@link Rectangle} and {@link Circle} */
            public static class Area {
                float x;
                float y;
                /**
                 * Pixel on the screen in which to listen for screen gesture.
                 */
                public Area(float x, float y){
                    /** horizontal coordinate */
                    this.x = x;
                    /** vertical coordinate */
                    this.y = y;
                }
            }

            /** Define a rectangular area on Jibo's screen */
            public static class Rectangle extends Area{
                float width;
                float height;

                /**
                 * Rectangular area on the screen in which to listen for screen gesture
                 * where {@code (x,y) } is the top-left corner of the rectangle.
                 *  All params in pixels.
                 */
                public Rectangle(float x, float y, float width, float height) {
                    super(x, y);
                    this.width = width;
                    this.height = height;
                }
            }

            /** Define a circular area on Jibo's screen */
            public static class Circle extends Area{
                float radius;

                /**
                 * Circular area in which to listen for screen gesture
                 *  where {@code (x,y) } is the center of the circle.
                 *  All params in pixels.
                 */
                public Circle(float x, float y, float radius) {
                    super(x, y);
                    this.radius = radius;
                }
            }

            @SerializedName("Type")
            private ScreenGestureType type;

            @SerializedName("Area")
            private Area area;

            /**
             * Screen gesture information.
             * @param type Type of gesture to listen for
             * @param area Area to listen for gesture in
             */
            public ScreenGestureFilter(ScreenGestureType type, Area area){
                this.type = type;
                this.area = area;
            }


        }

        @SerializedName("StreamType")
        private StreamTypes streamType;

        @SerializedName("StreamFilter")
        private ScreenGestureFilter streamFilter;

        /** 
         * Request for Jibo to listen for screen touch input
         * @param {ScreenGestureFilter} filter Screen touch input options
         */
        public ScreenGestureRequest(ScreenGestureFilter filter) {
            super(CommandType.ScreenGesture);
            this.streamType = StreamTypes.ScreenGesture;
            this.streamFilter = filter;
        }
        /** 
         * What type of stream is this
         * @type {StreamTypes}
         */
        public StreamTypes getStreamType() {return streamType;}
        /** The screen touch options  */
        public ScreenGestureFilter getScreenGestureFilter() {return streamFilter;}
    }

}
