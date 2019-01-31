package com.kanayev.android.taskmanager2.ui.activity;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kanayev.android.taskmanager2.R;
import com.kanayev.android.taskmanager2.util.SettingsPreferences;
import com.kanayev.android.taskmanager2.ui.NoScrollRecyclerView;
import com.kanayev.android.taskmanager2.util.HelpUtils;
import com.kanayev.android.taskmanager2.model.TaskManagerDBHelper;
import com.kanayev.android.taskmanager2.adapter.TaskManagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateTodoActivity extends AppCompatActivity {

    long clck;

    Activity activity;
    TaskManagerDBHelper mydb;
    NoScrollRecyclerView taskListBefore, taskListToday, taskListTomorrow, taskListUpcoming;
    NestedScrollView scrollView;
    ProgressBar loader;
    TextView beforeText, todayText, tomorrowText, upcomingText;
    ImageView changeTasks;
    ArrayList<HashMap<String, String>> beforeList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> todayList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> upcomingList = new ArrayList<HashMap<String, String>>();

    public static String KEY_ID = "id";
    public static String KEY_TASK = "task";
    public static String KEY_DATE = "date";
    public static String KEY_DONE = "isDone";
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_INTERVAL = "interval";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_home);

        clck = SettingsPreferences.getPrefCheck(this);
        changeTasks = (ImageView) findViewById(R.id.changeTasks);
        activity = CreateTodoActivity.this;
        mydb = new TaskManagerDBHelper(activity);
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

        changeTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoadTask loadTask = new LoadTask();
                if (clck == 0) {
                    loadTask.execute();
                    SettingsPreferences.setPrefCheck(getApplicationContext(), clck);
                    changeTasks.setImageResource(R.drawable.ic_action_to_do);
                    clck++;
                } else if (clck == 1) {
                    loadTask.execute();
                    SettingsPreferences.setPrefCheck(getApplicationContext(), clck);
                    changeTasks.setImageResource(R.drawable.ic_action_done_tasks);
                    clck++;
                } else if (clck == 2) {
                    loadTask.execute();
                    SettingsPreferences.setPrefCheck(getApplicationContext(), clck);
                    changeTasks.setImageResource(R.drawable.ic_action_all_tasks);
                    clck = clck - 2;
                }

            }
        });
    }

    public void openSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
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


    public class LoadTask extends AsyncTask<String, Void, String> {
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
                mapToday.put(KEY_DATE, HelpUtils.Epoch2DateString(cursor.getString(2).toString(), "dd/MM/yyyy HH:mm"));
                mapToday.put(KEY_DONE, cursor.getString(3).toString());
                mapToday.put(KEY_DESCRIPTION, cursor.getString(4).toString());
                mapToday.put(KEY_INTERVAL, cursor.getString(5).toString());

                if (clck == 0) {
                    dataList.add(mapToday);
                } else if (clck == 1) {
                    if (mapToday.get(KEY_DONE).compareTo("false") == 0) {
                        dataList.add(mapToday);
                    }
                } else if (clck == 2) {
                    if (mapToday.get(KEY_DONE).compareTo("true") == 0) {
                        dataList.add(mapToday);
                    }
                }
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
