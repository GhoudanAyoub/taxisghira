package com.TaxiSghira.TreeProg.plashscreen.Api;

import com.TaxiSghira.TreeProg.plashscreen.Callback.IGoogleAPI;

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
public class RetrofitClient {
    private static final String BaseURL = "http://maps.googleapis.com/";

    @Singleton
    @Provides
    public static IGoogleAPI FCMClient() {
        return new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(IGoogleAPI.class);
    }
}