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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
                Snackbar.make(v, "We'll add the task", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                addTask();
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


    private void addTask() {
        final String name = titleTask.getText().toString().trim();
        final String priority = spinner.getSelectedItem().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String currentDateandTime = sdf.format(new Date());

        class AddTask extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddTaskActivity.this,"Adding task in progress","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(AddTaskActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();

                params.put(Config.KEY_TASK_NAME, name);
                params.put(Config.KEY_TASK_PRIORITY, priority);
                params.put(Config.KEY_TASK_STATE, "TODO");

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD_TASK, params);

                return res;
            }
        }

        AddTask at = new AddTask();
        at.execute();

    }

}
