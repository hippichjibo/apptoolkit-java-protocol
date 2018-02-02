package com.jibo.atk.utils;

/*
 * Created by Jibo, Inc. on 10.10.17.
 */
/** @hide */
public class Commons {
    public static final String DEFAULT_DOMAIN = "jibo.com";

    public static final String DEV_ENDPOINT = "dev-customer-portal.jibo.com";
    public static final String STG_ENDPOINT = "stg-customer-portal.jibo.com";
    public static final String PREPROD_ENDPOINT = "preprod-customer-portal.jibo.com";
    public static final String PROD_ENDPOINT = "portal.jibo.com";
    public static final String[] ALLOWED_ENDPOINTS = {DEV_ENDPOINT, STG_ENDPOINT,PREPROD_ENDPOINT,PROD_ENDPOINT};

    public static String ROOT_ENDPOINT = STG_ENDPOINT;

    public static void setRootEndpoint(String endpoint) throws InvalidParameterValueException {
        for (String ep : ALLOWED_ENDPOINTS) {
            if (ep.equals(endpoint)) {
                ROOT_ENDPOINT = endpoint;
                return;
            }
        }
        throw new InvalidParameterValueException("Endpoint " + endpoint + " in not in allowed endpoints list.");
    }

    public interface AsyncCallback<T> {
        void onSuccess(T result);

        void onError(Throwable exception);
    }

    public static class InvalidParameterValueException extends Throwable {
        public InvalidParameterValueException(String s) {
            super(s);
        }
    }
}
