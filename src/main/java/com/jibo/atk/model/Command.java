package com.jibo.atk.model;

import com.google.gson.annotations.SerializedName;

/*
 * Created by alexz on 13.10.17.
 */

/**
 * Class for all Android command
 */
public class Command {

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

    public Command(Header.RequestHeader clientHeader, BaseCommand command) {
        ClientHeader = clientHeader;
        Command = command;
    }

    public Header.RequestHeader getClientHeader() {
        return ClientHeader;
    }

    public BaseCommand getCommand() {
        return Command;
    }

    static public class BaseCommand {
        private CommandType Type;

        private BaseCommand(CommandType type) {
            this.Type = type;
        }

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

    static public class MotionRequest extends BaseSubscribeCommand {

        public MotionRequest() {
            this.StreamType = StreamTypes.Motion;
        }
    }

    static public class SpeechRequest extends BaseSubscribeCommand {
        @SerializedName("Listen")
        boolean listen;

        public SpeechRequest(boolean listen) {
            this.StreamType = StreamTypes.Speech;
            this.listen = listen;
        }
    }

    static public class HeadTouchRequest extends BaseSubscribeCommand {

        public HeadTouchRequest() {
            this.StreamType = StreamTypes.HeadTouch;
        }
    }

    /*
     * We use this interface to mark commands that dont trigger events
     * This will allow us release their listeners as soon as acknowledgment arrives
     *
     */
    public interface AtomicCommand {}

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

    static public class CancelRequest extends BaseCommand implements AtomicCommand {
        private String ID;

        public CancelRequest(String id) {
            super(CommandType.Cancel);
            this.ID = id;
        }

        public String getID() {
            return ID;
        }
    }

    static public class EntityRequest extends BaseSubscribeCommand {

        public EntityRequest() {
            super();
            StreamType = StreamTypes.Entity;
        }
    }


    static public class GetConfigRequest extends BaseCommand implements AtomicCommand {
        public GetConfigRequest() {
            super(CommandType.GetConfig);
        }
    }

    static public class SetConfigRequest extends BaseCommand implements AtomicCommand {
        public static class SetConfigOptions {
            @SerializedName("Mixer")
            private float mixer;

            public SetConfigOptions(float mixer){
                this.mixer = mixer;
            }
        }

        @SerializedName("Options")
        private SetConfigOptions options;

        public SetConfigRequest(SetConfigOptions options) {
            super(CommandType.SetConfig);
            this.options = options;
        }
    }

    static public class ListenRequest extends BaseCommand implements AtomicCommand {

        @SerializedName("MaxSpeechTimeout")
        Long maxSpeechTimeout;
        @SerializedName("MaxNoSpeechTimeout")
        Long maxNoSpeechTimeout;
        @SerializedName("LanguageCode")
        String languageCode;

        public ListenRequest(Long maxSpeechTimeout, Long maxNoSpeechTimeout, String languageCode) {
            super(CommandType.Listen);
            this.maxSpeechTimeout = maxSpeechTimeout;
            this.maxNoSpeechTimeout = maxNoSpeechTimeout;
            this.languageCode = languageCode;
        }
    }


    static public class LookAtRequest extends BaseCommand {
        private BaseLookAtTarget LookAtTarget;
        private Boolean TrackFlag;

        public LookAtRequest(LookAtRequest.BaseLookAtTarget lookAtTarget, Boolean trackFlag) {
            super(CommandType.LookAt);
            LookAtTarget = lookAtTarget;
            TrackFlag = trackFlag;
        }

        public BaseLookAtTarget getLookAtTarget() {
            return LookAtTarget;
        }

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

            public PositionTarget(int[] position) {
                Position = position;
            }

            /**
             * Get the location for the base coordinate frame of the robot.
             * @return Position 3-number array for location, defined as `[x: meters forward, y: meters left, z: meters up]`
             */
            public int[] getPosition() {
                return Position;
            }
        }

        /**
         * Class for 2D angle targets
         */
        static public class AngleTarget extends BaseLookAtTarget{
            private int[] Angle;

            public AngleTarget(int[] angle) {
                Angle = angle;
            }

            /**
             * Get the look at location relative to Jibo's current position.
             * @return Angle 2-number array, defined as `[theta: twist/horiz angle, psi: vert angle]`
             */
            public int[] getAngle() {
                return Angle;
            }
        }

        /**
         * Class for entity (face) targets. Currently unsupported
         */
        static public class EntityTarget extends BaseLookAtTarget{
            private Long Entity;

            public EntityTarget(Long entity) {
                Entity = entity;
            }

