package com.artivisi.android.playsms.service;

import com.artivisi.android.playsms.helper.LoginHelper;

/**
 * Created by opaw on 2/5/15.
 */
public interface AndroidMasterService {
    public LoginHelper getToken(String username, String password);

}
