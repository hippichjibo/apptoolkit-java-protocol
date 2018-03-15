package com.jibo.apptoolkit.protocol;

/**
 * Created by alexz on 12/22/2017.
 */
/** @hide */
public class ROMConnectionException extends Exception {
    static public final String ERROR_STATES_MISMATCH = "State values do not match!";
    static public final String ERROR_SPOOFING_DETECTED = "Spoofing detected!";
    static public final String ERROR_AUTHORIZATION_PROBLEMS = "Authorization problems. Please re-login.";
    static public final String ERROR_CERTIFICATE_CREATION_PROBLEMS = "Error connecting robot. Please enable Remote " +
            "Control Mode.";
    static public final String ERROR_CONNECTION_PROBLEMS = "Something weird's happened along the way. Please try again...";
    static public final String ERROR_BAD_REQUEST_OR_SOMETHING = "Bad request or something. Please try " +
            "again...";
    static public final String ERROR_COULD_NOT_CONNECT_TO_ROBOT = "Could not connect to the Jibo. Please try " +
            "again...";
    static public final String ERROR_INTERNAL_SYSTEM = "Something went wrong. Please try again...";
    static public final String ERROR_ROBOT_DROPPED_CONNECTION = ERROR_CONNECTION_PROBLEMS;
    public ROMConnectionException(String s) {
        super(s);
    }
}
