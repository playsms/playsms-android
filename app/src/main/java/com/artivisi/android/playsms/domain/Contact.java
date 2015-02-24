package com.artivisi.android.playsms.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by opaw on 2/9/15.
 */
public class Contact {
    @JsonProperty("pid")
    private String pid;
    private String p_desc;
    private String p_num;
    private String email;
    @JsonIgnore private boolean selected;


//    public Contact(String pid, String p_num, String email, String p_desc) {
//        this.pid = pid;
//        this.p_num = p_num;
//        this.email = email;
//        this.p_desc = p_desc;
//    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getP_num() {
        return p_num;
    }

    public void setP_num(String p_num) {
        this.p_num = p_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getP_desc() {
        return p_desc;
    }

    public void setP_desc(String p_desc) {
        this.p_desc = p_desc;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}

