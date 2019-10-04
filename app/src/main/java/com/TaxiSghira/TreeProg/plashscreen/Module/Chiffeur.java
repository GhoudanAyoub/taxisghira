package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Chiffeur extends User {

    private String CIN;
    private int NUM;
    private String Counteur;
    private String image;

    public Chiffeur() { }

    public Chiffeur(String CIN, int NUM, String counteur, String url) {
        this.CIN = CIN;
        this.NUM = NUM;
        Counteur = counteur;
        image = url;
    }

    public String getCIN() {
        return CIN;
    }

    public void setCIN(String CIN) {
        this.CIN = CIN;
    }

    public int getNUM() {
        return NUM;
    }

    public void setNUM(int NUM) {
        this.NUM = NUM;
    }

    public String getCounteur() {
        return Counteur;
    }

    public void setCounteur(String counteur) {
        Counteur = counteur;
    }

    public String getimage() {
        return image;
    }

    public void setimage(String url) {
        image = url;
    }
}
