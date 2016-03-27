package com.oulu.daussy.broommate;

/**
 * Created by daussy on 14/03/16.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragmentOverview tab1 = new TabFragmentOverview();
                return tab1;
            case 1:
                TabFragmentMap tab2 = new TabFragmentMap();
                return tab2;
            case 2:
                TabFragmentTasks tab3 = new TabFragmentTasks();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
