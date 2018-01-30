package com.jibo.atk.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.jibo.atk.model.EventMessage.EventType.*;

/*
 * Created by alexz on 10.10.17.
 */
/** Information returned with command messages */
public class EventMessage extends BaseResponse {

    public enum EventType {
        @SerializedName("onStart")
        Start,
        @SerializedName("onStop")
        Stop,
        @SerializedName("onError")
        Error,
        @SerializedName("onLookAtAchieved")
        LookAtAchieved,
        @SerializedName("onTrackEntityLost")
        TrackEntityLost,
        @SerializedName("onVideoReady")
        VideoReady,
        @SerializedName("onEntityUpdate")
        TrackUpdate,
        @SerializedName("onEntityLost")
        TrackLost,
        @SerializedName("onEntityGained")
        TrackGained,
        @SerializedName("onTakePhoto")
        TakePhoto,
        @SerializedName("onTap")
        Tap,
        @SerializedName("onSwipe")
        Swipe,
        @SerializedName("onHotWordHeard")
        HotWordHeard,
        @SerializedName("onListenResult")
        ListenResult,
        @SerializedName("onMotionDetected")
        MotionDetected,
        @SerializedName("onAssetFailed")
        AssetFailed,
        @SerializedName("onAssetReady")
        AssetReady,
        @SerializedName("onHeadTouch")
        HeadTouched;
    }

    private EventMessage.EventHeader EventHeader;
    private BaseEvent EventBody;

    public EventMessage(EventMessage.EventHeader eventHeader, BaseEvent eventBody) {
        EventHeader = eventHeader;
        EventBody = eventBody;
    }

    public EventMessage.EventHeader getEventHeader() {
        return EventHeader;
    }

    public BaseEvent getEventBody() {
        return EventBody;
    }

    public void setEventBody(BaseEvent eventBody) {
        EventBody = eventBody;
    }

    /****************DEFINING EVENT PARTS HERE****************/

    public class EventHeader extends Header.ResponseHeader {
        private float Timestamp;

        public float getTimestamp() {
            return Timestamp;
        }

    }

    /****************DEFINING EVENT TYPES HERE****************/

    static public class BaseEvent {
        protected EventType Event;

        public EventType getEvent() {
            return Event;
        }
    }

    public interface FinalisingEvent {}

    static public class StartEvent extends BaseEvent implements FinalisingEvent {}
    static public class StopEvent extends BaseEvent implements FinalisingEvent {}

    static public class ErrorEvent extends BaseEvent implements FinalisingEvent {
        private ErrorData EventError;

        public ErrorData getEventError() {
            return EventError;
        }

        static public class ErrorData {
            private int ErrorCode;
            private String ErrorString;

            public int getErrorCode() {
                return ErrorCode;
            }

            public String getErrorString() {
                return ErrorString;
            }
        }
    }

    /**
     * Class for face tracking events. Currently unsupported.
     */
    static public class EntityTrackEvent extends BaseEvent {

        private TrackedEntity[] Tracks;

        /**
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

        /** 
         * Get the tracks in Jibo's perceptual space
         * @return Tracks
         */
        public TrackedEntity[] getTracks() {
            return Tracks;
        }

        /**
         * Info for tracking a face
         */
        static public class TrackedEntity {
            private Long EntityID;
            private EntityType Type;
            private int Confidence;
            private int[] WorldCoords;
            private int[] ScreenCoords;

            /**
             * Get the ID of the tracked face
             * @return EntityID
             */
            public Long getEntityID() {
                return EntityID;
            }

            /**
             * Get the type of track (loop member or unknown)
             * @return Type
             * */
            public EntityType getType() {
                return Type;
            }

            /**
             * Get Jibo's confidence in his identifcation of the person
             * @return Confidence `int` [0,1]
             */
            public int getConfidence() {
                return Confidence;
            }

            /**
             * 3-number array in space where the face exists
             * @return WorldCoords `[x: meters forward, y: meters left, z: meters up]`
             */
            public int[] getWorldCoords() {
                return WorldCoords;
            }

            /**
             * Point in Jibo's field of vision where face currently exists
             * @return ScreenCoords`[x,y]`
             */
            public int[] getScreenCoords() {
                return ScreenCoords;
            }
        }
    }

    /** Jibo achieved his lookat command */
    static public class LookAtAchievedEvent extends BaseEvent implements FinalisingEvent {
        private int[] PositionTarget;
        private int[] AngleTarget;

        /**
         * Returns the location that was achieved in absolute space.
         */
        public int[] getPositionTarget() {
            return PositionTarget;
        }
        /** Returns the location that was achieved in angle space */
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
         * Get the location of what Jibo photographed
         * @return PositionTarget 3-number array for location, defined as `[x: meters forward, y: meters left, z: meters up]`
         */
        public int[] getPositionTarget() {
            return PositionTarget;
        }

        /** 
         * Get the location of what Jibo photographed
         * @return AngleTarget 2-number array, defined as `[theta: twist/horiz angle, psi: vert angle]`
         */
        public int[] getAngleTarget() {
            return AngleTarget;
        }
    }

    /** 
     * Enum of screen gesture events
     */
    public enum ScreenGestureEvents {
        /** `onTap` for tap events */
        @SerializedName("onTap")
        Tap,
        /** `onSwipe` for swipe events */
        @SerializedName("onSwipe")
        Swipe
    }

    /** Someone tapped Jibo's screen */
    static public class TapEvent extends BaseEvent {

        @SerializedName("Event")
        private ScreenGestureEvents event;

