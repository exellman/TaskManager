package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.app.AlarmManager;
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

import java.util.Calendar;
import java.util.Date;

public class NotificationScheduler {

    private static final String TAG = "NotificationScheduler";
    private static final String EXTRA_ID = "extra id";
    public static final String ID_EXTRA = "task_id";
    public static final int DAILY_REMINDER_REQUEST_CODE = 100;

    private static Activity activity;

    public static void setReminder(Activity a, Intent intent, Class<AlarmReceiver> arCls) {
        activity = a;
        ForTakeInfo forTakeInfo = new ForTakeInfo();
        int id = forTakeInfo.get_id();

        ComponentName receiver = new ComponentName(activity, AlarmReceiver.class);
        PackageManager pm = activity.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Calendar calendar = Calendar.getInstance();
        Date taskTime = forTakeInfo.get_date();
        Date currentTime = new Date();
        calendar.setTime(taskTime);

        Intent alertIntent = new Intent(activity, arCls);
        PendingIntent pi = PendingIntent.getBroadcast(activity, id, alertIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);


        //Check Time
        if(currentTime.getTime() < taskTime.getTime()){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }

//        final Intent in = new Intent(context, AddTaskActivity.class);
//        in.putExtra("isUpdate", true);
//        in.putExtra("id", forInfo.get(TaskHomeActivity.KEY_ID));
//        in.putExtra("task", forInfo.get(TaskHomeActivity.KEY_TASK));
//        in.putExtra("date", forInfo.get(TaskHomeActivity.KEY_DATE));
//
//        String id = in.getStringExtra("id");
//        String strDate = in.getStringExtra("date");
//        String task = in.getStringExtra("task");

//        Date date = forTakeInfo.get_date();
//        int id = forTakeInfo.get_id();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        Date currentDate = new Date();
//
//        Intent intent1 = new Intent(context, arCls);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//
//        if (currentDate.getTime() < date.getTime()) {
//            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//        }
    }

    public static void showTaskNotification(Context context, Class<AddTaskActivity> adCls) {
        ForTakeInfo forTakeInfo = new ForTakeInfo();

        long[] vibrate = new long[2];
        vibrate[0] = 500;
        vibrate[1] = 500;

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int id = forTakeInfo.get_id();
        String task = forTakeInfo.get_name();
        Date taskDate = forTakeInfo.get_date();


//        Intent notificationIntent = new Intent(context, cls);
//        notificationIntent.putExtra("task", task);
//        PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Intent resultIntent = new Intent(context, adCls);
        resultIntent.putExtra(EXTRA_ID, id);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(adCls);
        taskStackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);



//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(cls);
//        stackBuilder.addNextIntent(notificationIntent);
//
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(id,PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("It's Task Time!")
                .setContentText(task + " at " + taskDate)
                .setTicker("It's Task Time!")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setSound(alarmSound)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, mBuilder.build());


//        NotificationManager notificationManager =
//                (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);


//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(0, notification);

//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(id, notification);


    }
}
