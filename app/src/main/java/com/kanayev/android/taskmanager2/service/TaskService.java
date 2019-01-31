package com.kanayev.android.taskmanager2.service;

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
import android.util.Log;

import com.kanayev.android.taskmanager2.api.AlarmReceiver;
import com.kanayev.android.taskmanager2.R;
import com.kanayev.android.taskmanager2.util.SettingsPreferences;
import com.kanayev.android.taskmanager2.model.TaskManagerDBHelper;
import com.kanayev.android.taskmanager2.ui.activity.AddTaskActivity;
import com.kanayev.android.taskmanager2.ui.activity.DaySummaryActivity;
import com.kanayev.android.taskmanager2.util.HelpUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskService extends IntentService {
    TaskManagerDBHelper mydb;


    private static final String TAG = "TaskService";

    public TaskService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, TaskService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "On handle intent");
        mydb = new TaskManagerDBHelper(getApplicationContext());

        String id = intent.getStringExtra("id");

        String taskName = null;
        String isDone = null;
        String date = null;
        String interval = null;
        String description = null;

        long[] vibrate = new long[3];
        if (SettingsPreferences.getPrefVibration(getApplicationContext())) {
            vibrate[0] = 500;
            vibrate[1] = 500;
            vibrate[2] = 5000;
        }

        Uri soundUri = null;
        if (SettingsPreferences.getPrefSound(getApplicationContext())) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        if (Integer.parseInt(id) != 0) {
            Cursor task = mydb.getDataSpecific(id);
            if (task != null) {
                task.moveToFirst();
                taskName = task.getString(1).toString();
                date = (HelpUtils.Epoch2DateString(task.getString(2).toString(), "dd/MM/yyyy HH:mm"));
                isDone = task.getString(3).toString();
                description = task.getString(4).toString();
                interval = task.getString(5).toString();
                task.close();
            }
            showTaskNotification(intent, vibrate, soundUri, id, taskName, date, isDone, description, interval);
            return;
        }

        if (SettingsPreferences.getPrefSummary(getApplicationContext())) {
            showDayNotification(vibrate, soundUri);
        }
    }

    private void showTaskNotification(Intent intent, long[] vibrate, Uri soundUri, String id, String taskName, String date, String isDone, String description, String interval) {

        Intent i = new Intent(getApplicationContext(), AddTaskActivity.class);
        i.putExtra("isUpdate", true);
        i.putExtra("id", id);
        PendingIntent pi = PendingIntent.getActivity(this, Integer.parseInt(id), i, PendingIntent.FLAG_CANCEL_CURRENT);

        if (isDone.compareTo("true") == 0) {
            setInterval(id, taskName, date, description, interval);
            return;
        }

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("It's task time!")
                .setContentText(taskName + " at " + intent.getStringExtra("date"))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setSound(soundUri)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(id), notification);

        setInterval(id, taskName, date, description, interval);

        Log.i(TAG, "Notification with task '" + taskName + "' is showed");
    }

    private void showDayNotification(long[] vibrate, Uri soundUri) {
        Log.i(TAG, "Check day's tasks");

        Intent i = new Intent(getApplicationContext(), DaySummaryActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_day_summary)
                .setContentTitle("Check your summary")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setSound(soundUri)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }

    public static void setTaskAlarm(Context context, Intent intent) {

        String id = intent.getStringExtra("id");
        String dateStr = intent.getStringExtra("date");
        String name = intent.getStringExtra("task");

        Intent i = TaskService.newIntent(context);
        i.putExtra("id", id);
        i.putExtra("date", dateStr);

        PendingIntent pi = PendingIntent.getService(context, Integer.parseInt(id), i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
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

    public static void setDayAlarm(Context context, Date time, boolean isOn) {
        cancelReminder(context, 0);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent i = TaskService.newIntent(context);
        i.putExtra("id", "0");
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
            Log.i(TAG, "Day summary notification has changed");
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

    }

    public static void cancelReminder(Context context, int id) {
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = TaskService.newIntent(context);
        intent.putExtra("id", id);
        PendingIntent pi = PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        pi.cancel();
    }

    public void setInterval(String id, String taskName, String dateStr, String description, String interval) {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if (interval.compareTo("none") == 0) {
            return;

        } else if (interval.compareTo("day") == 0) {

            cal.add(Calendar.DATE, +1);

        } else if (interval.compareTo("week") == 0) {

            cal.add(Calendar.WEEK_OF_MONTH, +1);

        } else if (interval.compareTo("month") == 0) {

            cal.add(Calendar.MONTH, +1);

        } else if (interval.compareTo("year") == 0) {

            cal.add(Calendar.YEAR, +1);
        }

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String strDate = dateFormat.format(cal.getTime());

        Intent i = new Intent(getApplicationContext(), AddTaskActivity.class);
        i.putExtra("isUpdate", true);
        i.putExtra("id", id);
        i.putExtra("task", taskName);
        i.putExtra("date", strDate);
        mydb.updateContact(id, taskName, strDate, "false", description, interval);

        TaskService.setTaskAlarm(getApplicationContext(), i);

    }


}
