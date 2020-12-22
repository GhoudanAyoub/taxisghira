package com.TaxiSghira.TreeProg.plashscreen.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.TaxiSghira.TreeProg.plashscreen.Callback.Doa;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;

@Database(entities = {Chifor.class},version = 1)
public abstract class ChiforDb extends RoomDatabase {
    public abstract Doa getDoa();
}
