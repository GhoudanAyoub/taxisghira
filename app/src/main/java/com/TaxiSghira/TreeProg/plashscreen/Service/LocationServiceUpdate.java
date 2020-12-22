package com.TaxiSghira.TreeProg.plashscreen.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.UserLocation;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.ui.LocationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                    updateUser(userLocation,location);
                }

            }
        }, Looper.myLooper());

    }


    private void updateUser(final UserLocation userLocation, Location location){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/l/0" , userLocation.getLnt());
        childUpdates.put("/l/1" , userLocation.getLong());
        try {

            String city = LocationUtils.getAddressFromLocation(getApplicationContext(),location);
            FireBaseClient.getFireBaseClient()
                    .getDatabaseReference()
                    .child(Common.CLIENT_LOCATION_REFERENCES)
                    .child(city)
                    .child(Common.Current_Client_Id)
                    .updateChildren(childUpdates);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

