package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Module.DriverGeoModel;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMSendData;
import com.TaxiSghira.TreeProg.plashscreen.Module.TokenModel;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserUtils {

    public static void UpdateToken(Context context, String token) {
        TokenModel token1 = new TokenModel(token);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            FirebaseDatabase.getInstance()
                    .getReference(Common.CLIENT_TOKEN_REFERENCE)
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .setValue(token1)
                    .addOnFailureListener(Throwable::printStackTrace)
                    .addOnSuccessListener(aVoid -> {
                    });
    }

    public static void sendRequestToDriver(MapViewModel mapViewModel, LatLng Destination_point, String Destination_String, DriverGeoModel foundDriver, Location location, Client client) {

        CompositeDisposable disposable = new CompositeDisposable();
        FirebaseDatabase
                .getInstance()
                .getReference(Common.DRIVER_TOKEN_REFERENCE)
                .child(foundDriver.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            TokenModel token = snapshot.getValue(TokenModel.class);
                            Map<String, String> notification = new HashMap<String, String>();
                            notification.put(Common.NOTI_TITLE, Common.REQUEST_DRIVER_TITLE);
                            notification.put(Common.NOTI_BODY, "Message For Driver Action");
                            notification.put(Common.RIDER_KEY, Common.Current_Client_Id);
                            notification.put(Common.CLIENT_DATA, client.getFullname() + "," + client.getTell());
                            notification.put(Common.RIDER_PICKUP_LOCATION_STRING, location.toString());
                            notification.put(Common.RIDER_PICK_UP_LOCATION, location.getLongitude() + "," + location.getLatitude());
                            notification.put(Common.RIDER_DESTINATION_STRING, Destination_String);
                            notification.put(Common.RIDER_DESTINATION, Destination_point.latitude + "," + Destination_point.longitude);
                            FCMSendData fcmSendData = new FCMSendData(token.getToken(), notification);
                            disposable.add(mapViewModel.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {
                                        if (fcmResponse.getSuccess() == 0) {
                                            disposable.clear();
                                            Log.e("RD","clear");
                                        }
                                    }, Throwable::printStackTrace));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static Bitmap CreateIconWithDuration(Context context, String duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.pick_up_info_duration, null);
        TextView textView = view.findViewById(R.id.duration_text);
        textView.setText(duration.substring(0, duration.indexOf(" ")));

        IconGenerator icnGenerator = new IconGenerator(context);
        icnGenerator.setContentView(view);
        icnGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        return icnGenerator.makeIcon();
    }
}
