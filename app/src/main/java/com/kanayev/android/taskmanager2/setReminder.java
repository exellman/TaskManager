package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.ALARM_SERVICE;

public class setReminder {

    Activity activity;
    private HashMap<String, String> forInfo;

    public setReminder(Activity activity, HashMap<String, String> finalInfo, Class<AlarmReceiver> arCls) {
        this.activity = activity;
        forInfo = finalInfo;

        final Intent in = new Intent(activity, AddTaskActivity.class);
        in.putExtra("isUpdate", true);
        in.putExtra("id", forInfo.get(TaskHomeActivity.KEY_ID));
        in.putExtra("task", forInfo.get(TaskHomeActivity.KEY_TASK));
        in.putExtra("date", forInfo.get(TaskHomeActivity.KEY_DATE));

        String dateF = in.getStringExtra("date");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(dateF);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);

        // Enable a receiver
        ComponentName receiver = new ComponentName(activity, arCls);
        PackageManager pm = activity.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(activity, arCls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);




    }




}
