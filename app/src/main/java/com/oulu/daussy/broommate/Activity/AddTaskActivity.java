package com.oulu.daussy.broommate.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button cancelButton;
    private Button addButton;
    private EditText titleTask;
    private CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        cancelButton = (Button) findViewById(R.id.buttonCancel);
        addButton = (Button) findViewById(R.id.buttonAdd);
        titleTask = (EditText) findViewById(R.id.titleTask);

        //Cancel = back
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });


        // Spinner
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("LOW");
        spinnerArray.add("MEDIUM");
        spinnerArray.add("HIGH");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinnerPriority);
        spinner.setAdapter(adapter);

    }


    private void addTask(final View v) {

        final String name = titleTask.getText().toString().trim();
        final String priority = spinner.getSelectedItem().toString();
        final String owner = currentUser.getFacebook_id();


        class AddTask extends AsyncTask<Void, Void, String>{

            //ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Snackbar.make(v, s, Snackbar.LENGTH_LONG).setAction("Action",null).show();
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

}
