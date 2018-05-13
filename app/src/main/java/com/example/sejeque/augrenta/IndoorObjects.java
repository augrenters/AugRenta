package com.example.sejeque.augrenta;

/**
 * Created by Faith on 13/05/2018.
 */

public class IndoorObjects {
    public String propertyId;
    public String latitude;
    public String longitude;
    public String altitude;
    public String title;
    public String description;

    //DO NOT REMOVE
    //critical for fetching data from firebase database
    public IndoorObjects() {
    }

    public IndoorObjects(String propertyId, String latitude, String longitude, String altitude, String title, String description) {
        this.propertyId = propertyId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.title = title;
        this.description = description;
    }
}
