package com.jibo.atk.api;

import com.google.gson.annotations.SerializedName;

/*
 * Created by calvinator on 1/26/18.
 */
/** @hide */
public class BaseCertificates {

    String cert;

    @SerializedName("public")
    String publicKey;

    @SerializedName("private")
    String privateKey;

    String fingerprint;

    IpPayload payload;

    Long created;

    String p12;

    public BaseCertificates() {}

    public BaseCertificates(String cert) {
        this.cert = cert;
        this.payload = new IpPayload();
    }

    public String getCert() {
        return cert;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public IpPayload getPayload() {
        return payload;
    }

    /**
    * Get the IP address of the robot to connect to.
    */
    public String getIpAddress() {
        return payload.ipAddress;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public Long getCreated(){
      return created;
    }

    public String getP12() {
        return p12;
    }

}
