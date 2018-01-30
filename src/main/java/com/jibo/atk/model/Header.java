package com.jibo.atk.model;

/*
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

     public String getTransactionID() {
         return TransactionID;
     }

     static public class RequestHeader extends Header {
         static public final String VER_1 = "1.0";

         private String AppID = "ImmaLittleTeapot";
         private String Credentials;
         private String Version = VER_1;

         public RequestHeader(String transactionID) {
             super(transactionID);
         }

         public RequestHeader(String transactionID, String sessionID, String version) {
             super(transactionID, sessionID);
             this.Version = version;
         }

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

         public ResponseHeader() {
         }

         public ResponseHeader(String transactionID) {
             super(transactionID);
         }

         public ResponseHeader(String transactionID, String sessionID) {
             super(transactionID, sessionID);
         }

         public String getRobotID() {
             return RobotID;
         }

     }

 }
