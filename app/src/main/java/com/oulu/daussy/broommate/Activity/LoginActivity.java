package com.oulu.daussy.broommate.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.oulu.daussy.broommate.R;

import org.json.JSONObject;


public class LoginActivity extends Activity {

    private LoginButton loginButton;
    private Button cancelButton;
    private AccessToken accessToken;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        //cancelButton = (Button) findViewById(R.id.cancel_button);


        //ask for permission to read our profil
        loginButton.setReadPermissions("public_profile");

        //if already logged in, start next activity
        if (AccessToken.getCurrentAccessToken()!=null) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
        }



        //button to log in facebook
        //when logged in, send to next activity
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                displayToast("Login successful");
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);

                accessToken = AccessToken.getCurrentAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                            }
                        }

                );
                Bundle loginBundle = new Bundle();
                loginBundle.putString("userInfo", "id, first_name, last_name");
                request.setParameters(loginBundle);
                request.executeAsync();

                myIntent.putExtras(loginBundle);

                startActivity(myIntent);

                //finish();
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


}
