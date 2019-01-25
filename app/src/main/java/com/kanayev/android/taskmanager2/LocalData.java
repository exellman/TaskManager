package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LocalData {
    static Activity activity;
    private static HashMap<String, String> forInfo;

    public void LocalData(Activity a, HashMap<String, String> finalInfo){
        activity = a;
        forInfo = finalInfo;

        NotificationHelper.setReminder(activity, AlarmReceiver.class);
    }

    private Intent IntentTake(){
        final Intent in = new Intent(activity, AddTaskActivity.class);
        in.putExtra("isUpdate", true);
        in.putExtra("id", forInfo.get(CreateTodoActivity.KEY_ID));
        in.putExtra("task", forInfo.get(CreateTodoActivity.KEY_TASK));
        in.putExtra("date", forInfo.get(CreateTodoActivity.KEY_DATE));
        in.putExtra("description", forInfo.get(CreateTodoActivity.KEY_DESCRIPTION));

        return in;
    }

    public  Activity get_a(){
        return activity;
    }

    public int get_id(){
        int id = Integer.parseInt(IntentTake().getStringExtra("id"));

        return id;
    }

    public String get_name(){
        String task = IntentTake().getStringExtra("task");

        return task;
    }

    public String get_description(){
        String description = IntentTake().getStringExtra("description");

        return description;
    }

    public Date get_date(){

        String strDate = IntentTake().getStringExtra("date");

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

}
