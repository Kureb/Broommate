package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Helper.ListAdapter;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.Model.Home;
import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class TabFragmentOverview extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String JSON_STRING;
    private ListView listView;
    private ArrayList<User> listUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListAdapter listAdapter;
    private FloatingActionButton fab;
    private CurrentUser currentUser = CurrentUser.getInstance();
    private Home home = Home.getInstance();
    private ImageView qrcode;
    private TextView groupName;

    public TabFragmentOverview() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_overview, container, false);

        listView = (ListView) view.findViewById(R.id.listview_overview);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_overview);
        fab.attachToListView(listView);
        groupName = (TextView) view.findViewById(R.id.groupName);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_overview);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchUsers();
            }
        });


        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.share_group);

                Button cancelButton = (Button) dialog.findViewById(R.id.buttonCancelShare);
                Button shareButton = (Button) dialog.findViewById(R.id.buttonShareShare);
                TextView keyText = (TextView) dialog.findViewById(R.id.keyShare);
                qrcode = (ImageView) dialog.findViewById(R.id.qrCode);

                keyText.setText("Flash the QRCode or copy key in clipboard.");

                new DownloadImageTask((ImageView) dialog.findViewById(R.id.qrCode))
                        .execute("http://api.qrserver.com/v1/create-qr-code/?data="+ currentUser.getGroupKey() +"&size=500x500");


                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", currentUser.getGroupKey());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getContext(), "Key copied in clipboard, ready to share", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                });

                dialog.setTitle("Share group " + home.getName());
                dialog.show();


                /*
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Want a new user?");
                builder.setMessage("The key to share is:\n" + currentUser.getGroupKey())
                        .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", currentUser.getGroupKey());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getContext(), "Copied in clipboard, ready to share", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
                */
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
                String s = rh.sendGetRequestParam(Config.URL_GET_USER, currentUser.getGroupKey());
                //Toast.makeText(getContext(), s, Toast.LENGTH_LONG);
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
            JSONObject jo = null;
            for (int i = 0; i < result.length(); i++) {
                User user = new User();
                jo = result.getJSONObject(i);
                user.setId(Integer.parseInt(jo.getString(Config.KEY_USER_ID)));
                user.setFacebook_id(jo.getString(Config.KEY_USER_FACEBOOK_ID));
                user.setName(jo.getString(Config.KEY_USER_NAME));
                user.setPosX(jo.getString(Config.KEY_USER_POSX));
                user.setPosY(jo.getString(Config.KEY_USER_POSY));
                user.setLastUpdatePos(jo.getString(Config.KEY_USER_LAST_UPDATE));
                listUser.add(user);
            }

            home.setPosX(jo.getString("posX_home"));
            home.setPosY(jo.getString("posY_home"));
            home.setName(jo.getString("group_name"));

            groupName.setText(home.getName());
            
        } catch (JSONException e) {
            e.printStackTrace();
        }


        listAdapter = new ListAdapter(getContext(), listUser);
        listView.setAdapter(listAdapter);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



    @Override
    public void onRefresh() {
        fetchUsers();
    }
}
