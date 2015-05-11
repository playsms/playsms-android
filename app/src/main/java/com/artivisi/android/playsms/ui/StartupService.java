package com.artivisi.android.playsms.ui;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by opaw on 5/11/15.
 */
public class StartupService extends IntentService {

    public static final String PREFS = "playSMS";
    public static final String KEY_USER = "user";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public StartupService() {
        super("Startup Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
    if(sp.contains(KEY_USER)){
        Intent alarmIntent = new Intent(this, QueryReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 60 * 1000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }
    }
}
