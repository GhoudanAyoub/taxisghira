package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Demande {

    private String ClientId;
    private String ClientName;
    private String Arrive;
    private String city;
    private double Lnt;
    private double Long;
    public Demande() {
    }

    public Demande(String clientId, String clientName, String arrive, String city, double lnt, double aLong) {
        ClientId = clientId;
        ClientName = clientName;
        Arrive = arrive;
        this.city = city;
        Lnt = lnt;
        Long = aLong;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String clientId) {
        ClientId = clientId;
    }

    public String getArrive() {
        return Arrive;
    }

    public void setArrive(String arrive) {
        Arrive = arrive;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
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
