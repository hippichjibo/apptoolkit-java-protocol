package com.jibo.apptoolkit_java_protocol.model;

import com.google.gson.annotations.SerializedName;

/*
 * Created by Jibo, Inc. on 11.10.17.
 */

 /**
  * Class for acknowledgement response codes
  */
 public class Acknowledgment extends BaseResponse {

    /**
     * Possible codes you'll receive in response to sending commands
     */
    public enum ResponseCode {
        /** The command was accepted and executed. Synchronous calls only. */
        @SerializedName("200")
        OK,
        /** The command was accepted and executed. Synchronous calls only. */
        @SerializedName("201")
        Created,
        /** The command was accepted and will begin execution. Most async commands will get this response. */
        @SerializedName("202")
        Accepted,
        /** Badly formatted request */
        @SerializedName("400")
        BadRequest,
        /** The command request is not supported */
        @SerializedName("403")
        Forbidden,
        /** Command request not found */
        @SerializedName("404")
        NotFound,
        /** The data in the command is not acceptable */
        @SerializedName("406")
        NotAcceptable,
        /** Unable to marshal the resources and set up the command within the time limits */
        @SerializedName("407")
        RequestTimeout,
        /** There is a conflicting command already executing */
        @SerializedName("409")
        Conflict,
        /** The execution of the command requires the execution of a prior command */
        @SerializedName("412")
        PreconditionFailed,
        /** The controller has crashed or hit a different unexpected error */
        @SerializedName("500")
        InternalError,
        /** The controller is temporarily unavailable. A robot service might be rebooting */
        @SerializedName("503")
        ServiceUnavailable,
        /** The version requested is not supported */
        @SerializedName("505")
        VersionNotSupported;
    }
     /** @hide */
     public enum ValueResult {
         @SerializedName("Success")
         Success,
         @SerializedName("Error")
         Error;
     }

     private Header.ResponseHeader ResponseHeader;
     private AcknowledgementBody Response;
     /** @hide */
     public Acknowledgment(Header.ResponseHeader responseHeader, AcknowledgementBody response) {
         ResponseHeader = responseHeader;
         Response = response;
     }
     /** @hide */
     public Header.ResponseHeader getResponseHeader() {
         return ResponseHeader;
     }
     /** @hide */
     public AcknowledgementBody getResponse() {
         return Response;
     }
     /** @hide */
     public void setResponse(AcknowledgementBody response) {
         Response = response;
     }

     /*******************DEFINING RESPONSE PARTS HERE*********************/
     /** @hide */
     static public class AcknowledgementBody {
         private ValueResult Value;
         private ResponseCode ResponseCode;
         private String ResponseString;

         public ValueResult getValue() {
             return Value;
         }

         public Acknowledgment.ResponseCode getResponseCode() {
             return ResponseCode;
         }

         public String getResponseString() {
             return ResponseString;
         }
     }

     //TODO not sure if we really need this one, but lets keep it for now
     /** @hide */
     static public class ErrorResponseBody extends AcknowledgementBody {
         private String ErrorDetail;

         public String getErrorDetail() {
             return ErrorDetail;
         }
     }

     /*******************DEFINING RESPONSES HERE*********************/
     /** @hide */
     static public class CancelResponse extends AcknowledgementBody {
         private String ResponseBody;

         public String getResponseBody() {
             return ResponseBody;
         }
     }
     /** @hide */
     static public class GetConfigResponse extends AcknowledgementBody {
         private ConfigInfo ResponseBody;

         public ConfigInfo getResponseBody() {
             return ResponseBody;
         }

         static public class ConfigInfo {
             private String Version;

             public String getVersion() {
                 return Version;
             }
         }
     }
     /** @hide */
     static public class ListenErrorResponse extends ErrorResponseBody {}
     /** @hide */
     static public class LookAtErrorResponse extends ErrorResponseBody {}
     /** @hide */
     static public class SayErrorResponse extends ErrorResponseBody {}
     /** @hide */
     static public class SessionResponse extends AcknowledgementBody {
         private SessionInfo ResponseBody;

         public SessionInfo getResponseBody() {
             return ResponseBody;
         }

         public SessionResponse(SessionInfo responseBody) {
             ResponseBody = responseBody;
         }

         static public class SessionInfo {
             private String SessionID;
             private String Version;

             public SessionInfo(String sessionID, String version) {
                 SessionID = sessionID;
                 Version = version;
             }

             public String getSessionID() {
                 return SessionID;
             }

             public String getVersion() {
                 return Version;
             }
         }
     }
 }
