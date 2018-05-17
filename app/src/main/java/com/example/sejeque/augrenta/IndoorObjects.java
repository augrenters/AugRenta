package com.example.sejeque.augrenta;

/**
 * Created by Faith on 13/05/2018.
 */

public class IndoorObjects {
    public String label;
    public String distance;
    public String latitude;
    public String longitude;

    //DO NOT REMOVE
    //critical for fetching data from firebase database
    public IndoorObjects() {
    }

    public IndoorObjects(String label, String distance, String latitude, String longitude) {
        this.label = label;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