            /**
             * Currently unsupported.
             * Get the id of the face being tracked.
             * @return Entity An integer that refers to an entity that is known and available in Jibo’s LPS
             * system. An error will be returned if that entity is no longer being tracked.
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

            public CameraTarget(int[] screenCoords) {
                ScreenCoords = screenCoords;
            }

            /**
             * Get the location on Jibo's screen that the camera is targeting
             * @return ScreenCoords 2-number array for location on Jibo's screen
             */
            public int[] getScreenCoords() {
                return ScreenCoords;
            }
        }

    }

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

    static public class SayRequest extends BaseCommand {
        private String ESML;

        public SayRequest(String esml) {
            super(CommandType.Say);
            this.ESML = esml;
        }

        public String getESML() {
            return ESML;
        }
    }

    static public class SessionRequest extends BaseCommand implements AtomicCommand {
        public SessionRequest() {
            super(CommandType.StartSession);
        }
    }

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
             * `right` reserved for face tracking
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

        public TakePhotoRequest(TakePhotoRequest.Camera camera, TakePhotoRequest.CameraResolution resolution, Boolean distortion) {
            super(CommandType.TakePhoto);
            Camera = camera;
            Resolution = resolution;
            Distortion = distortion;
        }

        public TakePhotoRequest.Camera getCamera() {
            return Camera;
        }

        public TakePhotoRequest.CameraResolution getResolution() {
            return Resolution;
        }

        public Boolean getDistortion() {
            return Distortion;
        }
    }

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

        public VideoRequest(VideoRequest.VideoType videoType) {
            super(CommandType.Video);
            VideoType = videoType;
            Duration = 0L;
        }

        public VideoRequest(VideoRequest.VideoType videoType, Long duration) {
            super(CommandType.Video);
            VideoType = videoType;
            Duration = duration;
        }

        public VideoRequest.VideoType getVideoType() {
            return VideoType;
        }

        public Long getDuration() {
            return Duration;
        }
    }

    static public class DisplayRequest extends BaseCommand {

        /**
         * Type of thing to display on Jibo's screen
         */
        public enum DisplayViewType {
            /**
             * `Eye` Display Jibo's eye
             */
            @SerializedName("Eye")
            Eye,
            /**
             * `Eye` Display text
             */
            @SerializedName("Text")
            Text,
            /**
             * `Eye` Display an image
             */
            @SerializedName("Image")
            Image;
        }

        /**
         * Data object for image info
         */
        static public class ImageData {
            /**
             * Name of asset in local cache
             */
            @SerializedName("name")
            String name;
            /**
             * URL to the image
             */
            @SerializedName("src")
            String src;
            @SerializedName("set")
            String set;
        }


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

        public DisplayView getView() {
            return view;
        }

        public DisplayRequest(DisplayView displayView) {
            super(CommandType.Display);
            this.view = displayView;
        }
    }

    static public class FetchAssetRequest extends BaseCommand {

        @SerializedName("URI")
        private  String uri;
        @SerializedName("Name")
        private String name;

        public FetchAssetRequest(String uri, String name) {
            super(CommandType.FetchAsset);
            this.uri = uri;
            this.name = name;
        }

        public String getUri() {
            return uri;
        }
        public String getName() {
            return name;
        }

    }

    static public class ScreenGestureRequest extends BaseCommand {

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
                 * SwipeDown
                 */
                SwipeDown,
                /**
                 * SwipeUp
                 */
                SwipeUp,
                /**
                 * SwipeRight
                 */
                SwipeRight,
                /**
                 * SwipeLeft
                 */
                SwipeLeft;
            }

            public static class Area {
                float x;
                float y;
                /**
                 * Pixel on the screen in which to listen for screen gesture.
                 * `[x, y]`
                 */
                public Area(float x, float y){
                    this.x = x;
                    this.y = y;
                }
            }


            public static class Rectangle extends Area{
                float width;
                float height;

                /**
                 * Rectanglar area on the screen in which to listen for screen gesture
                 * `[x, y, width, height]`
                 */
                public Rectangle(float x, float y, float width, float height) {
                    super(x, y);
                    this.width = width;
                    this.height = height;
                }
            }

            public static class Circle extends Area{
                float radius;

                /**
                 * Circular area in which to listen for screen gesture
                 * `[x, y, radius]` Where `(x,y)` is the center of the circle
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

        public ScreenGestureRequest(ScreenGestureFilter filter) {
            super(CommandType.ScreenGesture);
            this.streamType = StreamTypes.ScreenGesture;
            this.streamFilter = filter;
        }

        public StreamTypes getStreamType() {return streamType;}
        public ScreenGestureFilter getScreenGestureFilter() {return streamFilter;}
    }

}
