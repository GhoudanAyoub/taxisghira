package com.TaxiSghira.TreeProg.plashscreen.Module.EventBus;

public class DriverCompleteTrip {
    private String key;

    public DriverCompleteTrip() {
    }

    public DriverCompleteTrip(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
