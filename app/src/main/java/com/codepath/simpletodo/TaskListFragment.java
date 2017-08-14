package com.codepath.simpletodo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment {

    private static final String TAG = "TaskListFragment";
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.tasklist_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {
        List<Task> tasks = TaskDatabaseUtil.getIncompleteTasks();
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(taskAdapter);
        } else {
            taskAdapter.setTasks(tasks);
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_task:
                Task task = new Task();
                task.save();
                startTaskPagerActivity(task.getUuid());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startTaskPagerActivity(UUID taskId) {
        Intent intent = TaskPagerActivity.newIntent(getActivity(), taskId);
        startActivity(intent);
    }

    private class ListItemTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txvTaskName;
        private TextView txvTaskCompletionDate;
        private TextView txvTaskCategory;
        private CheckBox chbIsComplete;
        private Task task;
        public ListItemTaskViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txvTaskName = (TextView) itemView.findViewById(R.id.txvTaskName);
            txvTaskCompletionDate = (TextView) itemView.findViewById(R.id.txvTaskCompletionDate);
            txvTaskCategory = (TextView) itemView.findViewById(R.id.txvTaskCategory);
            chbIsComplete = (CheckBox) itemView.findViewById(R.id.chbIsTaskComplete);
            chbIsComplete.setOnCheckedChangeListener(new TaskCompleteListener());
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick taskId = " + task.getUuid());
            startTaskPagerActivity(task.getUuid());
        }

        public void bind(Task task) {
            this.task = task;
            txvTaskName.setText(this.task.getName());
            txvTaskCompletionDate.setText(this.task.getDate().toString());
            txvTaskCategory.setText(this.task.getCategory());
            chbIsComplete.setChecked(this.task.isComplete());
        }

        private class TaskCompleteListener implements CompoundButton.OnCheckedChangeListener {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    task.setComplete(chbIsComplete.isChecked());
                    task.save();
                    Toast.makeText(getActivity(), task.getName() + " Completed", Toast.LENGTH_LONG)
                            .show();
                    updateUI();
                }
            }
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<ListItemTaskViewHolder> {
        private List<Task> tasks;

        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        @Override
        public ListItemTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_task, parent, false);
            return new ListItemTaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ListItemTaskViewHolder holder, int position) {
            Log.i(TAG, "onBindViewHolder position = " + position);
            holder.bind(tasks.get(position));
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }

}
