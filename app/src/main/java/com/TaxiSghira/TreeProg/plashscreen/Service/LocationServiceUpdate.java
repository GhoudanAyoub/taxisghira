package com.TaxiSghira.TreeProg.plashscreen.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.GeoPoint;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class LocationServiceUpdate extends Service {


    private static final String TAG = "LocationService";
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String CHANNEL_ID = "my_channel_01";
        String CHANNEL_NAME = "location_Channel";
        if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
                ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Location")
                        .setContentText("Location update").build();
                startForeground(1,notification);
        }
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.tag(TAG).e("OnstartCommand : Called");
        getLocaion();
        return START_NOT_STICKY;
    }

    private void getLocaion(){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            Timber.tag(TAG).e("getLocation: stopping the location service.");
            stopSelf();
            return;
        }

        mFusedLocationClient.requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location location = locationResult.getLastLocation();

                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    UserLocation userLocation = new UserLocation( geoPoint.getLatitude(),geoPoint.getLongitude());
                    updateUser(userLocation);
                }
            }
        }, Looper.myLooper());

    }


    private void updateUser(final UserLocation userLocation){
        UserLocation userLocation1 = new UserLocation(userLocation.getLnt(),userLocation.getLong(),FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName());

        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection("Demande")
                .document( userLocation1.getDisplayName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                                .collection("Demande")
                                .document(userLocation1.getDisplayName())
                                .update("lnt",userLocation1.getLnt(),"long",userLocation1.getLong())
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        Timber.tag("ERR").d("onComplete: \ninserted user location into database." +
                                                "\n latitude: " + userLocation.getLnt() +
                                                "\n longitude: " + userLocation.getLong());
                                    }
                                });
                    }
                });

    }
}

