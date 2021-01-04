package com.TaxiSghira.TreeProg.plashscreen.Module;

public class YourLocations {

    private String Location_String;
    private String User_id;
    private double lnt;
    private double lng;

    public YourLocations() {
    }

    public YourLocations( String location_String, String user_id, double lnt, double lng) {
        Location_String = location_String;
        User_id = user_id;
        this.lnt = lnt;
        this.lng = lng;
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
