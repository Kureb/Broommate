package com.oulu.daussy.broommate.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.oulu.daussy.broommate.R;


/**
 * Created by daussy on 23/04/16.
 */
public class GcmMessageHandler extends IntentService {

    String title;
    String content;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        title = extras.getString("title");
        content = extras.getString("message");
        //showToast();
        makeNotification();
        Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
            }
        });

    }


    public void makeNotification() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_delete)
                        .setContentTitle(title)
                        .setContentText(content);

        //Vibration
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        builder.setLights(Color.BLUE, 3000, 3000);

        //Ton
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        
        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, builder.build());

    }
}