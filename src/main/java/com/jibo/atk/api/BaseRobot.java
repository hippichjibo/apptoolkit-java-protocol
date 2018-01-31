package com.jibo.atk.api;

/*
 * Created by calvinator on 1/26/18.
 */
/** Robot information */
public class BaseRobot {

    String id;
    String name;
    String robotName;

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

    /** @hide */
    public String getId() {
        return id;
    }

    /** @hide */
    public String getName() {
        return name;
    }

    /** @hide */
    public String getRobotName() {
        return robotName;
    }

}
