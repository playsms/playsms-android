package com.artivisi.android.playsms.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.artivisi.android.playsms.ui.fragment.StartupActivity;

/**
 * Created by opaw on 3/6/15.
 */
public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StartupActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
