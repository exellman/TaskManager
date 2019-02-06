package com.kanayev.android.taskmanager2.ui.activity;

import android.annotation.SuppressLint;
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

import com.kanayev.android.taskmanager2.R;
import com.kanayev.android.taskmanager2.ui.NoScrollRecyclerView;
import com.kanayev.android.taskmanager2.util.HelpUtils;
import com.kanayev.android.taskmanager2.model.TaskManagerDBHelper;
import com.kanayev.android.taskmanager2.adapter.TaskManagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DaySummaryActivity extends AppCompatActivity {

    int tasksDone;

    Activity activity;
    TaskManagerDBHelper mydb;
    NoScrollRecyclerView taskListDaySummary;
    NestedScrollView scrollView;
    ProgressBar loader;
    TextView daySummaryText;
    ArrayList<HashMap<String, String>> daySummaryList;

    public static String KEY_ID = "id";
    public static String KEY_TASK = "task";
    public static String KEY_DATE = "date";
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_DONE = "isDone";
    public static String KEY_INTERVAL = "interval";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_day_summary);

        daySummaryList = new ArrayList<>();
        activity = DaySummaryActivity.this;
        mydb = new TaskManagerDBHelper(getApplicationContext());
        scrollView = findViewById(R.id.scrollView_day_summary);
        loader = findViewById(R.id.loader_day_summary);
        taskListDaySummary = findViewById(R.id.taskListDaySummary);

        daySummaryText = findViewById(R.id.daySummaryText);
    }

    public void openAddTask(View v) {
        Intent i = new Intent(this, AddTaskActivity.class);
        startActivity(i);
    }

    public void populateData() {
        mydb = new TaskManagerDBHelper(getApplicationContext());
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


    @SuppressLint("StaticFieldLeak")
    public class LoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            tasksDone = 0;

            daySummaryList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            //Load TODAY
            Cursor today = mydb.getDataToday();
            loadDataList(today, daySummaryList);

            return xml;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String xml) {

            loadRecyclerView(taskListDaySummary, daySummaryList);

            if (daySummaryList.size() > 0) {
                daySummaryText.setText("For today you have " + daySummaryList.size() + " task(s). You still should done " + (daySummaryList.size() - tasksDone) + " task(s).");
            } else {
                daySummaryText.setText(R.string.day_summary_no_tasks);
            }

            loader.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                HashMap<String, String> mapToday = new HashMap<>();
                mapToday.put(KEY_ID, cursor.getString(0));
                mapToday.put(KEY_TASK, cursor.getString(1));
                mapToday.put(KEY_DATE, HelpUtils.Epoch2DateString(cursor.getString(2), "dd/MM/yyyy HH:mm"));
                mapToday.put(KEY_DONE, cursor.getString(3));
                mapToday.put(KEY_DESCRIPTION, cursor.getString(4));
                mapToday.put(KEY_INTERVAL, cursor.getString(5));

                if (Objects.requireNonNull(mapToday.get(KEY_DONE)).compareTo("true") == 0) {
                    tasksDone++;
                }

                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadRecyclerView(final RecyclerView recyclerView, ArrayList<HashMap<String, String>> dataList) {
        final TaskManagerAdapter adapter = new TaskManagerAdapter(getApplicationContext(), dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }
}
