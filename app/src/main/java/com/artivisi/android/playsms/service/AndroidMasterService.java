package com.artivisi.android.playsms.service;

import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.helper.MessageHelper;

/**
 * Created by opaw on 2/5/15.
 */
public interface AndroidMasterService {
    public LoginHelper getToken(String username, String password);
    public MessageHelper getSentMessage(String username, String token);
    public MessageHelper getInbox(String username, String token);
    public MessageHelper sendMessage(String username, String token, String to, String msg);
}
