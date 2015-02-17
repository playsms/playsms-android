package com.artivisi.android.playsms.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/17/15.
 */
public class QueryHelper {
    private String status;
    private String error;
    private DataQuery data;
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

    public DataQuery getData() {
        return data;
    }

    public void setData(DataQuery data) {
        this.data = data;
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
