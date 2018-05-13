package com.example.sejeque.augrenta;

/**
 * Created by SejeQue on 4/24/2018.
 */

public class Message {

    private String message, sender, date;

    public Message(){
    }

    public Message(String message, String sender, String date){
        this.message = message;
        this.sender = sender;
        this.date = date;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String content){
        this.message = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
