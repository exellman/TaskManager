package com.kanayev.android.taskmanager2;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TaskService extends IntentService {

    private static final String TAG = "TaskService";

    public static Intent newIntent(Context context) {
        return new Intent(context, TaskService.class);
    }

    public TaskService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "On handle intent");

        String id = intent.getStringExtra("id");
        String taskName = null;

        TaskManagerDBHelper mydb = new TaskManagerDBHelper(getApplicationContext());

        Cursor task = mydb.getDataSpecific(id);
        if (task != null) {
            task.moveToFirst();
            taskName = task.getString(1).toString();
            task.close();
        }

        long[] vibrate = new long[2];
            vibrate[0] = 500;
            vibrate[1] = 500;

        Uri soundUri = null;
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


        if ((Integer.parseInt(intent.getStringExtra("id")) != 0)) {
            showTaskNotification(intent, vibrate, soundUri, id, taskName);
            return;
        }
    }

    private void showTaskNotification(Intent intent, long[] vibrate, Uri soundUri, String id, String taskName) {

        Intent i = new Intent(getApplicationContext(), AddTaskActivity.class);
        i.putExtra("isUpdate", true);
        i.putExtra("id", id);
        PendingIntent pi = PendingIntent.getActivity(this, Integer.parseInt(id), i, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("It's task time!")
                .setContentText(taskName + " at " + intent.getStringExtra("date"))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setSound(soundUri)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(id), notification);

        Log.i(TAG, "Notification with task '" + taskName + "' is showed");
    }

    public static void setTaskAlarm(Context context, HashMap<String, String> forInfo) {

        String id = forInfo.get(CreateTodoActivity.KEY_ID);
        String dateStr = forInfo.get(CreateTodoActivity.KEY_DATE);
        String name = forInfo.get(CreateTodoActivity.KEY_TASK);

        Intent i = TaskService.newIntent(context);
        i.putExtra("id", id);
        i.putExtra("date", dateStr);

        PendingIntent pi = PendingIntent.getService(context, Integer.parseInt(id), i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date taskTime = date;
        Date currentTime = new Date();
        if (currentTime.getTime() < taskTime.getTime()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, taskTime.getTime(), pi);
            Log.i(TAG, "Task " + name + " added");
        }
    }
}
