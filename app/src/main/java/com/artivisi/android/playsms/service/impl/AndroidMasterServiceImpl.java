package com.artivisi.android.playsms.service.impl;

import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by opaw on 2/5/15.
 */
public class AndroidMasterServiceImpl implements AndroidMasterService {
    private static final String PLAYSMS_URL = "http://my.textng.com";
    private static final String BASE_URI = "/index.php?app=ws";
    RestTemplate restTemplate = new RestTemplate(true);

    public AndroidMasterServiceImpl() {
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Override
    public LoginHelper getToken(String username, String password) {
        String url = PLAYSMS_URL + BASE_URI + "&u=" + username + "&p=" + password + "&op=get_token&format=json";

        ResponseEntity<LoginHelper> responseEntity = restTemplate.getForEntity(url, LoginHelper.class);

        return responseEntity.getBody();
    }
}
