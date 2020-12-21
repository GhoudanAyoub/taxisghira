package com.TaxiSghira.TreeProg.plashscreen.Module.EventBus;

public class DriverAcceptTripEvent {
    private String TripKey;

    public DriverAcceptTripEvent(String tripKey) {
        TripKey = tripKey;
    }

    public String getTripKey() {
        return TripKey;
    }

    public void setTripKey(String tripKey) {
        TripKey = tripKey;
    }
}
