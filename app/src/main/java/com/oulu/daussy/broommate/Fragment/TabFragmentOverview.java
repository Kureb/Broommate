package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Helper.ListAdapter;
import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TabFragmentOverview extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String JSON_STRING;
    private ListView listView;
    private ArrayList<User> listUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListAdapter listAdapter;
    private FloatingActionButton fab;

    public TabFragmentOverview() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_overview, container, false);

        listView = (ListView) view.findViewById(R.id.listview_overview);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_overview);
        fab.attachToListView(listView);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_overview);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchUsers();
            }
        });

        return view;

    }


    private void fetchUsers() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                swipeRefreshLayout.setRefreshing(false);
                JSON_STRING = s;
                populateListView();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_ALL_USERS);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    private void populateListView() {
        JSONObject jsonObject = null;
        listUser = new ArrayList<User>();

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                User user = new User();
                JSONObject jo = result.getJSONObject(i);
                user.setId(Integer.parseInt(jo.getString(Config.KEY_USER_ID)));
                user.setFacebook_id(jo.getString(Config.KEY_USER_FACEBOOK_ID));
                user.setName(jo.getString(Config.KEY_USER_NAME));
                user.setPosX(jo.getString(Config.KEY_USER_POSX));
                user.setPosY(jo.getString(Config.KEY_USER_POSY));
                listUser.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        listAdapter = new ListAdapter(getContext(), listUser);
        listView.setAdapter(listAdapter);

    }


    @Override
    public void onRefresh() {
        fetchUsers();
    }
}
