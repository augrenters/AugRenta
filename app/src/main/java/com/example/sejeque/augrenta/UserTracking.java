package com.example.sejeque.augrenta;

/**
 * Created by Faith on 20/05/2018.
 */

public class UserTracking {
    String userId;
    String username;
    String latitude;
    String longitude;

    public UserTracking(){

    }

    public UserTracking(String userId, String username, String latitude, String longitude){
        this.userId = userId;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
