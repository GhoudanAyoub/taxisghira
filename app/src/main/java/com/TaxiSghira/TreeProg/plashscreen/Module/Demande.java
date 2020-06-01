package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Demande {

    private String ClientName;
    private String Arrive;
    private double Lnt;
    private double Long;
    private String customerId;
    public Demande() {
    }

    public Demande(String clientName, String arrive, double lnt, double aLong, String customerId) {
        ClientName = clientName;
        Arrive = arrive;
        Lnt = lnt;
        Long = aLong;
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
