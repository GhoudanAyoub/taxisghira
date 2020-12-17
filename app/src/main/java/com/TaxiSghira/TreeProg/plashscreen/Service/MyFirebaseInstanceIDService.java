package com.TaxiSghira.TreeProg.plashscreen.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DeclineRequestFromDriver;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.ui.SplashScreen;
import com.TaxiSghira.TreeProg.plashscreen.ui.UserUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (Common.Current_Client_Id != null)
            UserUtils.UpdateToken(this,s);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        try {
            Map<String,String> dataRec = remoteMessage.getData();
            if (dataRec!=null){
                if (dataRec.get(Common.NOTI_TITLE) != null){
                    if (Objects.requireNonNull(dataRec.get(Common.NOTI_TITLE)).equals(Common.REQUEST_DRIVER_DECLINE)){
                        EventBus.getDefault().postSticky(new DeclineRequestFromDriver());
                    }else {
                        messagingstyle_Notification(new Random().nextInt());
                    }
                }
            }
        } catch (Throwable e) {
            Timber.e("onMessageReceivedNotification: %s", e.getMessage());
        }

    }

    private void messagingstyle_Notification(int notificationId) {
        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = null;
        Intent intent = new Intent(getApplicationContext(), SplashScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "TreeProg_TaxiSghira";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "TaxiSghira", NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0,1000,500,1000});
            channel.enableVibration(true);
            channel.setDescription("TaxiSghira");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.taxisymb)
                .setContentTitle("TaxiSghira")
                .setContentText("لقد اتى سائقك")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.no))
                .setSound(path);

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, builder.build());
    }
}