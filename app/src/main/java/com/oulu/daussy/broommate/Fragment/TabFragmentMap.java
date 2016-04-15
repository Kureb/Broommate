package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class TabFragmentMap extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SupportMapFragment fragment;
    private GoogleMap map;
    private User[] users;
    private String JSON_STRING;
    private int numberOfLocations;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton fabHome;
    private FloatingActionButton fabShare;
    private FloatingActionButton fabAsk;
    private FloatingActionButton fabRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private LocationManager locationManager;
    private Location location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_map, container, false);

        floatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        fabHome = (FloatingActionButton) view.findViewById(R.id.fabSetHome);
        fabShare = (FloatingActionButton) view.findViewById(R.id.fabSharePosition);
        fabAsk = (FloatingActionButton) view.findViewById(R.id.fabAskLocation);
        fabRefresh = (FloatingActionButton) view.findViewById(R.id.fabRefresh);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_overview);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchPositions();
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Share", Toast.LENGTH_LONG).show();
                updateLocation();
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
            }
        });

        fabAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Ask", Toast.LENGTH_SHORT).show();
            }
        });

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPositions();
            }
        });


        return view;
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
        fetchPositions();
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
                user.setLastUpdatePos(jo.getString(Config.KEY_USER_LAST_UPDATE));

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
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null") {
                numberOfLocations++;
                map.addMarker(new MarkerOptions()
                        .title(user.getName())
                        .snippet(("Last update " + user.timeAgo()))
                        .position(new LatLng(Double.parseDouble(user.getPosX()), Double.parseDouble(user.getPosY()))))
                        .showInfoWindow();
            }
        }

        LatLng mapCenter = new LatLng(getCenterPositionX(), getCenterPositionY());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));
        //location = map.getMyLocation();
        //double latitude = location.getLatitude();
    }

    public double getCenterPositionX() {
        double x = 0;
        User user;
        for (int i = 0; i < users.length; i++) {
            user = users[i];
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null") {
                x += Double.parseDouble(user.getPosX());
            }
        }
        x /= numberOfLocations;
        return x;
    }

    public double getCenterPositionY() {
        double y = 0;
        User user;
        for (int i = 0; i < users.length; i++) {
            user = users[i];
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null") {
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
                swipeRefreshLayout.setRefreshing(true);
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                populateUsers();
                swipeRefreshLayout.setRefreshing(false);
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


    private void updateLocation() {
        final CurrentUser currentUser = CurrentUser.getInstance();

        LocationManager locationManager = (LocationManager)
                getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        currentUser.setPosX(Double.toString(latitude));
        currentUser.setPosY(Double.toString(longitude));

        class UpdateLocation extends AsyncTask<Void, Void, String>{


            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_USER_FACEBOOK_ID, currentUser.getFacebook_id());
                params.put(Config.KEY_USER_POSX, currentUser.getPosX());
                params.put(Config.KEY_USER_POSY, currentUser.getPosY());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_UPDATE_LOCATION, params);

                return res;
            }
        }
        UpdateLocation ul = new UpdateLocation();
        ul.execute();
    }


    @Override
    public void onRefresh() {
        fetchPositions();
    }
}
