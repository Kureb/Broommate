package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Helper.ExpandableListAdapter;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.Model.GoogleCloudMessage;
import com.oulu.daussy.broommate.Model.Task;
import com.oulu.daussy.broommate.Model.User;
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
    // DataHeader are the main categories
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

    private CurrentUser currentUser = CurrentUser.getInstance();


    public TabFragmentTasks() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_tasks, container, false);

        expandedGroup = new boolean[3];
        Arrays.fill(expandedGroup, Boolean.FALSE);

        addButton = (FloatingActionButton) view.findViewById(R.id.fab);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            /*
             * New dialog to add a new task
             */
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.activity_add_task);

                Button cancelButton = (Button) dialog.findViewById(R.id.buttonCancel);
                Button addButtonTask = (Button) dialog.findViewById(R.id.buttonAdd);
                final EditText titleTask = (EditText) dialog.findViewById(R.id.titleTask);
                // Spinner
                List<String> spinnerArray =  new ArrayList<String>();
                spinnerArray.add("LOW");
                spinnerArray.add("MEDIUM");
                spinnerArray.add("HIGH");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerPriority);
                spinner.setAdapter(adapter);

                //Cancel = back
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                addButtonTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = titleTask.getText().toString().trim();
                        final String priority = spinner.getSelectedItem().toString();
                        final String owner = currentUser.getFacebook_id();
                        addTask(v, name, priority, owner);
                        dialog.dismiss();
                        onRefresh();
                    }
                });

                dialog.setTitle("New task");
                dialog.show();
            }

            private void addTask(final View v, String nameTask, String priorityTask, String ownerTask) {

                final String name = nameTask;
                final String priority = priorityTask;
                final String owner = ownerTask;


                class AddTask extends AsyncTask<Void, Void, String>{

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                    }

                    @Override
                    protected String doInBackground(Void... v) {
                        HashMap<String,String> params = new HashMap<>();

                        params.put(Config.KEY_TASK_NAME, name);
                        params.put(Config.KEY_TASK_PRIORITY, priority);
                        params.put(Config.KEY_TASK_OWNER, owner);
                        params.put(Config.KEY_USER_GROUP_KEY, currentUser.getGroupKey());

                        RequestHandler rh = new RequestHandler();
                        String res = rh.sendPostRequest(Config.URL_ADD_TASK, params);

                        return res;
                    }
                }

                AddTask at = new AddTask();
                at.execute();

            }


        });


        listView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        addButton.attachToListView(listView);

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

        // Listview Group collapsed listener
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
                    lastClicked.findViewById(R.id.imageNextStep).setVisibility(View.INVISIBLE);
                    lastClicked.invalidate();
                }
                lastClicked = v;
                lastClicked.findViewById(R.id.imageButtonDelete).setVisibility(View.VISIBLE);
                lastClicked.findViewById(R.id.imageNextStep).setVisibility(View.VISIBLE);

                if (task.getState().equals(Config.STATE_DONE))
                    v.findViewById(R.id.imageNextStep).setVisibility(View.INVISIBLE);

                if (task.getState().equals(Config.STATE_DOING) && !task.workerIsMe())
                    v.findViewById(R.id.imageNextStep).setVisibility(View.INVISIBLE);

                if (task.getState().equals(Config.STATE_TODO) && !task.ownerIsMe())
                    v.findViewById(R.id.imageButtonDelete).setVisibility(View.INVISIBLE);

                if (task.getState().equals(Config.STATE_DOING) && (!task.workerIsMe() && !task.ownerIsMe()))
                    v.findViewById(R.id.imageButtonDelete).setVisibility(View.INVISIBLE);

                if (task.getState().equals(Config.STATE_DONE) && (!task.workerIsMe() && !task.ownerIsMe()))
                    v.findViewById(R.id.imageButtonDelete).setVisibility(View.INVISIBLE);

                showTaskInformation(task, v);


                v.findViewById(R.id.imageButtonDelete).setOnClickListener(new View.OnClickListener() {
                    // read more on http://stackoverflow.com/a/31057956/4307336
                    // to know why we delete the item as soon as the user taps the delete button
                    @Override
                    public void onClick(View v) {
                        goPreviousState(task);
                    }
                });


                v.findViewById(R.id.imageNextStep).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goNextState(task);
                    }
                });

                return false;
            }
        });
        return view;
    }

    /**
     * Change the state of the selected task to the next one
     */
    private void goNextState(final Task task) {
        final String nextState = task.getNextState();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeTaskStatus(task, nextState, currentUser.getFacebook_id());
                if (!task.getOwner().equals(currentUser.getName()))
                    sendNotification(task);
                onRefresh();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(nextState.equals(Config.STATE_DOING) ? "Assign task to myself?" : "Mark task as done?");
        alertDialog.show();
    }


    public void sendNotification(final Task task){
        class NotificationTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... p) {
                GoogleCloudMessage message = new GoogleCloudMessage();
                message.addRegId(task.getOwnerGoogleId());
                message.createData(Config.NOTIF_TITLE, currentUser.getName() + (task.getState().equals(Config.STATE_TODO) ? Config.NOTIF_CONTENT_TASK_DOING : Config.NOTIF_CONTENT_TASK_DONE));
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGoogleRequest(message);
                return res;
            }
        }
        NotificationTask nt = new NotificationTask();
        nt.execute();
    }

    /**
     * Change the state of the selected task to the previous state
     */
    private void goPreviousState(final Task task) {
        final String previousState = task.getPreviousState();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeTaskStatus(task, previousState);
                onRefresh();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage(previousState.equals(Config.STATE_DELETED) ? "Delete task?" : "Put back in " + previousState + "?");
        alertDialog.show();


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
                taskToSort.setOwnerGoogleId(jo.getString(Config.KEY_USER_GOOGLE_ID));

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


    /**
     * Fetch the data from the server
     */
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
                String s = rh.sendGetRequestParam(Config.URL_GET_ALL_TASKS, currentUser.getGroupKey());
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    /**
     * Generate a custom snackbar to show more details about the selected task
     * @param task
     * @param v
     */
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

        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        snackbarText.append("Created by ");
        int boldStart = snackbarText.length();
        snackbarText.append(task.getOwner_name());
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
        textView.setMaxLines(10);  // show multiple line
        snackbar.show();
    }

    public void changeTaskStatus(final Task task, final String status) {
        class ChangeTaskStatus extends AsyncTask<Void, Void, String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_TASK_ID, Integer.toString(task.getId()));
                params.put(Config.KEY_TASK_STATE, status);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_UPDATE_TASK, params);

                return res;
            }
        }

        ChangeTaskStatus cts = new ChangeTaskStatus();
        cts.execute();
    }


    public void changeTaskStatus(final Task task, final String status, final String worker) {
        class ChangeTaskStatus extends AsyncTask<Void, Void, String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_TASK_ID, Integer.toString(task.getId()));
                params.put(Config.KEY_TASK_STATE, status);
                params.put(Config.KEY_TASK_WORKER, worker);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_UPDATE_TASK, params);

                return res;
            }
        }

        ChangeTaskStatus cts = new ChangeTaskStatus();
        cts.execute();
    }


    @Override
    public void onRefresh() {
        fetchTask();
    }



}
