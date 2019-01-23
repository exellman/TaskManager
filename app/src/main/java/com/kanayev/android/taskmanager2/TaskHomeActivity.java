package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskHomeActivity extends AppCompatActivity {

    Activity activity;
    TaskDBHelper mydb;
    NoScrollRecyclerView taskListBefore, taskListToday, taskListTomorrow, taskListUpcoming;
    NestedScrollView scrollView;
    ProgressBar loader;
    TextView beforeText, todayText, tomorrowText, upcomingText;
    ArrayList<HashMap<String, String>> beforeList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> todayList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> upcomingList = new ArrayList<HashMap<String, String>>();

    public static String KEY_ID = "id";
    public static String KEY_TASK = "task";
    public static String KEY_DATE = "date";
    public static String KEY_SOLVED = "isSolved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_home);

        activity = TaskHomeActivity.this;
        mydb = new TaskDBHelper(activity);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        loader = (ProgressBar) findViewById(R.id.loader);
        taskListBefore = (NoScrollRecyclerView) findViewById(R.id.taskListBefore);
        taskListToday = (NoScrollRecyclerView) findViewById(R.id.taskListToday);
        taskListTomorrow = (NoScrollRecyclerView) findViewById(R.id.taskListTomorrow);
        taskListUpcoming = (NoScrollRecyclerView) findViewById(R.id.taskListUpcoming);

        beforeText = (TextView) findViewById(R.id.beforeText);
        todayText = (TextView) findViewById(R.id.todayText);
        tomorrowText = (TextView) findViewById(R.id.tomorrowText);
        upcomingText = (TextView) findViewById(R.id.upcomingText);
    }

    public void openAddTask(View v) {
        Intent i = new Intent(this, AddTaskActivity.class);
        startActivity(i);
    }

    public void populateData() {
        mydb = new TaskDBHelper(activity);
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        LoadTask loadTask = new LoadTask();
        loadTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateData();

    }

    class LoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            beforeList.clear();
            todayList.clear();
            tomorrowList.clear();
            upcomingList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            //Load BEFORE
            Cursor before = mydb.getDataBefore();
            loadDataList(before, beforeList);

            //Load TODAY
            Cursor today = mydb.getDataToday();
            loadDataList(today, todayList);


            //Load TOMORROW
            Cursor tomorrow = mydb.getDataTomorrow();
            loadDataList(tomorrow, tomorrowList);

            //Load UPCOMING
            Cursor upcoming = mydb.getDataUpcoming();
            loadDataList(upcoming, upcomingList);

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            loadRecyclerView(taskListBefore, beforeList);
            loadRecyclerView(taskListToday, todayList);
            loadRecyclerView(taskListTomorrow, tomorrowList);
            loadRecyclerView(taskListUpcoming, upcomingList);

            if (beforeList.size() > 0) {
                beforeText.setVisibility(View.VISIBLE);
            } else {
                beforeText.setVisibility(View.GONE);
            }

            if (todayList.size() > 0) {
                todayText.setVisibility(View.VISIBLE);
            } else {
                todayText.setVisibility(View.GONE);
            }

            if (tomorrowList.size() > 0) {
                tomorrowText.setVisibility(View.VISIBLE);
            } else {
                tomorrowText.setVisibility(View.GONE);
            }

            if (upcomingList.size() > 0) {
                upcomingText.setVisibility(View.VISIBLE);
            } else {
                upcomingText.setVisibility(View.GONE);
            }

            loader.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                HashMap<String, String> mapToday = new HashMap<String, String>();
                mapToday.put(KEY_ID, cursor.getString(0).toString());
                mapToday.put(KEY_TASK, cursor.getString(1).toString());
                mapToday.put(KEY_DATE, Function.Epoch2DateString(cursor.getString(2).toString(), "dd/MM/yyyy HH:mm"));
                mapToday.put(KEY_SOLVED, cursor.getString(3).toString());
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadRecyclerView(final RecyclerView recyclerView, final ArrayList<HashMap<String, String>> dataList) {
        final ListTaskAdapter adapter = new ListTaskAdapter(activity, dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
