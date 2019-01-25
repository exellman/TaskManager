package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ForTakeInfo {
    static Activity activity;
    Intent intentInfo;


    public void ForTakeInfo(Activity a, Intent intent){
        activity = a;
        intentInfo = intent;

        NotificationScheduler.setReminder(activity, intentInfo, AlarmReceiver.class);
    }

    public Intent IntentTake(){

        return intentInfo;
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
