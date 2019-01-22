package com.kanayev.android.taskmanager2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Function {

    public static String Epoch2DateString(String epochSeconds, String formatString) {
        Date updatedate = new Date(Long.parseLong(epochSeconds));
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);
    }


    public static Calendar Epoch2Calender(String epochSeconds) {
        Date updatedate = new Date(Long.parseLong(epochSeconds));
        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedate);

        return cal;
    }

    public static boolean isSolved(String s) {
        boolean isChecked;
        if (s.compareTo("true") == 0) {
            isChecked = true;
        } else {
            isChecked = false;
        }
        return isChecked;
    }
}