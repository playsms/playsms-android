package com.artivisi.android.playsms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/17/15.
 */
public class LastId {
    @JsonProperty("user_inbox")
    private String userInbox;

    @JsonProperty("user_incoming")
    private String userIncoming;

    @JsonProperty("user_outgoing")
    private String userOutgoing;

    public String getUserInbox() {
        return userInbox;
    }

    public void setUserInbox(String userInbox) {
        this.userInbox = userInbox;
    }

    public String getUserIncoming() {
        return userIncoming;
    }

    public void setUserIncoming(String userIncoming) {
        this.userIncoming = userIncoming;
    }

    public String getUserOutgoing() {
        return userOutgoing;
    }

    public void setUserOutgoing(String userOutgoing) {
        this.userOutgoing = userOutgoing;
    }
}
