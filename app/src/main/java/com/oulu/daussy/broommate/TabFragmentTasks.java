package com.oulu.daussy.broommate;

/**
 * Created by daussy on 14/03/16.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabFragmentTasks extends Fragment {

    private FloatingActionButton addButton;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView listView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private String JSON_STRING;
    private TextView taskName;
    private TextView taskPriority;

    public TabFragmentTasks() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_tasks, container, false);
        addButton = (FloatingActionButton) view.findViewById(R.id.fab);
        taskName = (TextView) view.findViewById(R.id.taskName);
        taskPriority = (TextView) view.findViewById(R.id.taskPriority);

        addButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), AddTaskActivity.class);
                startActivity(myIntent);
            }
        });

        // get the listview
        listView = (ExpandableListView) view.findViewById(R.id.expandableListView);

        /*
        // preparing list data
        prepareListDataBak();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        listView.setAdapter(listAdapter);
        listView.expandGroup(0);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
        */
        getJSON();
        //listView.expandGroup(0);

        return view;
    }

    private void prepareListDataBak() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("TODO");
        listDataHeader.add("DOING");
        listDataHeader.add("DONE");

        // Adding child data
        List<String> todo = new ArrayList<String>();
        todo.add("The Shawshank Redemption");
        todo.add("The Godfather");
        todo.add("The Godfather: Part II");
        todo.add("Pulp Fiction");
        todo.add("The Good, the Bad and the Ugly");
        todo.add("The Dark Knight");
        todo.add("12 Angry Men");

        List<String> doing = new ArrayList<String>();
        doing.add("The Conjuring");
        doing.add("Despicable Me 2");
        doing.add("Turbo");
        doing.add("Grown Ups 2");
        doing.add("Red 2");
        doing.add("The Wolverine");

        List<String> done = new ArrayList<String>();
        done.add("2 Guns");
        done.add("The Smurfs 2");
        done.add("The Spectacular Now");
        done.add("The Canyons");
        done.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), todo);
        listDataChild.put(listDataHeader.get(1), doing);
        listDataChild.put(listDataHeader.get(2), done);
    }


    private void prepareListData() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("TODO");
        listDataHeader.add("DOING");
        listDataHeader.add("DONE");

        List<String> todo = new ArrayList<String>();
        List<String> doing = new ArrayList<String>();
        List<String> done = new ArrayList<String>();

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Config.KEY_TASK_ID);
                String name = jo.getString(Config.KEY_TASK_NAME);
                String priority = jo.getString(Config.KEY_TASK_PRIORITY);
                String state = jo.getString(Config.KEY_TASK_STATE);

                HashMap<String,String> task = new HashMap<>();
                task.put(Config.KEY_TASK_ID,id);
                task.put(Config.KEY_TASK_NAME,name);

                switch (state) {
                    case "TODO":
                        todo.add(name);
                        break;
                    case "DOING":
                        doing.add(name);
                        break;
                    case "DONE":
                        done.add(name);
                        break;

                }
                //list.add(task);
            }

            listDataChild.put(listDataHeader.get(0), todo);
            listDataChild.put(listDataHeader.get(1), doing);
            listDataChild.put(listDataHeader.get(2), done);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
/*
        ListAdapter adapter = new SimpleAdapter(
                TabFragmentTasks.this, list, R.layout.list_item,
                new String[]{Config.TAG_TASK_ID, Config.TAG_TASK_NAME},
                new int[]{R.id.lblListItem, R.id.lblListItem2});
                */

        listView.setAdapter(listAdapter);


    }


    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(),"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
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

}
