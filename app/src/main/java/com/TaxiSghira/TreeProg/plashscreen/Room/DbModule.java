package com.TaxiSghira.TreeProg.plashscreen.Room;

import android.app.Application;

import androidx.room.Room;

import com.TaxiSghira.TreeProg.plashscreen.Callback.Doa;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class DbModule {

    @Singleton
    @Provides
    public static ChiforDb ProvideDb(Application application){
        return Room.databaseBuilder(
                application
                ,ChiforDb.class
                ,"ChiforDb")
                .build();
    }

    @Singleton
    @Provides
    public static Doa ProvideDoa(ChiforDb chiforDb){
        return chiforDb.getDoa();
    }
}
