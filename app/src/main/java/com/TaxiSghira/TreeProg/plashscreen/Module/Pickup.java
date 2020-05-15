package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Pickup {


    private Demande demande;
    private String Ch_Name;
    private String Ch_num;
    private String Taxi_num;
    private Boolean Status;

    public Pickup() {
    }


    public Pickup(Demande demande, String ch_Name, String ch_num, String taxi_num, Boolean s) {
        this.demande = demande;
        Ch_Name = ch_Name;
        Ch_num = ch_num;
        Taxi_num = taxi_num;
        Status = s;
    }


    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public Demande getDemande() {
        if (demande == null) {
            demande = new Demande();
        }
        return demande;
    }


    public String getCh_Name() {
        return Ch_Name;
    }

    public void setCh_Name(String ch_Name) {
        Ch_Name = ch_Name;
    }

    public String getCh_num() {
        return Ch_num;
    }

    public void setCh_num(String ch_num) {
        Ch_num = ch_num;
    }

    public String getTaxi_num() {
        return Taxi_num;
    }

    public void setTaxi_num(String taxi_num) {
        Taxi_num = taxi_num;
    }

}