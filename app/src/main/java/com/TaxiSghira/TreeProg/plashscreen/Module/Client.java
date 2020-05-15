package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Client {

    private String Fullname;
    private String tell;
    private String gmail;
    private String WHosme;
    public Client() { }

    public Client(String fullname, String tell, String gmail, String WHosme) {
        Fullname = fullname;
        this.tell = tell;
        this.gmail = gmail;
        this.WHosme = WHosme;
    }

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

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String Fullname) {
        this.Fullname = Fullname;
    }

    public String getTell() { return tell; }

    public void setTell(String tell) { this.tell = tell; }
}
