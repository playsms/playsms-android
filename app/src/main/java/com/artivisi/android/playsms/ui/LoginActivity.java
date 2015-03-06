package com.artivisi.android.playsms.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.Credit;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.ContactHelper;
import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;
import com.google.gson.Gson;


public class LoginActivity extends Activity {

    AndroidMasterService service = new AndroidMasterServiceImpl();
    EditText mUsername, mPassword, mServerUrl;
    LinearLayout layoutLoading;
    TextView textLoginError, txtNoNetwork, txtUrlError, txtServerError;
    String username, password, serverUrl;
    private PlaySmsDb playSmsDb;
    private Intent dashboardActivity;

    public static final String PREFS = "playSMS";
    public static final String KEY_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        playSmsDb = new PlaySmsDb(getApplicationContext());

        Typeface typefaceTittle = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Heavy.ttf");
        Typeface typefaceSubTittle = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");

        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        layoutLoading.setVisibility(View.INVISIBLE);

        textLoginError = (TextView) findViewById(R.id.text_login_error);
        textLoginError.setVisibility(View.INVISIBLE);
        textLoginError.setTypeface(typefaceSubTittle);

        txtNoNetwork = (TextView) findViewById(R.id.text_no_network);
        txtNoNetwork.setVisibility(View.INVISIBLE);
        txtNoNetwork.setTypeface(typefaceSubTittle);

        txtUrlError = (TextView) findViewById(R.id.text_url_error);
        txtUrlError.setVisibility(View.INVISIBLE);
        txtUrlError.setTypeface(typefaceSubTittle);

        txtServerError = (TextView) findViewById(R.id.text_server_error);
        txtServerError.setVisibility(View.INVISIBLE);
        txtServerError.setTypeface(typefaceSubTittle);

        TextView loadingText = (TextView) findViewById(R.id.text_login_loading);
        loadingText.setTypeface(typefaceSubTittle);

        TextView bannerTittle = (TextView) findViewById(R.id.banner_tittle);
        bannerTittle.setTypeface(typefaceTittle);

        TextView bannerSubTittle = (TextView) findViewById(R.id.banner_subtittle);
        bannerSubTittle.setTypeface(typefaceSubTittle);

        mServerUrl = (EditText) findViewById(R.id.server_url);
        mServerUrl.setTypeface(ralewayRegular);

        mUsername = (EditText) findViewById(R.id.username);
        mUsername.setTypeface(ralewayRegular);

        mPassword = (EditText) findViewById(R.id.password);
        mPassword.setTypeface(ralewayRegular);

        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setTypeface(typefaceTittle);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){

                    txtNoNetwork.setVisibility(View.INVISIBLE);
                    txtUrlError.setVisibility(View.INVISIBLE);
                    txtServerError.setVisibility(View.INVISIBLE);

                    serverUrl = mServerUrl.getText().toString();
                    username = mUsername.getText().toString();
                    password = mPassword.getText().toString();

                    mServerUrl.setError(null);
                    mUsername.setError(null);
                    mPassword.setError(null);

                    if(serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()){
                        if(serverUrl.isEmpty()) {
                            mServerUrl.setError("Required");
                            mServerUrl.setFocusable(true);
                        }
                        if(username.isEmpty()) {
                            mUsername.setError("Required");
                            mUsername.setFocusable(true);
                        }
                        if (password.isEmpty()) {
                            mPassword.setError("Required");
                            mPassword.setFocusable(true);
                        }
                    } else {
                        if (serverUrl.contains(".") && serverUrl.contains("http://") || serverUrl.contains("https://")){
                            mServerUrl.setError(null);
                            mUsername.setError(null);
                            mPassword.setError(null);
                            new Login().execute();
                        } else {
                            txtUrlError.setVisibility(View.VISIBLE);
                            mServerUrl.setError("Invalid");
                            mServerUrl.setFocusable(true);
                        }
                    }
                } else {
                    txtNoNetwork.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class Login extends AsyncTask<Void, Void, LoginHelper>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textLoginError.setVisibility(View.INVISIBLE);
            txtUrlError.setVisibility(View.INVISIBLE);
            layoutLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected LoginHelper doInBackground(Void... params) {
            try {
                return service.getToken(serverUrl, username, password);
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(LoginHelper loginHelper) {
            super.onPostExecute(loginHelper);
            layoutLoading.setVisibility(View.INVISIBLE);
            if(loginHelper == null){
                txtServerError.setVisibility(View.VISIBLE);
            } else {
                if(loginHelper.getError().equals("0")){
                    textLoginError.setVisibility(View.INVISIBLE);
                    Gson gson = new Gson();
                    User user = new User();
                    user.setServerUrl(serverUrl);
                    user.setUsername(username);
                    user.setToken(loginHelper.getToken());
                    service = new AndroidMasterServiceImpl(user);
                    new GetContact().execute();
                    setUserCookies(KEY_USER, gson.toJson(user));
                } else {
                    textLoginError.setText(loginHelper.getErrorString());
                    textLoginError.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(sp.contains(KEY_USER)){
            showDashboard("logged");
        }
    }

    public void showDashboard(String type){
        dashboardActivity = new Intent(getApplicationContext(), DashboardActivity.class);
        dashboardActivity.putExtra("type", type);
        startActivity(dashboardActivity);
        finish();
    }

    protected void setUserCookies(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class GetInbox extends AsyncTask<Void, Void, MessageHelper>{
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try {
                return service.getInbox();
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            if(messageHelper == null){
                Toast.makeText(getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT).show();
            } else {
                if (messageHelper.getStatus() != null) {
                    if (messageHelper.getStatus().equals("ERR")) {
                        if (messageHelper.getError().equals("501")) {
                            Log.i("ERROR : ", "NO INBOX");
                        }
                    }
                } else {
                    for (int i = 0; i < messageHelper.getData().size(); i++) {
                        playSmsDb.insertInbox(messageHelper.getData().get(i));
                    }
                }
                showDashboard("login");
            }
        }

    }

    private class GetSentMessage extends AsyncTask<Void, Void, MessageHelper>{

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try{
                return service.getSentMessage();
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            if (messageHelper == null){
                Toast.makeText(getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT).show();
            } else {
                if (messageHelper.getStatus() != null) {
                    if (messageHelper.getStatus().equals("ERR")) {
                        if (messageHelper.getError().equals("400")) {
                            Log.i("ERROR : ", "NO SENT MESSAGE");
                        }
                    }
                } else {
                    for (int i = 0; i < messageHelper.getData().size(); i++) {
                        playSmsDb.insertSent(messageHelper.getData().get(i));
                    }
                }
                new GetInbox().execute();
            }
        }
    }

    private class GetContact extends AsyncTask<Void, Void, ContactHelper>{

        @Override
        protected ContactHelper doInBackground(Void... params) {
            try{
                return service.getContact();
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ContactHelper contactHelper) {
            super.onPostExecute(contactHelper);
            if (contactHelper == null){
                Toast.makeText(getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT).show();
            } else {
                if (contactHelper.getStatus() != null) {
                    if (contactHelper.getStatus().equals("OK")) {
                        if (contactHelper.getError().equals("0")) {
                            if(contactHelper.getData() != null){
                                for (int i = 0; i < contactHelper.getData().size(); i++) {
                                    playSmsDb.insertContact(contactHelper.getData().get(i));
                                }
                            }
                        }
                    } else {
                        Log.i("ERROR : ", "NO CONTACT");
                    }
                }
                new GetSentMessage().execute();
            }
        }
    }
}
