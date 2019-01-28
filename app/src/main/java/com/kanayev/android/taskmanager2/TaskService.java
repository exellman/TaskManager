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
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
        if (SettingsPreferences.getPrefVibration(getApplicationContext())) {
            vibrate[0] = 500;
            vibrate[1] = 500;
        }

        Uri soundUri = null;
        if (SettingsPreferences.getPrefSound(getApplicationContext())) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        if ((Integer.parseInt(intent.getStringExtra("id")) != 0)) {
            showTaskNotification(intent, vibrate, soundUri, id, taskName);
            return;
        }

        if (SettingsPreferences.getPrefSummary(getApplicationContext())) {
            showDayNotification(vibrate, soundUri);
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

    private void showDayNotification(long[] vibrate, Uri soundUri) {
        Log.i(TAG, "Check day's tasks");

//        Date currentDay = new Date();
//        GetAllTasksInteractor interactor = new GetAllTasksInteractor();
//        List<Task> allTasks = interactor.getAllTasks();
//        ArrayList<Task> todayTasks = new ArrayList<>();
//
//        for (Task task :
//                allTasks) {
//            if(task.getDate().getDate() == currentDay.getDate() &&
//                    task.getDate().getMonth() == currentDay.getMonth()){
//                Intent i = new Intent(getApplicationContext(), DaySummaryActivity.class);
//
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//                stackBuilder.addNextIntentWithParentStack(i);
//
//                PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
//
//                Notification notification = new NotificationCompat.Builder(this)
//                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
//                        .setContentTitle("Check your summary")
//                        .setContentIntent(pi)
//                        .setAutoCancel(true)
//                        .setVibrate(vibrate)
//                        .setSound(soundUri)
//                        .build();
//
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//                notificationManager.notify(0, notification);
//
//                Log.i(TAG, "Day notification is showed");
//                break;
//            }
//        }
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

    public static void setDayAlarm(Context context, Date time, boolean isOn) {
        cancelReminder(context);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent i = TaskService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
            calendar.set(Calendar.MINUTE, time.getMinutes());
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
            Log.i(TAG, "Day summary notification has changed");
            Log.i(TAG, calendar.getTime().toString());
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

    }

    private static void cancelReminder(Context context){
        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = TaskService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        pi.cancel();
    }



}
