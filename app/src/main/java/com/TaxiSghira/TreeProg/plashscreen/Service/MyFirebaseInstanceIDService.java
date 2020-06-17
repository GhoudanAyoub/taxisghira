package com.TaxiSghira.TreeProg.plashscreen.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.TaxiSghira.TreeProg.plashscreen.Both.SpachScreen;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        try {
            if (remoteMessage.getData().size() > 0 || remoteMessage.getNotification() != null) {
                messagingstyle_Notification();
            }
        } catch (Throwable e) {
            Log.e("feald", "onMessageReceivedNotification: " + e.getMessage());
        }

    }

    private void messagingstyle_Notification() {
        Intent intent = new Intent(getApplicationContext(), SpachScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int notificationId = 4;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.no)
                .setContentTitle("TaxiSghira")
                .setContentText("لقد اتى سائقك")
                .setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(path);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        notificationManager.notify(notificationId, builder.build());
    }
}