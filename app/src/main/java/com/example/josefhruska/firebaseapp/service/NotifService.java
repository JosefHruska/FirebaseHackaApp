package com.example.josefhruska.firebaseapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.josefhruska.firebaseapp.R;

/**
 * Created by josefhruska on 16.7.16.
 */
public class NotifService extends IntentService {

    public NotifService(){
        super("NotifService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundleNotif = intent.getBundleExtra("bundle");
        String userId = bundleNotif.getString("user_id");

        String notif_text = getString(R.string.notif_1) + userId + getString(R.string.notif_2);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_adb_black_24dp)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(notif_text);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
}
