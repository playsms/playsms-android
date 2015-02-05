package com.artivisi.android.playsms.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.helper.LoginHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;


public class LoginActivity extends Activity {

    AndroidMasterService service = new AndroidMasterServiceImpl();
    EditText mUsername, mPassword;
    LinearLayout layoutLoading;
    TextView textLoginError;
    String username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface typefaceTittle = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Heavy.ttf");
        Typeface typefaceSubTittle = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");

        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        layoutLoading.setVisibility(View.INVISIBLE);

        textLoginError = (TextView) findViewById(R.id.text_login_error);
        textLoginError.setVisibility(View.INVISIBLE);
        textLoginError.setTypeface(typefaceSubTittle);

        TextView loadingText = (TextView) findViewById(R.id.text_login_loading);
        loadingText.setTypeface(typefaceSubTittle);

        TextView bannerTittle = (TextView) findViewById(R.id.banner_tittle);
        bannerTittle.setTypeface(typefaceTittle);

        TextView bannerSubTittle = (TextView) findViewById(R.id.banner_subtittle);
        bannerSubTittle.setTypeface(typefaceSubTittle);

        mUsername = (EditText) findViewById(R.id.username);
        mUsername.setTypeface(ralewayRegular);

        mPassword = (EditText) findViewById(R.id.password);
        mPassword.setTypeface(ralewayRegular);

        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setTypeface(typefaceTittle);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                mUsername.setError(null);
                mPassword.setError(null);

                if(username.isEmpty()) {
                    mUsername.setError("Required");
                } else if (password.isEmpty()) {
                    mPassword.setError("Required");
                } else if (username.isEmpty() && password.isEmpty()) {
                    mUsername.setError("Required");
                    mPassword.setError("Required");
                } else {
                    mUsername.setError(null);
                    mPassword.setError(null);
                    new Login().execute();
                }
            }
        });
    }

    public class Login extends AsyncTask<Void, Void, LoginHelper>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textLoginError.setVisibility(View.INVISIBLE);
            layoutLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected LoginHelper doInBackground(Void... params) {
            return service.getToken(username, password);
        }

        @Override
        protected void onPostExecute(LoginHelper loginHelper) {
            super.onPostExecute(loginHelper);
            layoutLoading.setVisibility(View.INVISIBLE);
            if(loginHelper.getError().equals("100")){
                textLoginError.setVisibility(View.VISIBLE);
            }
            if(loginHelper.getError().equals("0")){
                textLoginError.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), loginHelper.getToken(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
