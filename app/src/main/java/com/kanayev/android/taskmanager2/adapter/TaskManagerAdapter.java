package com.kanayev.android.taskmanager2.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kanayev.android.taskmanager2.ui.activity.AddTaskActivity;
import com.kanayev.android.taskmanager2.ui.activity.CreateTodoActivity;
import com.kanayev.android.taskmanager2.util.HelpUtils;
import com.kanayev.android.taskmanager2.R;
import com.kanayev.android.taskmanager2.model.TaskManagerDBHelper;
import com.kanayev.android.taskmanager2.service.TaskService;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManagerAdapter extends RecyclerView.Adapter<TaskManagerAdapter.TaskManagerViewHolder> {
    private static Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public TaskManagerAdapter(Activity activity, ArrayList<HashMap<String, String>> hashMaps) {
        TaskManagerAdapter.activity = activity;
        data = hashMaps;
    }

    public Object getItem(int position) {
        return position;
    }


    @Override
    public TaskManagerViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new TaskManagerViewHolder(LayoutInflater.from(activity).inflate(
                R.layout.task_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(final TaskManagerViewHolder taskManagerViewHolder, int position) {
        taskManagerViewHolder.task_image.setId(position);
        taskManagerViewHolder.task_name.setId(position);
        taskManagerViewHolder.task_date.setId(position);
        taskManagerViewHolder.task_image_solved.setId(position);

        HashMap<String, String> map = new HashMap<String, String>();
        map = data.get(position);

        try {
            taskManagerViewHolder.task_name.setText(map.get(CreateTodoActivity.KEY_TASK));
            taskManagerViewHolder.task_date.setText(map.get(CreateTodoActivity.KEY_DATE));
            taskManagerViewHolder.task_image_solved.setVisibility(HelpUtils.isSolved(map.get(CreateTodoActivity.KEY_DONE)) ? View.VISIBLE : View.GONE);

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            taskManagerViewHolder.task_image.setTextColor(color);
            taskManagerViewHolder.task_image.setText(Html.fromHtml("&#11044;"));


        } catch (Exception e) {
        }

        final HashMap<String, String> finalMap = map;
        final TaskManagerViewHolder tsk = taskManagerViewHolder;
        final int pos = position;

        final Intent i = new Intent(activity, AddTaskActivity.class);
        i.putExtra("isUpdate", true);
        i.putExtra("id", finalMap.get(CreateTodoActivity.KEY_ID));
        i.putExtra("task", finalMap.get(CreateTodoActivity.KEY_TASK));
        i.putExtra("date", finalMap.get(CreateTodoActivity.KEY_DATE));
        i.putExtra("isDone", finalMap.get(CreateTodoActivity.KEY_DONE));
        i.putExtra("description", finalMap.get(CreateTodoActivity.KEY_DESCRIPTION));
        i.putExtra("interval", finalMap.get(CreateTodoActivity.KEY_INTERVAL));

        TaskService.setTaskAlarm(activity, /*finalMap,*/ i);

        taskManagerViewHolder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(i);
            }
        });

        taskManagerViewHolder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                final String idd = i.getStringExtra("id");
                final String taskName = i.getStringExtra("task");

                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + taskName);
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        TaskService.cancelReminder(activity, Integer.parseInt(idd));
                        data.remove(pos);
                        notifyDataSetChanged();
                        tsk.db.removeTask(idd);
                        activity.recreate();

                        Toast.makeText(v.getContext(), "Task " + taskName + " Removed.", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.show();
                return true;
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TaskManagerViewHolder extends RecyclerView.ViewHolder {
        private TextView task_image;
        private TextView task_name, task_date;
        private ImageView task_image_solved;
        public View parentView;
        private TaskManagerDBHelper db;

        public TaskManagerViewHolder(View itemView) {
            super(itemView);
            this.db = new TaskManagerDBHelper(itemView.getContext());
            this.parentView = itemView;
            this.task_image = itemView.findViewById(R.id.task_image);
            this.task_name = itemView.findViewById(R.id.task_name);
            this.task_date = itemView.findViewById(R.id.task_date);
            this.task_image_solved = itemView.findViewById(R.id.task_image_solved);
        }
    }
}
