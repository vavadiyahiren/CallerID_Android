package com.callerid.callmanager.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "contacts", indices = {@androidx.room.Index(value = {"normalizedNumber"}, unique = true)} )
@TypeConverters(Converters.class)
public class ContactEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String displayName;

    public String photo;

    public boolean isFavourite;

    public boolean isSaved;

    public List<Phone> phones;

    public List<Account> accounts;

    public boolean isBlocked;

    public String address;

    public String callType;

    public String oprator;

    public String favoritesIndex;

    public String callTime;

    public boolean spam;

    public String contactId;

    public String normalizedNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return displayName;
    }

    public void setName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getOprator() {
        return oprator;
    }

    public void setOprator(String oprator) {
        this.oprator = oprator;
    }

    public String getFavoritesIndex() {
        return favoritesIndex;
    }

    public void setFavoritesIndex(String favoritesIndex) {
        this.favoritesIndex = favoritesIndex;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public boolean isSpam() {
        return spam;
    }

    public void setSpam(boolean spam) {
        this.spam = spam;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getNormalizedNumber() {
        return normalizedNumber;
    }

    public void setNormalizedNumber(String normalizedNumber) {
        this.normalizedNumber = normalizedNumber;
    }
}

