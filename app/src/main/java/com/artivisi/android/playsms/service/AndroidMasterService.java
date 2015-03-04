package com.artivisi.android.playsms.service;

import com.artivisi.android.playsms.domain.Credit;
import com.artivisi.android.playsms.helper.ContactHelper;
import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.helper.QueryHelper;


/**
 * Created by opaw on 2/5/15.
 */
public interface AndroidMasterService {
    public LoginHelper getToken(String urlServer, String username, String password) throws Exception;
    public MessageHelper getSentMessage() throws Exception;
    public MessageHelper getInbox() throws Exception;
    public MessageHelper sendMessage(String to, String msg) throws Exception;
    public MessageHelper pollInbox(String id) throws Exception;
    public MessageHelper pollSentMessage(String smslogId) throws Exception;
    public Credit getCredit() throws Exception;
    public QueryHelper query() throws Exception;
    public ContactHelper getContact() throws Exception;
}
