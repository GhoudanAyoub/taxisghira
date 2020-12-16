package com.TaxiSghira.TreeProg.plashscreen.Module;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class routes {
    @SerializedName("routes")
    private List<route> routeList = null;

    public routes() { }

    public routes(List<route> routeList) { this.routeList = routeList; }

    public List<route> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<route> routeList) {
        this.routeList = routeList;
    }
}
