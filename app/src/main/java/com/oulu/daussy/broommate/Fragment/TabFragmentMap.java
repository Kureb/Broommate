package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TabFragmentMap extends Fragment {

    private SupportMapFragment fragment;
    private GoogleMap map;
    private User[] users;
    private String JSON_STRING;
    private int numberOfLocations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_map, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        if (map == null) {
            map = fragment.getMap();
            map.addMarker(new MarkerOptions()
                            .title("Yanchuan")
                            .snippet("Last update : 17:35")
                            .position(new LatLng(65.0523786,25.4748522)))
                    .showInfoWindow(); //Oulu University

            map.addMarker(new MarkerOptions()
                        .title("Alex")
                        .position(new LatLng(65.0723439, 25.462122))
                        .snippet("Last update : 2 hours ago")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    .showInfoWindow();//Ideapark

            LatLng mapCenter = new LatLng((65.0523786+65.0723439)/2, (25.4748522+25.462122)/2);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));
        }
        */
        fetchPositions();
        //populateUsers();
    }


    public void populateUsers() {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);
            users = new User[result.length()];
            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                User user = new User();

                user.setId(Integer.parseInt(jo.getString(Config.KEY_USER_ID)));
                user.setFacebook_id(jo.getString(Config.KEY_USER_FACEBOOK_ID));
                user.setName(jo.getString(Config.KEY_USER_NAME));
                //user.setGroup_id(Integer.parseInt(jo.getString(Config.KEY_USER_GROUP_ID)));
                user.setPosX(jo.getString(Config.KEY_USER_POSX));
                user.setPosY(jo.getString(Config.KEY_USER_POSY));

                users[i] = user;

            }

            populateMap();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateMap() {
        map = fragment.getMap();
        User user = null;
        numberOfLocations = 0;
        for (int i = 0; i < users.length; i++) {
            user = users[i];
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null"){
                numberOfLocations++;
                map.addMarker(new MarkerOptions()
                                .title(user.getName())
                                .snippet(("Last updated" + user.getId()))
                                .position(new LatLng(Double.parseDouble(user.getPosX()), Double.parseDouble(user.getPosY()))))
                        .showInfoWindow();
            }
        }

        LatLng mapCenter = new LatLng(getCenterPositionX(), getCenterPositionY());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));
    }

    public double getCenterPositionX() {
        double x = 0;
        User user;
        for (int i = 0; i < users.length; i++){
            user = users[i];
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null"){
                x += Double.parseDouble(user.getPosX());
            }
        }
        x /= numberOfLocations;
        return x;
    }

    public double getCenterPositionY() {
        double y = 0;
        User user;
        for (int i = 0; i < users.length; i++){
            user = users[i];
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null"){
                y += Double.parseDouble(user.getPosY());
            }
        }
        y /= numberOfLocations;
        return y;
    }

    private void fetchPositions() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                populateUsers();
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


}
