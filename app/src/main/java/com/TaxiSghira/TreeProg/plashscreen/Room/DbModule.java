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
    public static DataBaseRoom ProvideDb(Application application){
        return Room.databaseBuilder(
                application
                , DataBaseRoom.class
                ,"DataBaseRoom")
                .build();
    }

    @Singleton
    @Provides
    public static Doa ProvideDoa(DataBaseRoom dataBaseRoom){
        return dataBaseRoom.getDoa();
    }
}
