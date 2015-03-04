package com.artivisi.android.playsms.helper;

import com.artivisi.android.playsms.domain.Contact;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by opaw on 3/4/15.
 */
public class ContactHelper {
    private String status;
    private String error;
    private List<Contact> data;
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

    public List<Contact> getData() {
        return data;
    }

    public void setData(List<Contact> data) {
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
