package com.jibo.apptoolkit.protocol.api;

/*
 * Created by Jibo, Inc. on 1/26/18.
 */
/** Base robot information */
public class BaseRobot {

    String id;
    String name;
    String robotName;

    /** @hide */
    public BaseRobot(){
    }

    /**
     * Information about the authenticated robot
     * @param id Unique ID of the robot
     * @param name Loop name. Usually `OwnerFirstName's Jibo`
     * @param robotName `My-Friendly-Robot-Name`, found on the underside of the robot's base
     */
    public BaseRobot(String id, String name, String robotName) {
        this.id = id;
        this.name = name;
        this.robotName = robotName;
    }

    /** Get unique ID of the robot */
    public String getId() {
        return id;
    }

    /** Get robot's loop name */
    public String getName() {
        return name;
    }

    /** Get robot's name */
    public String getRobotName() {
        return robotName;
    }

}
