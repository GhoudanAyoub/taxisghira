package com.TaxiSghira.TreeProg.plashscreen.Module;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chifor")
public class Chifor {

    @PrimaryKey(autoGenerate = true)
    private int id2;
    private String id;
    private String fullname;
    private String phone;
    private String cin;
    private String taxi_NUM;
    private double lnt;
    private double lng;
    private String Status;
    private String city;

    public Chifor() { }

    public Chifor(String id, String fullname, String phone, String cin, String taxi_NUM, double lnt, double lng, String status) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.cin = cin;
        this.taxi_NUM = taxi_NUM;
        this.lnt = lnt;
        this.lng = lng;
        Status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getTaxi_NUM() {
        return taxi_NUM;
    }

    public void setTaxi_NUM(String taxi_NUM) {
        this.taxi_NUM = taxi_NUM;
    }

    public double getLnt() {
        return lnt;
    }

    public void setLnt(double lnt) {
        this.lnt = lnt;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
