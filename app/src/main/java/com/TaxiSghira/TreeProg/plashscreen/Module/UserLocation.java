package com.TaxiSghira.TreeProg.plashscreen.Module;

public class UserLocation {


    private double Lnt;
    private double Long;

    public UserLocation(double lnt, double aLong) {
        Lnt = lnt;
        Long = aLong;
    }

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
}
