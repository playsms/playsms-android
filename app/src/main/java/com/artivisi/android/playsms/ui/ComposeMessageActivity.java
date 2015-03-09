package com.artivisi.android.playsms.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ComposeMessageActivity extends ActionBarActivity {

    private String to;
    private String msg;

    private EditText mMsgTo, mMsg;
    private ProgressBar sendingMsg;
    private AndroidMasterService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        getSupportActionBar().setTitle("New Message");
        getSupportActionBar().setIcon(R.drawable.pen);
        User u = getUserCookie(LoginActivity.KEY_USER, User.class);

        service = new AndroidMasterServiceImpl(u);



        mMsgTo = (EditText) findViewById(R.id.txt_msg_to);
        mMsg = (EditText) findViewById(R.id.txt_msg);
        sendingMsg = (ProgressBar) findViewById(R.id.sending_msg);
        sendingMsg.setVisibility(View.INVISIBLE);

        if(getIntent().hasExtra("to")){
            mMsgTo.setText(getIntent().getStringExtra("to"));
        }
        if(getIntent().hasExtra("msg")){
            mMsg.setText(getIntent().getStringExtra("msg"));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            if(isNetworkAvailable()){
                try {
                    msg = URLEncoder.encode(mMsg.getText().toString(), "UTF-8");
                    to = URLEncoder.encode(mMsgTo.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    msg = mMsg.getText().toString();
                    to = mMsgTo.getText().toString();
                }
                to = to.trim();
                new SendMessage().execute();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class SendMessage extends AsyncTask<Void, Void, MessageHelper> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendingMsg.setVisibility(View.VISIBLE);
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try {
                return service.sendMessage(to, msg);
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            sendingMsg.setVisibility(View.INVISIBLE);
            if (messageHelper == null){
                Toast.makeText(getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT).show();
            } else {
                if(messageHelper.getStatus() != null){
                    if(messageHelper.getStatus().equals("ERR")){
                        Toast.makeText(getApplicationContext(), messageHelper.getErrorString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Message has been delivered", Toast.LENGTH_SHORT).show();
                    mMsgTo.setText("");
                }
            }
        }
    }

    protected <T> T getUserCookie(String key, Class<T> a) {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE);

        if (sharedPreferences == null) {
            return null;
        }

        String data = sharedPreferences.getString(key, null);

        if (data == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(data, a);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
