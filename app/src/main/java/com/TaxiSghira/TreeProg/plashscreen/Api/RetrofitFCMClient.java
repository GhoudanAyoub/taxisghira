package com.TaxiSghira.TreeProg.plashscreen.Api;

import com.TaxiSghira.TreeProg.plashscreen.Callback.IFCMService;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(ApplicationComponent.class)
public class RetrofitFCMClient {
    //private static final String BaseURL = "http://192.168.1.11/";
    private static final String BaseURL = "https://fcm.googleapis.com/";

    @Singleton
    @Provides
    public static IFCMService RetrofitFCMClient() {
        return new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(IFCMService.class);
    }
}
