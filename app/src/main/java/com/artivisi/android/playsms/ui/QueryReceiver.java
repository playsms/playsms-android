package com.artivisi.android.playsms.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.artivisi.android.playsms.R;
import com.artivisi.android.playsms.domain.User;
import com.artivisi.android.playsms.helper.MessageHelper;
import com.artivisi.android.playsms.helper.QueryHelper;
import com.artivisi.android.playsms.service.AndroidMasterService;
import com.artivisi.android.playsms.service.impl.AndroidMasterServiceImpl;
import com.artivisi.android.playsms.ui.db.PlaySmsDb;
import com.google.gson.Gson;

/**
 * Created by opaw on 2/17/15.
 */
public class QueryReceiver extends BroadcastReceiver {

    private AndroidMasterService service;
    private PlaySmsDb db;
    private String lastInbox;
    private String lastSent;

    @Override
    public void onReceive(Context context, Intent intent) {
        persistentNotif(context);
        User user = getUserCookie(context, LoginActivity.KEY_USER, User.class);
        service = new AndroidMasterServiceImpl(user);
        db = new PlaySmsDb(context);
        lastInbox = db.getLastInbox();
        lastSent = db.getLastSent();
        if(isNetworkAvailable(context)){
            new DoQuery(context).execute();
        } else {
            Log.i("CONNECTION : ", "NO INTERNET CONNECTION");
        }
    }

    private static void generateNotif(Context context, String message){
        int icon = R.drawable.app_notif;
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(icon, message, when);
        notification.defaults |= Notification.DEFAULT_ALL;
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, DashboardActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(0, notification);
    }

    private static void persistentNotif(Context context){
        int icon = R.drawable.app_notif;
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, null, when);
        notification.defaults |= Notification.DEFAULT_ALL;
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, DashboardActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, null, intent);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(0, notification);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class DoQuery extends AsyncTask<Void, Void, QueryHelper>{

        private Context context;

        public DoQuery(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("DO QUERY : ", "RUNNING");
        }

        @Override
        protected QueryHelper doInBackground(Void... params) {
            try {
                return service.query();
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(QueryHelper queryHelper) {
            super.onPostExecute(queryHelper);
            Log.i("DO QUERY : ", "DONE");
            if (queryHelper == null){
                Log.i("CONNECTION ERROR : ", "TIMEOUT");
            } else {
                if(queryHelper.getError().equals("0")){

                    if(!queryHelper.getData().getLastId().getUserInbox().equals(lastInbox)){
                        new GetInbox(context).execute();
                    }

                    if(!queryHelper.getData().getLastId().getUserOutgoing().equals(lastSent)){
                        new GetSentMessage(context).execute();
                    }

                } else {
                    Log.i("ERROR : ", queryHelper.getErrorString());
                }
            }
        }
    }

    private class GetInbox extends AsyncTask<Void, Void, MessageHelper> {
        private Context context;

        public GetInbox(Context context) {
            this.context = context;
            Log.i("GET INBOX : ", "RUNNING");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try {
                return service.pollInbox(lastInbox);
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            Log.i("GET INBOX : ", "DONE");
            if(messageHelper == null){
                Log.i("CONNECTION ERROR : ", "TIMEOUT");
            } else {
                if (messageHelper.getStatus() != null) {
                    if (messageHelper.getStatus().equals("ERR")) {
                        if (messageHelper.getError().equals("501")) {
                            Log.i("NEW INBOX", "NO NEW INBOX");
                        }
                    }
                } else {
                    for (int i = 0; i < messageHelper.getData().size(); i++) {
                        db.insertNewInbox(messageHelper.getData().get(i));
                    }
                    String msg;
                    if (messageHelper.getData().size() == 1) {
                        msg = messageHelper.getData().size() + " New Message";
                    } else {
                        msg = messageHelper.getData().size() + " New Messages";
                    }
                    Intent intent = new Intent(DashboardActivity.DISPLAY_MESSAGE_ACTION);
                    intent.putExtra("polling","newInbox");
                    context.sendBroadcast(intent);
                    generateNotif(context, msg);
                }
            }
        }

    }

    private class GetSentMessage extends AsyncTask<Void, Void, MessageHelper>{
        private Context context;

        public GetSentMessage(Context context) {
            this.context = context;
            Log.i("GET INBOX : ", "RUNNING");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET SENT : ", "RUNNING");
        }

        @Override
        protected MessageHelper doInBackground(Void... params) {
            try {
                return service.pollSentMessage(lastSent);
            } catch (Exception e) {
                Log.d("CONNECTION ERROR : ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MessageHelper messageHelper) {
            super.onPostExecute(messageHelper);
            Log.i("GET SENT : ", "DONE");
            if(messageHelper == null){
                Log.i("CONNECTION ERROR : ", "TIMEOUT");
            } else {
                if(messageHelper.getStatus() != null){
                    if(messageHelper.getStatus().equals("ERR")){
                        if(messageHelper.getError().equals("400")){
                            Log.i("ERROR : ", "NO SENT MESSAGE");
                        }
                    }
                } else {
                    for (int i = 0; i < messageHelper.getData().size(); i++){
                        db.insertSent(messageHelper.getData().get(i));
                    }
                    Intent intent = new Intent(DashboardActivity.DISPLAY_MESSAGE_ACTION);
                    intent.putExtra("polling","newSent");
                    context.sendBroadcast(intent);
                }
            }
        }
    }

    protected <T> T getUserCookie(Context context, String key, Class<T> a) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE);

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

}
