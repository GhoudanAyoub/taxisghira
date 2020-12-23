package com.TaxiSghira.TreeProg.plashscreen.Module;

public class Client {

    private String id;
    private String Fullname;
    private String tell;
    private String gmail;
    private String city;

    public Client() {
    }

    public Client(String id, String fullname, String tell, String gmail, String city) {
        this.id = id;
        Fullname = fullname;
        this.tell = tell;
        this.gmail = gmail;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }
}
