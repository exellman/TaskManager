package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.ALARM_SERVICE;

public class NotificationScheduler {

    static Context context;
    private static HashMap<String, String> forInfo;

    private static int DAILY_REMINDER_REQUEST_CODE = 0;


    public static void setReminder(Activity a, HashMap<String, String> finalInfo, Class<AlarmReceiver> arCls) {
        context = a;
        forInfo = finalInfo;

        final Intent in = new Intent(context, AddTaskActivity.class);
        in.putExtra("isUpdate", true);
        in.putExtra("id", forInfo.get(TaskHomeActivity.KEY_ID));
        in.putExtra("task", forInfo.get(TaskHomeActivity.KEY_TASK));
        in.putExtra("date", forInfo.get(TaskHomeActivity.KEY_DATE));

        String id = in.getStringExtra("id");
        String strDate = in.getStringExtra("date");
        String task = in.getStringExtra("task");

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date currentDate = new Date();

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, arCls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, arCls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(id), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (currentDate.getTime() < date.getTime()) {
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public static void showNotification(Context context, Class<AddTaskActivity> cls) {
        String title = "Title";
        String content = "Content";

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(DAILY_REMINDER_REQUEST_CODE,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification);


    }
}
