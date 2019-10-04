package com.TaxiSghira.TreeProg.plashscreen.Module;

public class User {

    private String ID;
    private String firstName;
    private String secondName;
    private String adress;
    private String tell;
    private String gmail;
    private String WHosme;
    public User() { }

    public String getWHosme() {
        return WHosme;
    }

    public void setWHosme(String WHosme) {
        this.WHosme = WHosme;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getID() { return ID; }

    public void setID(String ID) { this.ID = ID; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTell() { return tell; }

    public void setTell(String tell) { this.tell = tell; }
}
