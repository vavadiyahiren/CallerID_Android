package com.callerid.callmanager.models;

import androidx.annotation.Keep;

@Keep
public class CallLogModel {
    private String name;
    private String number;
    private int type;
    private String callType;
    private String date;
    private String duration;

    public CallLogModel(String name, String number, int type, String callType, String date, String duration) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.callType = callType;
        this.date = date;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}