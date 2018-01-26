package com.jibo.rom.sdk.model.api;

/**
 * Created by calvinator on 1/26/18.
 */

public class BaseRobot {

    String id;
    String name;
    String robotName;

    public BaseRobot(){
    }

    /**
     * Information about the authenticated robot
     * @param id Unique ID of the robot
     * @param name Loop name. Usually `<OwnerFirstName>'s Jibo`
     * @param robotName My-Friendly-Robot-Name, found on the underside of the robot's base
     */
    public BaseRobot(String id, String name, String robotName) {
        this.id = id;
        this.name = name;
        this.robotName = robotName;
    }

    /**
     * Get the authenticated robot's unique ID.
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the authenticated robot's Loop name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the authenticated robot's serial name.
     * @return robotName
     */
    public String getRobotName() {
        return robotName;
    }

}
