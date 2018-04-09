package com.jibo.apptoolkit.protocol.model;

import com.google.gson.annotations.SerializedName;
import com.jibo.apptoolkit.protocol.model.EventMessage.BaseEvent;
import com.jibo.apptoolkit.protocol.model.EventMessage.HeadTouchEvent;
import com.jibo.apptoolkit.protocol.model.EventMessage.ListenResultEvent;
import com.jibo.apptoolkit.protocol.model.EventMessage.MotionEvent;
import com.jibo.apptoolkit.protocol.model.EventMessage.SwipeEvent;
import com.jibo.apptoolkit.protocol.model.EventMessage.TapEvent;

/**
 * Information adapted to Be-a-maker app
 */
public class BAMEventMessage {

    public static BaseEvent toBAMEvent(BaseEvent event) {
        if (event instanceof SwipeEvent) return new BAMSwipeEvent((SwipeEvent) event);
        if (event instanceof TapEvent) return new BAMTapEvent((TapEvent) event);
        if (event instanceof ListenResultEvent) return new BAMListenEvent((ListenResultEvent) event);
        if (event instanceof MotionEvent) return new BAMMotionEvent((MotionEvent) event);
        if (event instanceof HeadTouchEvent) return new BAMHeadtouchEvent((HeadTouchEvent) event);
        return event;
    }


    //----------------------------------------------------------------------------------------------


    /**
     * Swipe event adapted to Be-a-maker
     */
    public static class BAMSwipeEvent extends BaseEvent {

        @SerializedName("gesture")
        private SwipeEvent gesture;

        private BAMSwipeEvent(SwipeEvent swipeEvent) {
            gesture = swipeEvent;
            gesture.Event = null;
        }

        public SwipeEvent getGesture() {
            return gesture;
        }

    }

    /**
     * Tap event adapted to Be-a-maker
     */
    public static class BAMTapEvent extends BaseEvent {

        @SerializedName("gesture")
        private TapEvent gesture;

        private BAMTapEvent(TapEvent tapEvent) {
            gesture = tapEvent;
            gesture.Event = null;
        }

        public TapEvent getGesture() {
            return gesture;
        }

    }

    /**
     * Listen event adapted to Be-a-maker
     */
    public static class BAMListenEvent extends BaseEvent {

        @SerializedName("listen")
        private ListenResultEvent listen;

        private BAMListenEvent(ListenResultEvent listenEvent) {
            listen = listenEvent;
            listen.Event = null;
        }

        public ListenResultEvent getGesture() {
            return listen;
        }

    }

    /**
     * Motion event adapted to Be-a-maker
     */
    public static class BAMMotionEvent extends MotionEvent {

        private BAMMotionEvent(MotionEvent motionEvent) {
            motions = motionEvent.motions;
            for (MotionEntity item : motions) {
                item.intensity = null;
            }
        }

    }

    /**
     * Head-touch event adapted to Be-a-maker
     */
    public static class BAMHeadtouchEvent extends BaseEvent {

        @SerializedName("HeadSensors")
        private HeadSensors headSensors;

        private BAMHeadtouchEvent(HeadTouchEvent headtouchEvent) {
            boolean[] sensors = headtouchEvent.getDetail();
            headSensors = new HeadSensors();

            headSensors.leftFront = sensors[0];
            headSensors.leftMiddle = sensors[1];
            headSensors.leftBack = sensors[2];
            headSensors.rightFront = sensors[3];
            headSensors.rightMiddle = sensors[4];
            headSensors.rightBack = sensors[5];
        }

        private class HeadSensors {
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

}
