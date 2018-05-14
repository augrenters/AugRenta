package com.example.sejeque.augrenta;

/**
 * Created by SejeQue on 5/11/2018.
 */

public class RequestVisit {

    private String sender, date, time, type;
    private boolean accepted;

    public RequestVisit(){
    }
    public RequestVisit(String sender, String date, String time, String type, boolean accepted){
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.type = type;
        this.accepted = accepted;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
