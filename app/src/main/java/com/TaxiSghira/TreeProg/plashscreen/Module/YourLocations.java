package com.TaxiSghira.TreeProg.plashscreen.Module;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "YourLocation")
public class YourLocations {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Location_String")
    private String Location_String;
    @ColumnInfo(name = "User_id")
    private String User_id;
    @ColumnInfo(name = "lnt")
    private double lnt;
    @ColumnInfo(name = "lng")
    private double lng;

    public YourLocations() {
    }

    public YourLocations( String location_String, String user_id, double lnt, double lng) {
        Location_String = location_String;
        User_id = user_id;
        this.lnt = lnt;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getLocation_String() {
        return Location_String;
    }

    public void setLocation_String(String location_String) {
        Location_String = location_String;
    }

    public double getLnt() {
        return lnt;
    }

    public void setLnt(double lnt) {
        this.lnt = lnt;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
