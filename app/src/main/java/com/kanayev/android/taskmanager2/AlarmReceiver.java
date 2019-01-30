package com.kanayev.android.taskmanager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

    }


}

//        LocalData localData = new LocalData();
//
//        if (intent.getAction() != null && context != null) {
//            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
//                // Set the alarm here.
//                Log.d(TAG, "onReceive: BOOT_COMPLETED");
//
//                NotificationHelper.setReminder(localData.get_a(), AlarmReceiver.class );
//                return;
//            }
//        }
//        NotificationHelper.showTaskNotification(context, AddTaskActivity.class);

