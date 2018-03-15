package com.jibo.apptoolkit_java_protocol.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Created by Jibo, Inc. on 30.10.17.
 */

/**
 * Convenience class for robot information
 */
public class RobotData {

    @SerializedName("data")
    private List<BaseRobot> robots;


    /**
     * Convenience function for converting List of Robot to List of BaseRobot
     * @return robots See {@link BaseRobot}
     */
    public List<BaseRobot> getRobots() {
        return robots;
    }


}
