package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oulu.daussy.broommate.Configuration.Config;
import com.oulu.daussy.broommate.Configuration.RequestHandler;
import com.oulu.daussy.broommate.Helper.DialogLocation;
import com.oulu.daussy.broommate.Model.CurrentUser;
import com.oulu.daussy.broommate.Model.GoogleCloudMessage;
import com.oulu.daussy.broommate.Model.Home;
import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class TabFragmentMap extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LocationListener {

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
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    private String lat;
    private String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final Home home = Home.getInstance();
    private Circle mCircle;//home
    private Marker mMarker;//home

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

        swipeRefreshLayout.setEnabled(false);
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
                floatingActionsMenu.collapse();
                updateLocation();
            }
        });

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                updateHome();
            }
        });

        fabAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                askUsersPosition();
            }
        });

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.collapse();
                fetchPositions();
            }
        });







        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        return view;
    }

    private void askUsersPosition() {
        class AskUsers extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... p) {
                HashMap<String, String> params = new HashMap<>();

                GoogleCloudMessage message = new GoogleCloudMessage();

                for (User u: users) {
                    if (!u.getName().equals(currentUser.getName()))
                       if (!u.getGCMid().isEmpty())
                           message.addRegId(u.getGCMid());
                }

                message.createData(Config.NOTIF_TITLE, currentUser.getName() + Config.NOTIF_CONTENT);



                RequestHandler rh = new RequestHandler();
                String res = rh.sendGoogleRequest(message);


                return res;
            }
        }

        AskUsers au = new AskUsers();
        au.execute();
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
            JSONObject jo = null;
            for (int i = 0; i < result.length(); i++) {
                jo = result.getJSONObject(i);
                User user = new User();

                user.setId(Integer.parseInt(jo.getString(Config.KEY_USER_ID)));
                user.setFacebook_id(jo.getString(Config.KEY_USER_FACEBOOK_ID));
                user.setName(jo.getString(Config.KEY_USER_NAME));
                //user.setGroup_id(Integer.parseInt(jo.getString(Config.KEY_USER_GROUP_ID)));
                user.setPosX(jo.getString(Config.KEY_USER_POSX));
                user.setPosY(jo.getString(Config.KEY_USER_POSY));
                user.setLastUpdatePos(jo.getString(Config.KEY_USER_LAST_UPDATE));
                user.setGCMid(jo.getString(Config.KEY_USER_GOOGLE_ID));


                users[i] = user;

            }

            home.setPosX(jo.getString("posX_home"));
            home.setPosY(jo.getString("posY_home"));

            populateMap();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateMap() {
        map = fragment.getMap();
        User user = null;
        numberOfLocations = 0;
        map.clear();
        ArrayList<MarkerOptions> markers = new ArrayList<>();
        for (int i = 0; i < users.length; i++) {
            user = users[i];
            if (user.getPosX().length() > 0 && user.getPosY().length() > 0 && user.getPosX() != "null" && user.getPosY() != "null") {
                numberOfLocations++;
                MarkerOptions marker = new MarkerOptions()
                        .title(user.getName())
                        .snippet(("Last update " + user.timeAgo()))
                        .position(new LatLng(Double.parseDouble(user.getPosX()), Double.parseDouble(user.getPosY())));

                if (user.getName().equals(currentUser.getName()))
                    marker.alpha(0.7f);

                markers.add(marker);

                map.addMarker(marker);

            }
        }


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!marker.getTitle().equals(Config.HOME)){
                    String name = marker.getTitle();
                    User userToAsk = null;
                    for (User u:users
                            ) {
                        if (u.getName().equals(marker.getTitle()))
                            userToAsk = u;
                    }
                    String key = userToAsk.getGCMid();
                    if (!key.isEmpty() && !marker.getTitle().equals(currentUser.getName())){
                        DialogLocation dl = DialogLocation.newInstance(marker.getTitle().split(" ")[0], key);
                        dl.show(getFragmentManager(), "location");
                    }
                }
            }

        });

        LatLng latLng = new LatLng(Double.parseDouble(home.getPosX()), Double.parseDouble(home.getPosY()));
        drawMarkerWithCircle(latLng);

        /**
         * To see all the markers on the map we need to compute the center
         * position and the level of zoom
         * TODO fix bug when just one user who has shared its location
         */
        LatLng mapCenter = new LatLng(getCenterPositionX(), getCenterPositionY());
        map.moveCamera(CameraUpdateFactory.newLatLng(mapCenter));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.animateCamera(cu); //or moveCamera without animation




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
                String s = rh.sendGetRequestParam(Config.URL_GET_USER, currentUser.getGroupKey());
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }


    private void updateHome() {
        currentUser.setPosX(latitude);
        currentUser.setPosY(longitude);

        class UpdateHomeLocation extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Config.KEY_USER_POSX, currentUser.getPosX());
                params.put(Config.KEY_USER_POSY, currentUser.getPosY());
                params.put(Config.KEY_USER_GROUP_KEY, currentUser.getGroupKey());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_UPDATE_HOME, params);

                home.setPosX(currentUser.getPosX());
                home.setPosY(currentUser.getPosY());

                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                onRefresh();
            }
        }
        UpdateHomeLocation uhl = new UpdateHomeLocation();
        uhl.execute();
    }


    private void updateLocation() {
        currentUser.setPosX(latitude);
        currentUser.setPosY(longitude);

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

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                onRefresh();
            }
        }
        UpdateLocation ul = new UpdateLocation();
        ul.execute();
    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = Config.SIZE_CIRCLE;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = map.addCircle(circleOptions);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.color_icons_green_home);
        //Bitmap b= BitmapDescriptorFactory.fromResource(R.drawable.color_icons_green_home);
        Bitmap bhalfsize=Bitmap.createScaledBitmap(icon, icon.getWidth()/12,icon.getHeight()/12, false);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(Config.HOME)
                //.rotation((float) 45.0)
                .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize));
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMarker = map.addMarker(markerOptions);
    }


    @Override
    public void onRefresh() {
        fetchPositions();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = Double.toString(location.getLatitude());
        longitude = Double.toString(location.getLongitude());
        Log.v("zbra", "IN ON LOCATION CHANGE, lat=" + latitude + ", lon=" + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
