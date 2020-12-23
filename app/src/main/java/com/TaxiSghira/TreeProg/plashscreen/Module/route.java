package com.TaxiSghira.TreeProg.plashscreen.Module;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class route {

    @SerializedName("legs")
    private List<legs> legsList = null;
    @SerializedName("summary")
    private String summary;
    @SerializedName("distance")
    private double distance;
    @SerializedName("duration")
    private double duration;
    @SerializedName("geometry")
    private String geometry;

    public route() {
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<legs> getLegsList() {
        return legsList;
    }

    public void setLegsList(List<legs> legsList) {
        this.legsList = legsList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
