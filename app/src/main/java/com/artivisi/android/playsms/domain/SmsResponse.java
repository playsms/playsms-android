package com.artivisi.android.playsms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/5/15.
 */
public class SmsResponse {
    private String status;
    private String error;
    @JsonProperty("smslog_id")
    private String smslogId;
    private String queue;
    private String to;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSmslogId() {
        return smslogId;
    }

    public void setSmslogId(String smslogId) {
        this.smslogId = smslogId;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
