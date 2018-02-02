package com.jibo.atk.api;
/**
 * Created by Jibo, Inc. on 30.10.17.
 */
/** @hide */
public class Token {

    private String token_type;
    private String access_token;
    private String refresh_token;
    private long timestamp;

    public String getTokenType() {
        return token_type;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
