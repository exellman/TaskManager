package com.kanayev.android.taskmanager2.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.kanayev.android.taskmanager2.model.TaskManagerDBHelper;
import com.kanayev.android.taskmanager2.service.TaskService;
import com.kanayev.android.taskmanager2.util.SettingsPreferences;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
//        String id = intent.getStringExtra("id");
//        TaskManagerDBHelper db = new TaskManagerDBHelper(context);
//        Cursor task = db.getDataSpecific(id);
//        if (task != null) {
//            task.moveToFirst();
//            while (!task.isAfterLast()) {
//                TaskService.setTaskAlarm(context, intent);
//                task.moveToNext();
//            }
//        }
//
//        Log.i(TAG, new Date(SettingsPreferences.getPrefTime(context)).toString());
//        TaskService.setDayAlarm(context, new Date(SettingsPreferences.getPrefTime(context)), SettingsPreferences.getPrefSummary(context));

    }
}


