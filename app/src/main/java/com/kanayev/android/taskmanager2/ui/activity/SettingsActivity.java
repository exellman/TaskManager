package com.kanayev.android.taskmanager2.ui.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kanayev.android.taskmanager2.R;
import com.kanayev.android.taskmanager2.util.SettingsPreferences;
import com.kanayev.android.taskmanager2.service.TaskService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText dateSummary;

    int startHour, startMinute, hourFinal, minuteFinal;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_settings);

        Switch switchSound = findViewById(R.id.switch_sound);
        Switch switchVibrate = findViewById(R.id.switch_vibrate);
        Switch switchSummary = findViewById(R.id.switch_day_notification);
        dateSummary = findViewById(R.id.set_notification_date);

        switchSound.setChecked(SettingsPreferences.getPrefSound(this));
        switchVibrate.setChecked(SettingsPreferences.getPrefVibration(this));
        switchSummary.setChecked(SettingsPreferences.getPrefSummary(this));
        Date dateFromPreferences = new Date(SettingsPreferences.getPrefTime(this));
        dateSummary.setText(dateFromPreferences.getHours() + ":" + (dateFromPreferences.getMinutes() < 10 ? '0' : "") + dateFromPreferences.getMinutes());
        menageViews(SettingsPreferences.getPrefSummary(this));

        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsPreferences.setPrefSound(getApplicationContext(), isChecked);
            }
        });

        switchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsPreferences.setPrefVibration(getApplicationContext(), isChecked);
            }
        });

        switchSummary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsPreferences.setPrefSummary(getApplicationContext(), isChecked);
                menageViews(isChecked);
            }
        });
    }

    public void chooseSummaryTime(View v) {
        Calendar c = Calendar.getInstance();
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, SettingsActivity.this, startHour, startMinute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        String timeStr = (hourFinal < 10 ? "0" + hourFinal : "" + hourFinal) + ":" + (minuteFinal < 10 ? "0" + minuteFinal : "" + minuteFinal);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date finalDate = null;
        try {
            finalDate = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        EditText set_notification_date = findViewById(R.id.set_notification_date);
        set_notification_date.setText(timeStr);
        assert finalDate != null;
        SettingsPreferences.setPrefTime(this, finalDate.getTime());
        TaskService.setDayAlarm(this, finalDate, SettingsPreferences.getPrefSummary(this));
    }

    private void menageViews(boolean isVisible) {
        dateSummary.setEnabled(isVisible);
        if (isVisible) {
            dateSummary.setTextColor(Color.parseColor("#000000"));
        } else {
            dateSummary.setTextColor(Color.parseColor("#1E1D23"));
        }
    }

    public void closeSettings(View v) {
        Toast.makeText(getApplicationContext(), "Settings Updated.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void openDaySummary(View v) {
        Intent intent = new Intent(this, DaySummaryActivity.class);
        startActivity(intent);
    }
}
