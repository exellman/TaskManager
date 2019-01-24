package com.kanayev.android.taskmanager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction() != null && context != null) {
//            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
//                // Set the alarm here.
//                Log.d(TAG, "onReceive: BOOT_COMPLETED");
//                ListTaskAdapter listTaskAdapter = new ListTaskAdapter(setReminder);
//                NotificationScheduler.setReminder(context, AlarmReceiver.class,
//                        localData.get_hour(), localData.get_min());
//                return;
//            }
//        }

        NotificationScheduler.showNotification(context, AddTaskActivity.class);

    }
}