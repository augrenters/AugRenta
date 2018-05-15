package com.example.sejeque.augrenta;

/**
 * Created by SejeQue on 5/15/2018.
 */

public class RequestVisitData {

    private String propertyId, senderId, sender, date, time, type, propertyName;
    private boolean accepted;

    public RequestVisitData() {
    }

    public RequestVisitData(String propertyId, String senderId, String sender, String date, String time, String type, String propertyName, boolean accepted) {
        this.propertyId = propertyId;
        this.senderId = senderId;
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.type = type;
        this.propertyName = propertyName;
        this.accepted = accepted;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
