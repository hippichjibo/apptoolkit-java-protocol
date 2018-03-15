package com.jibo.apptoolkit_java_protocol.api;

import com.google.gson.annotations.SerializedName;

/*
 * Created by Jibo, Inc. on 1/26/18.
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

    /** @hide */
    public BaseCertificates() {}
    /** @hide */
    public BaseCertificates(String cert) {
        this.cert = cert;
        this.payload = new IpPayload();
    }
    /** @hide */
    public String getCert() {
        return cert;
    }
    /** @hide */
    public String getPublicKey() {
        return publicKey;
    }
    /** @hide */
    public String getPrivateKey() {
        return privateKey;
    }
    /** @hide */
    public IpPayload getPayload() {
        return payload;
    }

    /**
     * Get the IP address of the robot to connect to.
     */
    public String getIpAddress() {
        return payload.ipAddress;
    }
    /** @hide */
    public String getFingerprint() {
        return fingerprint;
    }
    /** @hide */
    public Long getCreated(){
      return created;
    }
    /** @hide */
    public String getP12() {
        return p12;
    }

}
