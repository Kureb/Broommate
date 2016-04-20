package com.oulu.daussy.broommate.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.HashMap;
import java.util.UUID;

public class GroupActivity extends AppCompatActivity {

    private CurrentUser currentUser = CurrentUser.getInstance();
    private Button buttonNewGroup;
    private Button buttonJoinGroup;
    private EditText textGroupName;
    private TextView textKeyNewGroup;
    private EditText textKeyJoinGroup;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        fetchUser();

        buttonNewGroup  = (Button)   findViewById(R.id.buttonCreateGroup);
        buttonJoinGroup = (Button)   findViewById(R.id.buttonJoinGroup);
        textGroupName   = (EditText) findViewById(R.id.editGroupName);
        textKeyJoinGroup = (EditText) findViewById(R.id.editKeyJoin);
        textKeyNewGroup  = (TextView) findViewById(R.id.editKeyGenerated);


        buttonNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textGroupName.getText().length() > 0){
                    buttonJoinGroup.setVisibility(View.INVISIBLE);
                    textKeyJoinGroup.setVisibility(View.INVISIBLE);
                    textKeyNewGroup.setText(generateKey());
                    createNewGroup(String.valueOf(textGroupName.getText()), String.valueOf(textKeyNewGroup.getText()));
                    joinGroup(String.valueOf(textKeyNewGroup.getText()));
                    nextActivity();
                }
            }
        });


        buttonJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textKeyJoinGroup.getText().length() > 0){
                    buttonNewGroup.setVisibility(View.INVISIBLE);
                    textKeyNewGroup.setVisibility(View.INVISIBLE);
                    joinGroup(String.valueOf(textKeyJoinGroup.getText()));
                    nextActivity();
                }
            }
        });


    }

    public String generateKey() {
        return UUID.randomUUID().toString();
    }


    public void nextActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(myIntent);
    }



    public void createNewGroup(final String name, final String key) {
        currentUser.setGroupKey(key);

        class NewGroup extends AsyncTask<Void, Void, String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(GroupActivity.this, "Creating group in progress..", Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_USER_GROUP_KEY, key);
                params.put(Config.KEY_USER_GROUP_NAME, name);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_GROUP, params);

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //Toast.makeText(GroupActivity.this, s, Toast.LENGTH_LONG).show();
                //Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }

        NewGroup ng = new NewGroup();
        ng.execute();
    }

    public void joinGroup(final String key) {
        //TODO check if group really exists
        currentUser.setGroupKey(key);

        class JoinGroup extends AsyncTask<Void, Void, String>{

            @Override
            protected  String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_USER_GROUP_ID, key);
                params.put(Config.KEY_USER_FACEBOOK_ID, currentUser.getFacebook_id());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_JOIN_GROUP, params);

                return res;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //Toast.makeText(GroupActivity.this, s, Toast.LENGTH_LONG).show();
                //Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }

        JoinGroup jg = new JoinGroup();
        jg.execute();
    }



    private void fetchUser() {
        class GetJSON extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                if (JSON_STRING != "{\"result\":[]}\n"){
                    populateGroup();
                }
            }


            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Config.URL_GET_UNIQUE_USER, currentUser.getFacebook_id());
                Log.d("String", " = " + s.toString());
                return s;
            }


        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    public void populateGroup() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            String groupkey = result.getJSONObject(0).getString(Config.KEY_USER_GROUP_ID);
            currentUser.setGroupKey(groupkey);

            if (groupkey.length() > 0 && !groupkey.equals("null")){
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
