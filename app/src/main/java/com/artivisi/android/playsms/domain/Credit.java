package com.artivisi.android.playsms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/11/15.
 */
public class Credit {
    private String status;
    private String error;
    private String credit;
    @JsonProperty("error_string")
    private String errorString;
    private String timestamp;

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

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
