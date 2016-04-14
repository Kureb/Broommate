package com.oulu.daussy.broommate.Helper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oulu.daussy.broommate.Model.User;
import com.oulu.daussy.broommate.R;

import java.util.ArrayList;

/**
 * Created by daussy on 14/04/16.
 */
public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<User> listUser;

    public ListAdapter(Context context, ArrayList<User> listUser){
        this.context = context;
        this.listUser = listUser;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        //return this.listDataChild.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("LISTADAPTER", "inside");
        final User user = (User) listUser.get(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_overview, null);
        }

        ProfilePictureView userPicture = (ProfilePictureView) convertView
                .findViewById(R.id.userPicture);

        TextView userName = (TextView) convertView
                .findViewById(R.id.nameUser);

        userName.setText(user.getName());
        userPicture.setProfileId(user.getFacebook_id());

        return convertView;
    }


}
