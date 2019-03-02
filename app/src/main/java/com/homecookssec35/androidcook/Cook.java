package com.homecookssec35.androidcook;

public class Cook {

    private int id;
    private String name , contact ;
    private double latitude,longitude;

    public Cook(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
