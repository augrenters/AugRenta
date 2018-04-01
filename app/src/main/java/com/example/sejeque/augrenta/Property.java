package com.example.sejeque.augrenta;

/**
 * Created by Faith on 31/03/2018.
 */

public class Property {

    //model for property
    //needed for saving data to firebase database
    //and fetching data from from firebase database

    public String description;
    public String latitude;
    public String longitude;
    public String owner;
    public String price;
    public String propertyName;

    //DO NOT REMOVE
    //critical for fetching data from firebase database
    public Property() {
    }

    public Property(String description, String latitude, String longitude, String owner, String price, String propertyName) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.owner = owner;
        this.price = price;
        this.propertyName = propertyName;
    }
}
