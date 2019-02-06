package com.kanayev.android.taskmanager2.util;

import android.content.Context;
import android.preference.PreferenceManager;

public class SettingsPreferences {

    private static final String PREF_TIME = "pref_time";
    private static final String PREF_VIBRATION = "pref_vibration";
    private static final String PREF_SOUND = "pref_sound";
    private static final String PREF_SUMMARY = "pref_summary";
    private static final String PREF_CHANGE_TASKS = "pref_change_tasks";

    public static Long getPrefTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PREF_TIME, 1546596028480L);
    }

    public static void setPrefTime(Context context, long query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PREF_TIME, query)
                .apply();
    }

    public static boolean getPrefVibration(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_VIBRATION, false);
    }

    public static void setPrefVibration(Context context, boolean query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_VIBRATION, query)
                .apply();
    }

    public static boolean getPrefSound(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SOUND, false);
    }

    public static void setPrefSound(Context context, boolean query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SOUND, query)
                .apply();
    }

    public static boolean getPrefSummary(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SUMMARY, true);
    }

    public static void setPrefSummary(Context context, boolean query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SUMMARY, query)
                .apply();
    }


    public static Long getPrefCheck(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PREF_CHANGE_TASKS, 1L);
    }

    public static void setPrefCheck(Context context, long query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PREF_CHANGE_TASKS, query)
                .apply();
    }

}
