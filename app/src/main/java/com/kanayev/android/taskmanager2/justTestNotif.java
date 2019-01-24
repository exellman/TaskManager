package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.ALARM_SERVICE;

public class justTestNotif{
    Activity activity;
    private HashMap<String, String> forInfo;


    public justTestNotif(Activity activity, HashMap<String, String> maap) {
        this.activity = activity;
        forInfo = maap;

        final Intent in = new Intent(activity, AddTaskActivity.class);
        in.putExtra("isUpdate", true);
        in.putExtra("id", forInfo.get(TaskHomeActivity.KEY_ID));
        in.putExtra("task", forInfo.get(TaskHomeActivity.KEY_TASK));
        in.putExtra("date", forInfo.get(TaskHomeActivity.KEY_DATE));
        justNotif(in);
    }

    public void justNotif(Intent in) {

        String dateF = in.getStringExtra("date");
        String id = in.getStringExtra("id");

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(dateF);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);

        Date currentDate = new Date();

        Intent intent = new Intent(activity.getApplicationContext(), Notification_reciever.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), Integer.parseInt(id), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getApplicationContext().getSystemService(ALARM_SERVICE);

        if (currentDate.getTime() < date.getTime()) {
                Log.d("Lol", "Yep");
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
