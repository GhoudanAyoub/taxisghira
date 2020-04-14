package com.TaxiSghira.TreeProg.plashscreen.Module;

public class UserLocation {


    private double Lnt;
    private double Long;
    private String DisplayName;

    public double getLnt() {
        return Lnt;
    }

    public void setLnt(double lnt) {
        Lnt = lnt;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public UserLocation(double lnt, double aLong, String displayName) {
        Lnt = lnt;
        Long = aLong;
        DisplayName = displayName;
    }

    public UserLocation(double lnt, double aLong) {
        Lnt = lnt;
        Long = aLong;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }
}
