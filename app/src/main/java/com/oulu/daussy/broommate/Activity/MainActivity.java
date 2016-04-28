package com.oulu.daussy.broommate.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.oulu.daussy.broommate.GoogleCloudMessaging.GcmMessageHandler;
import com.oulu.daussy.broommate.Helper.CustomViewPager;
import com.oulu.daussy.broommate.Helper.PagerAdapter;
import com.oulu.daussy.broommate.R;


public class MainActivity extends AppCompatActivity {

    private CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Members"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Tasks"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setPagingEnabled(false); //Disable swiping between tabs
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

/*        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            Log.d("AccessToken", "" + token.getToken());
        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String message = intent.getStringExtra(GcmMessageHandler.EXTRA_MESSAGE);
        viewPager.setCurrentItem(message.equals("location") ? 1 : 2);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


}