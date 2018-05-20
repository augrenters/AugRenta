package com.example.sejeque.augrenta;

/**
 * Created by Faith on 20/05/2018.
 */

public class UserStatus {
    String status;
    String user;
    String property;
    String owner;

    public UserStatus(){
    }

    public UserStatus(String status, String user, String property, String owner){
        this.status = status;
        this.user = user;
        this.property = property;
        this.owner = owner;
    }

}
