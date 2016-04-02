package com.oulu.daussy.broommate.Fragment;

/**
 * Created by daussy on 14/03/16.
 */
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
import com.oulu.daussy.broommate.R;

public class TabFragmentMap extends Fragment {

    private SupportMapFragment fragment;
    private GoogleMap map;

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
    }


}
