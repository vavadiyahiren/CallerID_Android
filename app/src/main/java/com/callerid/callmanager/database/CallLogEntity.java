package com.callerid.callmanager.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "CallLogsTable")
public class CallLogEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String contactId;
    public String name;
    public String number;
    public int type;
    public String photo;
    public long date;
    public String duration;
    public String timeDuration;

    public boolean isSaved;
    public String address;
    public boolean isFavourite;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }



    // =====================================
    // ✅ Runtime-only fields (not persisted)
    // =====================================
    @androidx.room.Ignore
    public String formattedDate;

    @androidx.room.Ignore
    public String formattedTime;

    @androidx.room.Ignore
    public String formattedDuration;

    @androidx.room.Ignore
    public int callTypeIcon;

    @androidx.room.Ignore
    public String callTypeText;
}