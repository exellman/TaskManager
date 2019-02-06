package com.kanayev.android.taskmanager2.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HelpUtils {

    public static String Epoch2DateString(String epochSeconds, String formatString) {
        Date updateDate = new Date(Long.parseLong(epochSeconds));
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
        return format.format(updateDate);
    }

    public static Calendar Epoch2Calender(String epochSeconds) {
        Date updateDate = new Date(Long.parseLong(epochSeconds));
        Calendar cal = Calendar.getInstance();
        cal.setTime(updateDate);

        return cal;
    }

    public static boolean isSolved(String s) {
        boolean isChecked;
        isChecked = s.compareTo("true") == 0;
        return isChecked;
    }
}