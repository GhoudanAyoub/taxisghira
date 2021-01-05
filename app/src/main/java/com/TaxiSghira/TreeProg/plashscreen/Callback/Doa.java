package com.TaxiSghira.TreeProg.plashscreen.Callback;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.YourLocations;
import com.google.protobuf.ByteOutput;

import java.util.List;

@Dao
public interface Doa {

    @Insert
    void InsertData(Chifor chifor);

    @Query("delete from chifor where id = :id")
    void DeleteData(int id);

    @Query("select * from chifor")
    LiveData<List<Chifor>> GetData();


    @Insert
    void InsertLocation(YourLocations yourLocations);

    @Query("select * from YourLocation")
    LiveData<List<YourLocations>> GetLocation();

}
