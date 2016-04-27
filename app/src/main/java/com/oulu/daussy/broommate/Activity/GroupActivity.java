package com.oulu.daussy.broommate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.webkit.WebView;
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
    private EditText textKeyJoinGroup;
    private String JSON_STRING;
    private WebView webView;
    private Button qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        fetchUser();
        buttonNewGroup  = (Button)   findViewById(R.id.buttonCreateGroup);
        buttonJoinGroup = (Button)   findViewById(R.id.buttonJoinGroup);
        textGroupName   = (EditText) findViewById(R.id.editGroupName);
        textKeyJoinGroup = (EditText) findViewById(R.id.editKeyJoin);
        qr = (Button) findViewById(R.id.qr);


        String htmlText = "<html><body style=\"text-align:justify\"> %s </body></html>";
        String myData = "You can either name and create a new group in which you will automatically belong, or you can enter a key somebody else shared with you to join his group. If you create your own group you will be able to share your key with your friends in the next screen.";
        webView = (WebView) findViewById(R.id.webview);
        webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");
        webView.setBackgroundColor(Color.TRANSPARENT);


        //populateView(View.INVISIBLE);

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No QR reader app installed", Toast.LENGTH_LONG).show();
                }
            }
        });


        buttonNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textGroupName.getText().length() > 0){
                    String textKeyNewGroup = generateKey();
                    createNewGroup(String.valueOf(textGroupName.getText()), textKeyNewGroup);
                    joinGroup(textKeyNewGroup);
                    nextActivity();
                }
            }
        });


        buttonJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textKeyJoinGroup.getText().length() > 0){
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
            }
        }

        NewGroup ng = new NewGroup();
        ng.execute();
    }

/*
    public void checkExistGroup(final String key) {
        class CheckGroup extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... p) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_USER_GROUP_ID, key);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_CHECK_GROUP, params);

                return res;
            }
        }

        CheckGroup cg = new CheckGroup();
        cg.execute();
    }
    */

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

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GroupActivity.this,"Fetching data","Check group...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("String post", " = " + s.toString());
                JSON_STRING = s;
                if (!JSON_STRING.equals(Config.EMPTY_JSON)){
                    populateGroup();
                } else {
                    populateView(View.VISIBLE);
                }
                loading.dismiss();
            }


            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                Log.d("REQUEST", Config.URL_GET_UNIQUE_USER + currentUser.getFacebook_id().toString());
                String s = rh.sendGetRequestParam(Config.URL_GET_UNIQUE_USER, currentUser.getFacebook_id().toString());
                Log.d("String in", " = " + s.toString());
                return s;
            }

        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    public void populateView(int visibility) {
        buttonNewGroup.setVisibility(visibility);
        buttonJoinGroup.setVisibility(visibility);
        textGroupName.setVisibility(visibility);
        textKeyJoinGroup.setVisibility(visibility);
    }

    public void populateGroup() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            String groupkey = result.getJSONObject(0).getString(Config.KEY_USER_GROUP_ID);
            currentUser.setGroupKey(groupkey);

            if (groupkey.length() > 0){
                //nextActivity();
            } else {
                populateView(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_SHORT).show();
                textKeyJoinGroup.setText(contents);
                // TODO: Do something here with it
            }// if result_ok
    }// onactivityresult

}
