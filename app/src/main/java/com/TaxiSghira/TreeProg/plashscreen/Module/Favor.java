package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Favor {
    private String id;
    private String Ch_Name;
    private String Ch_num;
    private String Taxi_num;
    private String client_name;
    public Favor() { }

    public Favor(String id, String ch_Name, String ch_num, String taxi_num, String Clientname) {
        this.id = id;
        Ch_Name = ch_Name;
        Ch_num = ch_num;
        Taxi_num = taxi_num;
        client_name = Clientname;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public void setCh_Name(String ch_Name) {
        Ch_Name = ch_Name;
    }

    public void setCh_num(String ch_num) {
        Ch_num = ch_num;
    }

    public void setTaxi_num(String taxi_num) {
        Taxi_num = taxi_num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCh_Name() {
        return Ch_Name;
    }

    public String getCh_num() {
        return Ch_num;
    }

    public String getTaxi_num() {
        return Taxi_num;
    }
}
