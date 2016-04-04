package com.oulu.daussy.broommate.Helper;

/**
 * Created by daussy on 25/03/16.
 * Adaptation of http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 * to fit the needs of our listview expandable
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.oulu.daussy.broommate.Model.Task;
import com.oulu.daussy.broommate.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Task>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Task>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Task taskToShow = (Task) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtTitleTask = (TextView) convertView
                .findViewById(R.id.taskName);

        ProfilePictureView ownerPicture = (ProfilePictureView) convertView
                .findViewById(R.id.owner);

        ProfilePictureView workerPicture = (ProfilePictureView) convertView
                .findViewById(R.id.worker);

        TextView priorityColorBar = (TextView) convertView
                .findViewById(R.id.colorBar);



        txtTitleTask.setText(taskToShow.getTitle());
        ownerPicture.setProfileId(taskToShow.getOwner());

        if (!taskToShow.getWorker().isEmpty())
            workerPicture.setProfileId(taskToShow.getWorker());

        switch (taskToShow.getState()) {
            case "TODO":
                workerPicture.setVisibility(View.INVISIBLE);
                ownerPicture.setVisibility(View.VISIBLE);
                break;
            case "DOING":
            case "DONE":
                workerPicture.setVisibility(View.VISIBLE);
                ownerPicture.setVisibility(View.VISIBLE);
                break;
        }

        switch (taskToShow.getPriority()) {
            case "LOW":
                priorityColorBar.setBackgroundColor(Color.GREEN);
                break;
            case "MEDIUM":
                priorityColorBar.setBackgroundColor(Color.YELLOW);
                break;
            case "HIGH":
                priorityColorBar.setBackgroundColor(Color.RED);
                break;
        }


        //profilePicture.setVisibility(View.GONE); //View.INVISIBLE

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView taskCategorie = (TextView) convertView
                .findViewById(R.id.taskCategorie);
        taskCategorie.setTypeface(null, Typeface.BOLD);
        taskCategorie.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
