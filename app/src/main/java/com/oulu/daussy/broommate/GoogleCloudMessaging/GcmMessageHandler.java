package com.oulu.daussy.broommate.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.oulu.daussy.broommate.Activity.MainActivity;
import com.oulu.daussy.broommate.Fragment.TabFragmentMap;
import com.oulu.daussy.broommate.R;


/**
 * Created by daussy on 23/04/16.
 */
public class GcmMessageHandler extends IntentService {

    public static final String EXTRA_MESSAGE = "com.daussy.oulu.Broommate.MESSAGE";
    String title;
    String content;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        title = extras.getString("title");
        content = extras.getString("message");
        makeNotification();
        Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }


    public void makeNotification() {

        int notificationId = 1;

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notificationId", notificationId);
        String message = content.contains("task") ? "tasks" : "location";
        resultIntent.putExtra(EXTRA_MESSAGE, message);

        PendingIntent resultPendingDismiss =
                PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(),
                        0);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder = content.contains("location") ?
                new NotificationCompat.Builder(this)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_close_white_36dp, "Dismiss", resultPendingDismiss)
                        .addAction(R.drawable.ic_gps_fixed_white_36dp, "Share", resultPendingIntent)
                :
                new NotificationCompat.Builder(this)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true);

        //Vibration
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        builder.setLights(Color.BLUE, 3000, 3000);

        //Ton
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, builder.build());

    }
}