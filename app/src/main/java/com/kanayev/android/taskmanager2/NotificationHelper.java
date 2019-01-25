package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class NotificationHelper {

    private static final String TAG = "NotificationHelper";
    private static final String EXTRA_ID = "extra id";
    public static final String ID_EXTRA = "task_id";
    public static final int DAILY_REMINDER_REQUEST_CODE = 100;

    public static LocalData localData = new LocalData();

    public static void setReminder(Activity activity, Class<AlarmReceiver> arCls) {

        int id = localData.get_id();
        String name = localData.get_name();
        Date date = localData.get_date();

        ComponentName receiver = new ComponentName(activity, arCls);
        PackageManager pm = activity.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Calendar calendar = Calendar.getInstance();
        Date taskTime = date;
        Date currentTime = new Date();
        calendar.setTime(taskTime);

        Intent alertIntent = new Intent(activity, arCls);
//        alertIntent.putExtra("id", localData.get_id());
//        alertIntent.putExtra("task", localData.get_name());
//        alertIntent.putExtra("date", localData.get_date());
        PendingIntent pi = PendingIntent.getBroadcast(activity, id, alertIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);


        //Check Time
        if(currentTime.getTime() < taskTime.getTime()){
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            Log.i(TAG, "Task " + name + " added.");
        }
    }

    public static void showTaskNotification(Context context, Class<AddTaskActivity> adCls) {

        long[] vibrate = new long[2];
        vibrate[0] = 500;
        vibrate[1] = 500;

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


        int id = localData.get_id();
        String name = localData.get_name();
        Date taskDate = localData.get_date();

            Intent i = new Intent(context, adCls);
            i.putExtra("id", id);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.arrow_up_float)
                    .setContentTitle("It's task time!")
                    .setContentText(name + " at " + taskDate)
                    .setTicker("It's task time!")
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setVibrate(vibrate)
                    .setSound(soundUri)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, notification);

            Log.i(TAG, "Notification with task '" + name + "' is showed");

    }




//    public static void showTaskNotification(Context context, int id, String name, Date date, Class<AddTaskActivity> adCls) {
//
//        long[] vibrate = new long[2];
//        vibrate[0] = 500;
//        vibrate[1] = 500;
//
//        Intent resultIntent = new Intent(context, adCls);
//        resultIntent.putExtra("id", id);
//
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
//        taskStackBuilder.addParentStack(adCls);
//        taskStackBuilder.addNextIntent(resultIntent);
//
//        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
//
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(android.R.drawable.arrow_up_float)
//                .setContentTitle("It's Task Time!")
//                .setContentText(name + " at " + date)
//                .setTicker("It's Task Time!")
//                .setContentIntent(resultPendingIntent)
//                .setAutoCancel(true)
//                .setVibrate(vibrate)
//                .setDefaults(NotificationCompat.DEFAULT_SOUND);
//
//                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(0, mBuilder.build());
//    }
}
