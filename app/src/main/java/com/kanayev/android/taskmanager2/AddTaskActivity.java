package com.kanayev.android.taskmanager2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TaskManagerDBHelper mydb;
    int startYear, startMonth, startDay, startHour, startMinute;
    int hourFinal, minuteFinal;
    String dateFinal;
    String nameFinal;
    String solvedFinal;
    String descriptionFinal;

    Intent intent;
    Boolean isUpdate;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_add_new);

        mydb = new TaskManagerDBHelper(getApplicationContext());
        intent = getIntent();
        isUpdate = intent.getBooleanExtra("isUpdate", false);

        dateFinal = todayDateString();
        Date your_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(your_date);

        if (isUpdate) {
            set_update();
        }
    }

    public void set_update() {
        id = intent.getStringExtra("id");
        TextView toolbar_task_add_title = (TextView) findViewById(R.id.toolbar_task_add_title);
        EditText task_name = (EditText) findViewById(R.id.task_name);
        EditText task_date = (EditText) findViewById(R.id.task_date);
        EditText task_description = (EditText) findViewById(R.id.task_description);
        CheckBox task_solved_checkbox = (CheckBox) findViewById(R.id.task_solved_checkbox);

        toolbar_task_add_title.setText("Update");
        Cursor task = mydb.getDataSpecific(id);

        if (task != null) {
            task.moveToFirst();

            task_name.setText(task.getString(1).toString());
            Calendar cal = Function.Epoch2Calender(task.getString(2).toString());
            startYear = cal.get(Calendar.YEAR);
            startMonth = cal.get(Calendar.MONTH);
            startDay = cal.get(Calendar.DAY_OF_MONTH);
            task_date.setText(Function.Epoch2DateString(task.getString(2).toString(), "dd/MM/yyyy HH:mm"));
            task_solved_checkbox.setChecked(Function.isSolved(task.getString(3).toString()));
            task_description.setText(task.getString(4).toString());
        }
    }

    public String todayDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return dateFormat.toString();

    }

    public void closeAddTask(View v) {
        finish();
    }

    public void doneAddTask(View v) {
        int errorStep = 0;
        EditText task_name = (EditText) findViewById(R.id.task_name);
        EditText task_date = (EditText) findViewById(R.id.task_date);
        EditText task_description = (EditText) findViewById(R.id.task_description);
        CheckBox task_solved_checkbox = (CheckBox) findViewById(R.id.task_solved_checkbox);
        nameFinal = task_name.getText().toString();
        dateFinal = task_date.getText().toString();
        descriptionFinal = task_description.getText().toString();
        if (task_solved_checkbox.isChecked())
            solvedFinal = "true";
        else solvedFinal = "false";

        if (nameFinal.trim().length() < 1) {
            errorStep++;
            task_name.setError("Provide a task name.");
        }

        if (dateFinal.trim().length() < 1) {
            errorStep++;
            task_date.setError("Provide a specific date.");
        }

        if (errorStep == 0) {
            if (isUpdate) {
                mydb.updateContact(id, nameFinal, dateFinal, solvedFinal, descriptionFinal);
                Toast.makeText(getApplicationContext(), "Task Updated.", Toast.LENGTH_SHORT).show();
            } else {
                mydb.insertContact(nameFinal, dateFinal, solvedFinal, descriptionFinal);
                Toast.makeText(getApplicationContext(), "Task Added.", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseDate(View v){
        Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, AddTaskActivity.this, startYear, startMonth, startDay);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        startYear = year;
        startMonth = month;
        startDay = dayOfMonth;

        Calendar c = Calendar.getInstance();
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this, AddTaskActivity.this, startHour, startMinute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        int monthAddOne = startMonth + 1;

        String date = (startDay < 10 ? "0" + startDay : "" + startDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                startYear;

        String time = (hourFinal < 10 ? "0" + hourFinal : ""  +hourFinal) + ":" + (minuteFinal < 10 ? "0" + minuteFinal : "" + minuteFinal);
        String finalDate = date + " " + time;

        EditText task_date = (EditText) findViewById(R.id.task_date);
        task_date.setText(finalDate);
    }
}