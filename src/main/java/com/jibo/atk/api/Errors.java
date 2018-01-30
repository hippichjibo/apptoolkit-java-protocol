package com.jibo.atk.api;

import java.util.List;

/*
 * Created by alexz on 30.10.17.
 */

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
