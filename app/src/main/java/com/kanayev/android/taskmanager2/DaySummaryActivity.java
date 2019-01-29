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

public class DaySummaryActivity extends AppCompatActivity {

    Activity activity;
    TaskManagerDBHelper mydb;
    NoScrollRecyclerView taskListDaySummary;
    NestedScrollView scrollView;
    ProgressBar loader;
    TextView daySummaryText;
    ArrayList<HashMap<String, String>> daySummaryList = new ArrayList<HashMap<String, String>>();

    int tasksDone = 0;

    public static String KEY_ID = "id";
    public static String KEY_TASK = "task";
    public static String KEY_DATE = "date";
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_SOLVED = "isSolved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_day_summary);

        activity = DaySummaryActivity.this;
        mydb = new TaskManagerDBHelper(activity);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView_day_summary);
        loader = (ProgressBar) findViewById(R.id.loader_day_summary);
        taskListDaySummary = (NoScrollRecyclerView) findViewById(R.id.taskListDaySummary);

        daySummaryText = (TextView) findViewById(R.id.daySummaryText);
    }

    public void openAddTask(View v) {
        Intent i = new Intent(this, AddTaskActivity.class);
        startActivity(i);
    }

    public void populateData() {
        mydb = new TaskManagerDBHelper(activity);
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

    public void closeDaySummary(View view) {
        finish();
    }

    public class LoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            daySummaryList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            //Load TODAY
            Cursor today = mydb.getDataToday();
            loadDataList(today, daySummaryList);

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            loadRecyclerView(taskListDaySummary, daySummaryList);

            if (daySummaryList.size() > 0) {
                daySummaryText.setText("For today you have " + daySummaryList.size() + " task(s). You still should done " + (daySummaryList.size() - tasksDone)  + " task(s).");
            } else {
                daySummaryText.setText("You have no tasks for today");
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
                mapToday.put(KEY_DESCRIPTION, cursor.getString(4).toString());
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadRecyclerView(final RecyclerView recyclerView, ArrayList<HashMap<String, String>> dataList) {
        final TaskManagerAdapter adapter = new TaskManagerAdapter(activity, dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
