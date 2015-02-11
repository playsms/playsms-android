package com.artivisi.android.playsms.helper;

import com.artivisi.android.playsms.domain.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by opaw on 2/9/15.
 */
public class MessageHelper {
    private List<Message> data;
    private String status;
    private String error;
    @JsonProperty("error_string")
    private String errorString;
    private String timestamp;

    public List<Message> getData() {
        return data;
    }

    public void setData(List<Message> data) {
        this.data = data;
    }

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
