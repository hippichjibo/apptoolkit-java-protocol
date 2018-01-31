package com.jibo.atk.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Created by alexz on 30.10.17.
 */

/**
 * Conveince class for robot information
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
