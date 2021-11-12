package com.TaxiSghira.TreeProg.plashscreen.Service;

import androidx.annotation.NonNull;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DeclineRequestAndRemoveTripFromDriver;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DeclineRequestFromDriver;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DriverAcceptTripEvent;
import com.TaxiSghira.TreeProg.plashscreen.Module.EventBus.DriverCompleteTrip;
import com.TaxiSghira.TreeProg.plashscreen.ui.UserUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
            UserUtils.UpdateToken(this, s);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        try {
            Map<String, String> dataRec = remoteMessage.getData();
            if (dataRec != null) {
                if (dataRec.get(Common.NOTI_TITLE) != null) {
                    if (Objects.requireNonNull(dataRec.get(Common.NOTI_TITLE)).equals(Common.REQUEST_DRIVER_DECLINE)) {
                        DeclineRequestFromDriver declineRequestFromDriver = new DeclineRequestFromDriver();
                        declineRequestFromDriver.setKey(dataRec.get(Common.DRIVER_KEY));
                        EventBus.getDefault().postSticky(declineRequestFromDriver);
                    }
                    else if (Objects.requireNonNull(dataRec.get(Common.NOTI_TITLE)).equals(Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP)) {
                        DeclineRequestAndRemoveTripFromDriver declineRequestAndRemoveTripFromDriver = new DeclineRequestAndRemoveTripFromDriver();
                        declineRequestAndRemoveTripFromDriver.setKey(dataRec.get(Common.DRIVER_KEY));
                        EventBus.getDefault().postSticky(declineRequestAndRemoveTripFromDriver);
                    }
                    else if (Objects.requireNonNull(dataRec.get(Common.NOTI_TITLE)).equals(Common.REQUEST_DRIVER_ACCEPT)) {
                        String TripKey = dataRec.get(Common.TRIP_KEY);
                        EventBus.getDefault().postSticky(new DriverAcceptTripEvent(TripKey));
                    }
                    else if (Objects.requireNonNull(dataRec.get(Common.NOTI_TITLE)).equals(Common.RIDER_COMPLETE_TRIP)) {
                        String tripkey = dataRec.get(Common.DRIVER_KEY);
                        EventBus.getDefault().postSticky(new DriverCompleteTrip(tripkey));
                    }
                    else {
                        Common.messagingstyle_Notification(getApplicationContext(), new Random().nextInt(),  "لقد اتى سائقك");
                    }
                }
            }
        } catch (Throwable e) {
          e.printStackTrace(); }

    }

}