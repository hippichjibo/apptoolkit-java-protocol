package com.jibo.atk.model;

/**
 * Created by alexz on 11.10.17.
 */

abstract public class Header {

    private String SessionID;
    private String TransactionID;

    public Header() {
    }

    public Header(String transactionID) {
        TransactionID = transactionID;
    }

    public Header(String transactionID, String sessionID) {
        TransactionID = transactionID;
        SessionID = sessionID;
    }

    public String getSessionID() {
        return SessionID;
    }

    /**
     * 128bit hexidecimal number that must be unique every transaction issued
     * by the particular Server. The Command Library supplied by Jibo does a
     * 128 bit hash on the Server IP address and the current time.
     * Because this is a hash this ID cannot be treated as a sequence number
     * for Command serialization.
     * @return TransactionID
     */
    public String getTransactionID() {
        return TransactionID;
    }

    static public class RequestHeader extends Header {
        static public final String VER_1 = "1.0";

        private String AppID = "ImmaLittleTeapot";
        private String Credentials;
        private String Version = VER_1;

        /**
         * Request header information
         * @param transactionID See {@link #getTransactionID}
         */
        public RequestHeader(String transactionID) {
            super(transactionID);
        }

        /**
         * Request header information
         * @param transactionID See {@link #getTransactionID}
         * @param sessionID  The session identifier that was assigned for this connection between
         * the Server and Controller. Is only allowed to be null for a `StartSession` command.
         * @param version Version that is required on the robot in order to handle the request.
         * Requesting a Protocol version that is greater than the supported version on the Robot is an error.
         */
        public RequestHeader(String transactionID, String sessionID, String version) {
            super(transactionID, sessionID);
            this.Version = version;
        }

        /**
         * Request header information
         * @param sessionID  The session identifier that was assigned for this connection between
         * the Server and Controller. Is only allowed to be null for a `StartSession` command.
         * @param transactionID See {@link #getTransactionID}
         * @param appID The reverse domain assigned name for the application provided by
         * Jibo, Inc. to the application developer.
         * @param credentials Currently unsupported
         * @param version Version that is required on the robot in order to handle the request.
         * Requesting a Protocol version that is greater than the supported version on the Robot is an error.
         */
        public RequestHeader(String sessionID, String transactionID, String appID, String credentials, String version) {
            super(sessionID, transactionID);
            AppID = appID;
            Credentials = credentials;
            Version = version;
        }

        public String getAppID() {
            return AppID;
        }

        public String getCredentials() {
            return Credentials;
        }

        public String getVersion() {
            return Version;
        }
    }

    static public class ResponseHeader extends Header {
        private String RobotID;

        public String getRobotID() {
            return RobotID;
        }
    }

}
