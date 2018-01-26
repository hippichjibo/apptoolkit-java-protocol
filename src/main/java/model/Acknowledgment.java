package com.jibo.rom.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexz on 11.10.17.
 */

public class Acknowledgment extends BaseResponse {

    /**
     * Possible response codes
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

        static public class SessionInfo {
            private String SessionID;
            private String Version;

            public String getSessionID() {
                return SessionID;
            }

            public String getVersion() {
                return Version;
            }
        }
    }
}
