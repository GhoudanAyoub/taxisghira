package com.TaxiSghira.TreeProg.plashscreen.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.TaxiSghira.TreeProg.plashscreen.Callback.Doa;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.YourLocations;

@Database(entities = {Chifor.class, YourLocations.class}, version = 1)
public abstract class DataBaseRoom extends RoomDatabase {
    public abstract Doa getDoa();
}
