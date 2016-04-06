package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.oulu.daussy.broommate.Activity.AddTaskActivity;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Helper.ExpandableListAdapter;
import com.oulu.daussy.broommate.Helper.ProfilePictureView;
import com.oulu.daussy.broommate.Model.Task;
import com.oulu.daussy.broommate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TabFragmentTasks extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // Button which allow us to add a new task
    private FloatingActionButton addButton;
    // Custom adapter to create an Expandable ListView
    private ExpandableListAdapter listAdapter;
    // Expandable list view which will contains the task
    private ExpandableListView listView;
    // DataHeader are the main categories (here, todo, doing, done)
    private List<String> listDataHeader;
    // DataChild contains the object to add in the categories
    private HashMap<String, List<Task>> listDataChild;
    // to fetch the data
    private String JSON_STRING;
    // to refresh by pulling down the view
    private SwipeRefreshLayout swipeRefreshLayout;
    // to remember on which child we clicked last
    private View lastClicked;
    // to remember which group are expanded or nto
    private boolean[] expandedGroup;


    public TabFragmentTasks() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_tasks, container, false);

        expandedGroup = new boolean[3];
        Arrays.fill(expandedGroup, Boolean.FALSE);

        addButton = (FloatingActionButton) view.findViewById(R.id.fab);


        addButton.setOnClickListener(new View.OnClickListener() {
            // When we click on the button, we are sent the activity which allows us to add a new task
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), AddTaskActivity.class);
                startActivity(myIntent);
            }
        });

        listView = (ExpandableListView) view.findViewById(R.id.expandableListView);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchTask();
            }
        });


        addButton.bringToFront();

        // Listview Group expanded listener
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                expandedGroup[groupPosition] = Boolean.TRUE;
            }
        });

        // Listview Group collasped listener
        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                expandedGroup[groupPosition] = Boolean.FALSE;
            }
        });


        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String section = listDataHeader.get(groupPosition);
                final View view = listAdapter.getChildView(groupPosition, childPosition, false, v, parent);
                final Task task = listDataChild.get(section).get(childPosition);

                // We show the delete button onChildClick
                // We remember this child to hide delete button when we click on another one
                if (lastClicked != null) {
                    lastClicked.findViewById(R.id.imageButtonDelete).setVisibility(View.INVISIBLE);
                    lastClicked.invalidate();
                }
                lastClicked = v;
                lastClicked.findViewById(R.id.imageButtonDelete).setVisibility(View.VISIBLE);


                //ProfilePictureView profilePictureView = (ProfilePictureView) v.findViewById(R.id.owner);

                showTaskInformation(task, v);




                final boolean[] toDelete = new boolean[1];
                v.findViewById(R.id.imageButtonDelete).setOnClickListener(new View.OnClickListener() {
                    // read more on http://stackoverflow.com/a/31057956/4307336
                    // to know why we delete the item as soon as tue user taps the delete button
                    @Override
                    public void onClick(View v) {
                        String s = "Delete task in progress...";
                        //Hide the whole line
                        //view.setVisibility(View.GONE);
                        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        final View delButton = v;
                        delButton.setVisibility(View.INVISIBLE);
/*
                        Snackbar.make(v, s, Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Restore the line
                                //view.setVisibility(View.VISIBLE);
                                view.setBackgroundColor(Color.TRANSPARENT);
                                delButton.setVisibility(View.VISIBLE);
                                String s = "Task restored";
                                Snackbar.make(v, s, Snackbar.LENGTH_LONG).show();
                            }
                        }).show();
*/
                        final Snackbar snackbar = Snackbar.make(v, "Delete task in progress...", Snackbar.LENGTH_INDEFINITE);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                view.setBackgroundColor(Color.TRANSPARENT);
                                delButton.setVisibility(View.VISIBLE);
                                String s = "Task restored";
                                Snackbar snackbarCanceld = Snackbar.make(v, s, Snackbar.LENGTH_LONG);
                                View snackbarView = snackbarCanceld.getView();
                                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                snackbarCanceld.show();
                            }
                        });
                        snackbar.show();


                    }
                });

                return false;
            }
        });

        //Load data

        //fetchTask();

        return view;
    }

    private void prepareListData() {

        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Task>>();

        listDataHeader.add("TODO");
        listDataHeader.add("DOING");
        listDataHeader.add("DONE");

        List<Task> todo = new ArrayList<Task>();
        List<Task> doing = new ArrayList<Task>();
        List<Task> done = new ArrayList<Task>();


        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                Task taskToSort = new Task();

                taskToSort.setId(Integer.parseInt(jo.getString(Config.KEY_TASK_ID)));
                taskToSort.setTitle(jo.getString(Config.KEY_TASK_NAME));
                taskToSort.setPriority(jo.getString(Config.KEY_TASK_PRIORITY));
                taskToSort.setState(jo.getString(Config.KEY_TASK_STATE));
                taskToSort.setOwner(jo.getString(Config.KEY_TASK_OWNER));
                taskToSort.setWorker(jo.getString(Config.KEY_TASK_WORKER));
                taskToSort.setDate_start(jo.getString(Config.KEY_TASK_DATE_START));
                taskToSort.setOwner_name(jo.getString(Config.KEY_TASK_OWNER_NAME));
                taskToSort.setDate_end(jo.getString(Config.KEY_TASK_DATE_END));
                taskToSort.setWorker_name(jo.getString(Config.KEY_TASK_WORKER_NAME));

                switch (taskToSort.getState()) {
                    case "TODO":
                        todo.add(taskToSort);
                        break;
                    case "DOING":
                        doing.add(taskToSort);
                        break;
                    case "DONE":
                        done.add(taskToSort);
                        break;

                }
            }

            listDataChild.put(listDataHeader.get(0), todo);
            listDataChild.put(listDataHeader.get(1), doing);
            listDataChild.put(listDataHeader.get(2), done);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        listView.setAdapter(listAdapter);

        for (int i = 0; i < expandedGroup.length; i++) {
            if (expandedGroup[i])
                listView.expandGroup(i);
        }

    }


    private void fetchTask() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            //ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);
                //loading = ProgressDialog.show(getContext(), "Fetching Data", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                swipeRefreshLayout.setRefreshing(false);
                //loading.dismiss();
                JSON_STRING = s;
                prepareListData();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_ALL_TASKS);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    private void showTaskInformation(Task task, View v) {
        int priority = 0;
        switch (task.getPriority()) {
            case "LOW":
                priority = Color.GREEN;
                break;
            case "MEDIUM":
                priority = Color.YELLOW;
                break;
            case "HIGH":
                priority = Color.RED;
                break;
        }
        String date = task.getDate_start().split(" ")[0];
        String time = task.getDate_start().split(" ")[1];

        //Making multi colored text color of SnackBar text
        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        snackbarText.append("Created by ");
        int boldStart = snackbarText.length();
        snackbarText.append(task.getOwner_name());
        //snackbarText.setSpan(new ForegroundColorSpan(Color.BLUE), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        snackbarText.append("\non ");
        boldStart = snackbarText.length();
        snackbarText.append(date);
        snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        snackbarText.append(" at ");
        boldStart = snackbarText.length();
        snackbarText.append(time);
        snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        snackbarText.append(" \nwith a ");
        boldStart = snackbarText.length();
        snackbarText.append(task.getPriority());
        snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        snackbarText.append(" priority.");

        if (!task.getWorker().isEmpty() && task.getState().equals("DOING")) {
            snackbarText.append("\n\nIn progress by ");
            boldStart = snackbarText.length();
            snackbarText.append(task.getWorker_name());
            snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!task.getWorker().isEmpty() && task.getState().equals("DONE")) {
            date = task.getDate_end().split(" ")[0];
            time = task.getDate_end().split(" ")[1];
            snackbarText.append("\n\nDone by ");
            boldStart = snackbarText.length();
            snackbarText.append(task.getWorker_name());
            snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackbarText.append("\non ");
            boldStart = snackbarText.length();
            snackbarText.append(date);
            snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snackbarText.append(" at ");
            boldStart = snackbarText.length();
            snackbarText.append(time);
            snackbarText.setSpan(new StyleSpan(Typeface.BOLD), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }



        final Snackbar snackbar = Snackbar.make(v, snackbarText, Snackbar.LENGTH_INDEFINITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        final Button snackbarActionButton = (Button) snackbarView.findViewById(android.support.design.R.id.snackbar_action);

        //TODO Add button to dismiss, but this way the floating action button stays up..
/*
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
*/
        textView.setMaxLines(10);  // show multiple line
        snackbar.show();
    }


    @Override
    public void onRefresh() {
        fetchTask();
    }
}
