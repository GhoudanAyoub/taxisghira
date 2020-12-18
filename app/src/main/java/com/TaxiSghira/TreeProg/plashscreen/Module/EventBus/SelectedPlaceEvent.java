package com.TaxiSghira.TreeProg.plashscreen.Module.EventBus;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class SelectedPlaceEvent {
    private Location origin;
    private Location destination;
    private String OriginString,DestinationString;

    public SelectedPlaceEvent() { }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public String getOriginString() {
        return OriginString;
    }

    public void setOriginString(String originString) {
        OriginString = originString;
    }

    public String getDestinationString() {
        return DestinationString;
    }

    public void setDestinationString(String destinationString) {
        DestinationString = destinationString;
    }
}
