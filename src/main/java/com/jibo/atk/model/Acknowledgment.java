package com.jibo.atk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexz on 11.10.17.
 */

 public class Acknowledgment extends BaseResponse {

     public enum ResponseCode {
         @SerializedName("200")
         OK,
         @SerializedName("201")
         Created,
         @SerializedName("202")
         Accepted,
         @SerializedName("400")
         BadRequest,
         @SerializedName("403")
         Forbidden,
         @SerializedName("404")
         NotFound,
         @SerializedName("406")
         NotAcceptable,
         @SerializedName("407")
         RequestTimeout,
         @SerializedName("409")
         Conflict,
         @SerializedName("412")
         PreconditionFailed,
         @SerializedName("500")
         InternalError,
         @SerializedName("503")
         ServiceUnavailable,
         @SerializedName("505")
         VersionNotSupported;
     }

     public enum ValueResult {
         @SerializedName("Success")
         Success,
         @SerializedName("Error")
         Error;
     }

     private Header.ResponseHeader ResponseHeader;
     private AcknowledgementBody Response;

     public Acknowledgment(Header.ResponseHeader responseHeader, AcknowledgementBody response) {
         ResponseHeader = responseHeader;
         Response = response;
     }

     public Header.ResponseHeader getResponseHeader() {
         return ResponseHeader;
     }

     public AcknowledgementBody getResponse() {
         return Response;
     }

     public void setResponse(AcknowledgementBody response) {
         Response = response;
     }

     /*******************DEFINING RESPONSE PARTS HERE*********************/

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
     static public class ErrorResponseBody extends AcknowledgementBody {
         private String ErrorDetail;

         public String getErrorDetail() {
             return ErrorDetail;
         }
     }

     /*******************DEFINING RESPONSES HERE*********************/

     static public class CancelResponse extends AcknowledgementBody {
         private String ResponseBody;

         public String getResponseBody() {
             return ResponseBody;
         }
     }

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

     static public class ListenErrorResponse extends ErrorResponseBody {}

     static public class LookAtErrorResponse extends ErrorResponseBody {}

     static public class SayErrorResponse extends ErrorResponseBody {}

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
