package com.example.sejeque.augrenta;

/**
 * Created by Faith on 13/05/2018.
 */

public class User {
    public String userId;
    public String username;
    public String email;
    public String deviceToken;

    //DO NOT REMOVE
    //critical for fetching data from firebase database
    public User() {
    }

    public User(String userId, String username, String email, String deviceToken) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.deviceToken = deviceToken;
    }


}
