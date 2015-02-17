package com.artivisi.android.playsms.helper;

import com.artivisi.android.playsms.domain.LastId;
import com.artivisi.android.playsms.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/17/15.
 */
public class DataQuery {

    private User user;
    @JsonProperty("last_id")
    private LastId lastId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LastId getLastId() {
        return lastId;
    }

    public void setLastId(LastId lastId) {
        this.lastId = lastId;
    }
}
