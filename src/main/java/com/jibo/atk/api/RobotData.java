package com.jibo.atk.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Created by alexz on 30.10.17.
 */

/**
 * Class for robot data
 */
public class RobotData {

    @SerializedName("data")
    private List<BaseRobot> robots;

    /**
     * Get a list of all robots associated with the user’s authenticated account.
     * It is suggested that you prompt users to select which robot they would
     * like to connect to use your app in the event that they own multiple robots.
     * @return robots Robots for whom this user is the owner.
     */
    public List<BaseRobot> getRobots() {
        return robots;
    }


}
