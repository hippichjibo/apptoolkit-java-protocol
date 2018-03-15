package com.jibo.apptoolkit.protocol;

/**
 * Created by alexz on 12/22/2017.
 */
/** @hide */
public class JiboRemoteInitializationException extends Exception {
    static public final String ERROR_INVALID_INIT_INPUT = "Please initialize properly with all fields filled in";
    static public final String ERROR_CONTEXT_MUST_BE_PROVIDED = "Context must be provided!";
    static public final String ERROR_JIBO_REMOTE_CONTROL_NOT_INITIALIZED = "Please initialize JiboRemoteControl " +
            "properly before using!";
    public JiboRemoteInitializationException(String s) {
        super(s);
    }
}
