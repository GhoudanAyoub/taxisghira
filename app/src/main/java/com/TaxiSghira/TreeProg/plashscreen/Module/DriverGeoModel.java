package com.TaxiSghira.TreeProg.plashscreen.Module;

import com.firebase.geofire.GeoLocation;

public class DriverGeoModel {
    private String key;
    private GeoLocation geoLocation;
    private Chifor chifor;
    private boolean isDecline;

    public DriverGeoModel() {
    }

    public DriverGeoModel(String key, GeoLocation geoLocation) {
        this.key = key;
        this.geoLocation = geoLocation;
    }

    public boolean isDecline() {
        return isDecline;
    }

    public void setDecline(boolean decline) {
        isDecline = decline;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Chifor getChifor() {
        return chifor;
    }

    public void setChifor(Chifor chifor) {
        this.chifor = chifor;
    }
}
