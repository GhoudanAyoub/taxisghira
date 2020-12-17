package com.TaxiSghira.TreeProg.plashscreen.Module.EventBus;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class SelectedPlaceEvent {
    private Location origin;
    private Location destination;

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

    public String getOriginString(){return this.origin.getLatitude()+","+this.origin.getLongitude();}
    public String getDestinationString(){return this.destination.getLatitude()+","+this.destination.getLongitude();}
}