        @SerializedName("Coordinate")
        private int[] coordinate;

        /** 
         * Get the tap event info
         * @return event 
         */
        public ScreenGestureEvents getGestureEvent() {
            return event;
        }
        /** 
         * Get location of the tap
         * @return coorindate `(x,y)`
         */
        public int[] getCoordinate() {
            return coordinate;
        }

    }

    /** Someone swiped on Jibo's screen */
    static public class SwipeEvent extends BaseEvent {
        /** Enum of swipe directions */
        public enum SwipeDirection {
            /** someone swiped from bottom to top */
            Up,
            /** someone swiped from top to bottom*/
            Down,
            /** someone swiped from user left to user right */
            Right,
            /** someone swiped from user right to user left */
            Left;
        }

        @SerializedName("Event")
        private ScreenGestureEvents event;

        @SerializedName("Direction")
        private SwipeDirection direction;

        /** 
         * Get the swipe event info
         * @return event 
         */
        public ScreenGestureEvents getGestureEvent() {
            return event;
        }
        /** 
         * Get the swipe direction
         * @return direction
         */
        public SwipeDirection getSwipeDirection() {
            return direction;
        }

    }



    static public class ListenStopEvent extends StopEvent {

        public enum ListenStopReason {
            @SerializedName("maxNoSpeech")
            MaxNoSpeech,
            @SerializedName("maxSpeech")
            MaxSpeech
        }

        @SerializedName("ListenStopReason")
        private ListenStopReason stopReason;

        public ListenStopEvent(ListenStopReason reason){
            this.stopReason = reason;
        }
    }

    static public class ListenResultEvent extends BaseEvent {

        @SerializedName("LanguageCode")
        private String languageCode;

        @SerializedName("Speech")
        private String speech;

        @SerializedName("Result")
        private String result;

        public String getLanguageCode() {
            return languageCode;
        }
        public String getSpeech() {
            return speech;
        }
        public String getResult() {
            return result;
        }
    }

    static public class MotionEvent extends BaseEvent {
        /** Info for motion tracking */
        public static class MotionEntity {
            /** Intensity of the motion from 0-1 */
            @SerializedName("Intensity")
            float intensity;
            /** 3D global position of the motion */
            @SerializedName("WorldCoords")
            float[] worldCoords;
            /** 2D screen position of the motion */
            @SerializedName("ScreenCoords")
            float[] ScreenCoords;
        }

        @SerializedName("Motions")
        private MotionEntity[] motions;

        public MotionEntity[] getMotions() {
            return motions;
        }
    }

    public static class HotWordHeardEvent extends BaseEvent {

        public static class LPSPosition {
            @SerializedName("Position")
            private int[] position;
            @SerializedName("AngleVector")
            private int[] angleVector;
            @SerializedName("Confidence")
            float confidence;
        }

        public static class SpeakerId {
            @SerializedName("Type")
            EntityTrackEvent.EntityType type;
            @SerializedName("Confidence")
            float confidence;
        }

        public static class Speaker {
            @SerializedName("LPSPosition")
            LPSPosition lpsPosition;
            @SerializedName("SpeakerID")
            SpeakerId speakerId;
        }

        @SerializedName("Speaker")
        private Speaker speaker;

        public Speaker getSpeaker() {
            return speaker;
        }
    }

    static public class FetchAssetEvent extends BaseEvent {

        public enum FetchAssetEvents {
            @SerializedName("onAssetReady")
            AssetReady,
            @SerializedName("onAssetFailed")
            AssetFailed
        }

        @SerializedName("Event")
        private FetchAssetEvents event;
        @SerializedName("Detail")
        private String detail;

        public FetchAssetEvents getFetchEvent() {
            return event;
        }
        public String getDetail() {
            return detail;
        }
    }

    static public class HeadTouchEvent extends BaseEvent {

        /** emits `onHeadTouch` if any one of Jibo's head touch sensors is touched */
        public enum HeadTouchEvents {
            @SerializedName("onHeadTouch")
            HeadTouched
        }

        /**
         * Head touch sensor info. Head touch pads run down the back
         * of Jibo's head, three on a side.
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


        @SerializedName("Event")
        private HeadTouchEvents event;

        @SerializedName("Pads")
        private boolean[] pads;

        /** Return a head touch event
         * @return event Head touch event
         */
        public HeadTouchEvents getHeadTouchEvent() {
            return event;
        }
        /**
         * Return state of Jibo's six head touch pads
         * @return pads Array of six head touch pad booleans. `true` if touched.
         */
        public boolean[] getDetail() {
            return pads;
        }
    }

    /****************THIS IS THE CLASS THAT MAPS RECEIVED JSON INTO FINAL EVENTMESSAGE****************/

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
            mEventsMap.put(Tap, TapEvent.class);
            mEventsMap.put(Swipe, SwipeEvent.class);
            mEventsMap.put(ListenResult, ListenResultEvent.class);
            mEventsMap.put(MotionDetected, MotionEvent.class);
            mEventsMap.put(HotWordHeard, HotWordHeardEvent.class);
            mEventsMap.put(AssetReady, FetchAssetEvent.class);
            mEventsMap.put(AssetFailed, FetchAssetEvent.class);
            mEventsMap.put(HeadTouched, HeadTouchEvent.class);
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
                    eventMessage.setEventBody(event);

                    return eventMessage;
                }
            } catch (Exception e) {
              System.out.print(e.getLocalizedMessage());
            }

            return eventMessage;
        }
    }

}
