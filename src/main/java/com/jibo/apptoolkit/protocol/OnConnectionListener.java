package com.jibo.apptoolkit.protocol;

/** Interface for connecting to a robot */
public interface OnConnectionListener {

    /** We succesfully connect to the robot */
    public void onConnected();

    /** We've started sending commands to the robot */
    public void onSessionStarted(CommandRequester commandRequester);

    @Deprecated
    public void onSessionStarted(CommandLibrary commandLibrary);

    /** We were unable to connect from the robot */
    public void onConnectionFailed(Throwable throwable);

    /** We disconnected from the robot */
    public void onDisconnected(int code);
}
