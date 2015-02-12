package com.artivisi.android.playsms.service;

import com.artivisi.android.playsms.domain.Credit;
import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.helper.MessageHelper;


/**
 * Created by opaw on 2/5/15.
 */
public interface AndroidMasterService {
    public LoginHelper getToken(String urlServer, String username, String password) throws Exception;
    public MessageHelper getSentMessage();
    public MessageHelper getInbox();
    public MessageHelper sendMessage(String to, String msg);
    public String pollInbox(String id);
    public String pollSentMessage(String smslogId);
    public Credit getCredit();
}
