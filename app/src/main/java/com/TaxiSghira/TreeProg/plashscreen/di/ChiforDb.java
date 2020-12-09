package com.TaxiSghira.TreeProg.plashscreen.di;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;

@Database(entities = {Chifor.class},version = 1)
public abstract class ChiforDb extends RoomDatabase {
    public abstract Doa getDoa();
}
