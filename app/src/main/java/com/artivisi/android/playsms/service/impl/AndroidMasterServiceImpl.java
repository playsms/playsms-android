package com.artivisi.android.playsms.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.artivisi.android.playsms.domain.Credit;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.ui.LoginActivity;
import com.google.gson.Gson;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by opaw on 2/5/15.
 */
public class AndroidMasterServiceImpl implements AndroidMasterService {


    private User user;
    private String PLAYSMS_URL;
    RestTemplate restTemplate = new RestTemplate(true);

    public AndroidMasterServiceImpl(String serverUrl){
        PLAYSMS_URL = serverUrl;
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public AndroidMasterServiceImpl(User user) {
        this.user = user;
        PLAYSMS_URL = user.getServerUrl();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }


    private static final String BASE_URI = "/index.php?app=ws";

    @Override
    public LoginHelper getToken(String username, String password) {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + username + "&p=" + password + "&op=get_token&format=json";
        ResponseEntity<LoginHelper> responseEntity = restTemplate.getForEntity(url, LoginHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public MessageHelper getSentMessage() {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=ds&format=json";
        ResponseEntity<MessageHelper> responseEntity = restTemplate.getForEntity(url, MessageHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public MessageHelper getInbox() {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=ix&format=json";
        ResponseEntity<MessageHelper> responseEntity = restTemplate.getForEntity(url, MessageHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public MessageHelper sendMessage(String to, String msg) {
        String url = PLAYSMS_URL + BASE_URI +
                "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=pv&to=" + to + "&msg=" + msg + "&format=json";
        ResponseEntity<MessageHelper> responseEntity = restTemplate.getForEntity(url, MessageHelper.class);
        return responseEntity.getBody();
    }

    @Override
    public Credit getCredit() {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + user.getUsername() + "&h=" + user.getToken() + "&op=cr&format=json";
        ResponseEntity<Credit> responseEntity = restTemplate.getForEntity(url, Credit.class);
        return responseEntity.getBody();
    }
}
