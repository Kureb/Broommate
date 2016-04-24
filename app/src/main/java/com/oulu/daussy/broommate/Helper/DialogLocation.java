package com.oulu.daussy.broommate.Helper;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.maps.model.Marker;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.Model.GoogleCloudMessage;
import com.oulu.daussy.broommate.R;

import java.util.HashMap;

/**
 * Created by daussy on 24/04/16.
 */
public class DialogLocation extends DialogFragment {

    String user;
    CurrentUser currentUser = CurrentUser.getInstance();

    public static DialogLocation newInstance(String userName, String userId) {
        DialogLocation f = new DialogLocation();
        Bundle args = new Bundle();
        args.putString("name", userName);
        args.putString("gid", userId);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String user = getArguments().getString("name");
        String lastLetter = user.substring(user.length() - 1);
        String s = lastLetter.equals("' ") ? "'" : "'s ";

        final String key = getArguments().getString("gid");
        builder.setMessage("Ask for " + user + s + " location?")
                .setPositiveButton("Ask", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendPositionRequest(key);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    public void sendPositionRequest(final String key) {
        class AskUsers extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... p) {
                HashMap<String, String> params = new HashMap<>();
                GoogleCloudMessage message = new GoogleCloudMessage();
                message.addRegId(key);
                message.createData(Config.NOTIF_TITLE, currentUser.getName() + Config.NOTIF_CONTENT);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendGoogleRequest(message);
                return res;
            }
        }
        AskUsers au = new AskUsers();
        au.execute();
    }
}