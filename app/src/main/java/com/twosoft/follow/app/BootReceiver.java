package com.twosoft.follow.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.twosoft.follow.data.PreferencesHelper;
import com.twosoft.follow.network.SendDataService;

/**
 * Created by robertofz on 3/22/18.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final int SEND_DATA_SERVICE_ID = 0;
    private PreferencesHelper preferencesHelper;
    private Intent sendDataServiceIntent;
    private PendingIntent sendDataServicePIntent;
    private AlarmManager alarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferencesHelper = new PreferencesHelper(context);

        if (preferencesHelper.getFollow_mode()) {
            sendDataServiceIntent = new Intent(context, SendDataService.class);
            sendDataServicePIntent = PendingIntent.getBroadcast(context, SEND_DATA_SERVICE_ID, sendDataServiceIntent, 0);
            alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, sendDataServicePIntent);
        }
    }
}
