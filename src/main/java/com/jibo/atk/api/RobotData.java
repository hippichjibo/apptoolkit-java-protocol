package com.jibo.atk.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alexz on 30.10.17.
 */

/**
 * Class for robot data
 */
public class RobotData {

    @SerializedName("data")
    private List<BaseRobot> robots;

    /**
     * Get the list of robots in this user's account
     * @return robots
     */
    public List<BaseRobot> getRobots() {
        return robots;
    }


}
