package com.jibo.apptoolkit_java_protocol.api;

import java.util.List;

/*
 * Created by Jibo, Inc. on 30.10.17.
 */
/** @hide */
public class Errors {
    static public final String ERROR_CERTIFICATES_NOT_DEPLOYED = "Certificate not deployed";

    private List<ResponseError> errors;

    public List<ResponseError> getErrors() {
        return errors;
    }

    static public class ResponseError {
        private String message;

        public String getMessage() {
            return message;
        }
    }

}
