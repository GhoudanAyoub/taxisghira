package com.TaxiSghira.TreeProg.plashscreen.Callback;

import com.TaxiSghira.TreeProg.plashscreen.Module.FCMResponse;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization: key=AAAAnSVv7kU:APA91bErWb1Ps-29el8QjW-UdQA21QNcvDyf8yBHJBpJWhNA140K7Xr0JysE4--eLlI9NK0721-s53Hqp6uHPzafQ9sd_EvWBGt-N5ymSuy0H0paEl4xJZG-sbh1jDufr702fTzbYFBn"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
