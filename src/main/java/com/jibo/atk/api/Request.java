package com.jibo.atk.api;

/*
 * Created by alexz on 30.10.17.
 */
/** @hide */
public class Request {

    public static class TokenExchangeRequest {
        private final String grant_type = "authorization_code";
        private String client_id;
        private String client_secret;
        private String redirect_uri;
        private String code;

        public TokenExchangeRequest(String client_id, String client_secret, String redirect_uri, String code) {
            this.client_id = client_id;
            this.client_secret = client_secret;
            this.redirect_uri = redirect_uri;
            this.code = code;
        }

        public String getGrantType() {
            return grant_type;
        }

        public String getClientId() {
            return client_id;
        }

        public String getClientSecret() {
            return client_secret;
        }

        public String getRedirectUri() {
            return redirect_uri;
        }

        public String getCode() {
            return code;
        }
    }

    public static class TokenRefreshRequest {
        private final String grant_type = "refresh_token";
        private String client_id;
        private String client_secret;
        private String refresh_token;

        public TokenRefreshRequest(String client_id, String client_secret, String refresh_token) {
            this.client_id = client_id;
            this.client_secret = client_secret;
            this.refresh_token = refresh_token;
        }

        public String getGrantType() {
            return grant_type;
        }

        public String getClientId() {
            return client_id;
        }

        public String getClientSecret() {
            return client_secret;
        }

        public String getRefreshToken() {
            return refresh_token;
        }
    }

    public static class CertificatesRequest {
        private String friendlyId;

        public CertificatesRequest(String friendlyId) {
            this.friendlyId = friendlyId;
        }

        public String getFriendlyId() {
            return friendlyId;
        }
    }
}
