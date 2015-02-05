package com.artivisi.android.playsms.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/5/15.
 */
public class LoginHelper {
    private String status;
    private String error;
    private String token;
    @JsonProperty("error_string")
    private String errorString;
    private Long timestamp;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
