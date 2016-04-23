package com.oulu.daussy.broommate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class LoginActivity extends Activity {

    private LoginButton loginButton;
    private Button cancelButton;
    private AccessToken accessToken;
    private CallbackManager callbackManager;
    public CurrentUser currentUser = CurrentUser.getInstance();
    private String JSON_STRING;
    private GoogleCloudMessaging gcm;
    private String gcmid;
    private String PROJECT_NUMBER = "731765884851";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        //ask for permission to read our profil
        loginButton.setReadPermissions("public_profile");


        //if already logged in, start next activity
        if (AccessToken.getCurrentAccessToken()!=null) {
            populateUserWithGroup();
        }



        //button to log in facebook
        //when logged in, send to next activity
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                displayToast("Login successful");
                Intent myIntent = new Intent(getApplicationContext(), GroupActivity.class);
                accessToken = AccessToken.getCurrentAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                populateUser();
                                addUser(currentUser);
                            }
                        }

                );
                Bundle loginBundle = new Bundle();
                loginBundle.putString("userInfo", "id, first_name, last_name");
                request.setParameters(loginBundle);
                request.executeAsync();

                myIntent.putExtras(loginBundle);

                startActivity(myIntent);
            }

            @Override
            public void onCancel() {
                displayToast("Login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                displayToast("Error");
                Log.e("Facebook Exception : ", error.toString());
            }
        });


    }

    private void populateUser() {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            currentUser.setName(object.getString("name"));
                            currentUser.setFacebook_id(object.getString("id"));
                            Log.d("USER ", currentUser.getFacebook_id());
                        } catch (JSONException e) { }
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name" );
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void populateUserWithGroupKey() {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            currentUser.setName(object.getString("name"));
                            currentUser.setFacebook_id(object.getString("id"));
                            Log.d("USER ", currentUser.getFacebook_id());
                            fetchUser();
                        } catch (JSONException e) { }
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name" );
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void populateUserWithGroup() {
        populateUserWithGroupKey();

        /*
        Intent myIntent = null;
        if (!currentUser.getGroupKey().isEmpty()){
            myIntent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            myIntent = new Intent(getApplicationContext(), GroupActivity.class);
        }
        startActivity(myIntent);
        */
    }

    private void fetchUser() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Fetching data","Check group...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("String post", " = " + s.toString());
                JSON_STRING = s;
                if (!JSON_STRING.equals(Config.EMPTY_JSON)){
                    populateGroup();
                }
                getRegId();
                loading.dismiss();
            }


            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
//                Log.d("REQUEST", Config.URL_GET_UNIQUE_USER + currentUser.getFacebook_id().toString());
                String s = rh.sendGetRequestParam(Config.URL_GET_UNIQUE_USER, currentUser.getFacebook_id());
                Log.d("String in", " = " + s.toString());
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

            if (groupkey.length() > 0){
                nextActivity(MainActivity.class);
            } else {
                nextActivity(GroupActivity.class);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addUser(User currentUser) {
        final String name = currentUser.getName();
        final String facebook_id = currentUser.getFacebook_id();

        class AddUser extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Adding user in progress","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                //Snackbar.make(findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_USER_NAME, name);
                params.put(Config.KEY_USER_FACEBOOK_ID, facebook_id);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_USER, params);

                return res;
            }
        }

        AddUser au = new AddUser();
        au.execute();
    }


    public void getRegId(){
        class GoogleCloudMessagingID extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcmid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + gcmid;
                    Log.d("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return gcmid;
            }

            @Override
            protected void onPostExecute(String msg) {
                currentUser.setGCMid(msg);
                currentUser.update(LoginActivity.this, currentUser);
            }
        }
        GoogleCloudMessagingID googleCloudMessagingID = new GoogleCloudMessagingID();
        googleCloudMessagingID.execute();
    }




    //    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Display a toast
     * @param text string to display on the screen
     */
    private void displayToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    public void nextActivity(Class cls) {
        Intent myIntent = new Intent(getApplicationContext(), cls);
        startActivity(myIntent);
    }



}
