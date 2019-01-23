package com.kanayev.android.taskmanager2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;



public class ListTaskAdapter extends RecyclerView.Adapter<ListTaskAdapter.ListTaskViewHolder> {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public ListTaskAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
    }

    public Object getItem(int position) {
        return position;
    }


    @NonNull
    @Override
    public ListTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ListTaskViewHolder(LayoutInflater.from(activity).inflate(
                R.layout.task_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListTaskViewHolder listTaskViewHolder, final int position) {
        listTaskViewHolder.task_image.setId(position);
        listTaskViewHolder.task_name.setId(position);
        listTaskViewHolder.task_date.setId(position);
        listTaskViewHolder.task_image_solved.setId(position);

        HashMap<String, String> map = new HashMap<String, String>();
        map = data.get(position);

        try{
            listTaskViewHolder.task_name.setText(map.get(TaskHomeActivity.KEY_TASK));
            listTaskViewHolder.task_date.setText(map.get(TaskHomeActivity.KEY_DATE));
            listTaskViewHolder.task_image_solved.setVisibility(Function.isSolved(map.get(TaskHomeActivity.KEY_SOLVED)) ? View.VISIBLE : View.GONE);

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            listTaskViewHolder.task_image.setTextColor(color);
            listTaskViewHolder.task_image.setText(Html.fromHtml("&#11044;"));


        }catch(Exception e) {}

        final HashMap<String, String> finalMap = map;

        listTaskViewHolder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, AddTaskActivity.class);
                i.putExtra("isUpdate", true);
                i.putExtra("id", (finalMap.get(TaskHomeActivity.KEY_ID)));
               
                activity.startActivity(i);
            }
        });

        listTaskViewHolder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final Intent i = new Intent(activity, AddTaskActivity.class);
                i.putExtra("id", finalMap.get(TaskHomeActivity.KEY_ID));
                i.putExtra("task", finalMap.get(TaskHomeActivity.KEY_TASK));


                final String taskName = i.getStringExtra("task");
                final String idd = i.getStringExtra("id");

                AlertDialog.Builder adb=new AlertDialog.Builder(activity);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + taskName);
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        data.remove(position);
                        listTaskViewHolder.db.removeTask(idd);
                        notifyDataSetChanged();
                        activity.recreate();

                        Toast.makeText(v.getContext(), "Task " + taskName + " Removed.", Toast.LENGTH_SHORT).show();
                    }});
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



    public class ListTaskViewHolder extends RecyclerView.ViewHolder {
        private TextView task_image;
        private TextView task_name, task_date;
        private ImageView task_image_solved;
        private View parentView;
        private TaskDBHelper db;

        public ListTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.db = new TaskDBHelper(itemView.getContext());
            this.parentView = itemView;
            this.task_image = (TextView) itemView.findViewById(R.id.task_image);
            this.task_name = (TextView) itemView.findViewById(R.id.task_name);
            this.task_date = (TextView) itemView.findViewById(R.id.task_date);
            this.task_image_solved = (ImageView) itemView.findViewById(R.id.task_image_solved);

        }
    }
}
