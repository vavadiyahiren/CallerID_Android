package com.callerid.callmanager.models;

import android.net.Uri;

public class BlockedContact {
    private String name;
    private String phoneNumber;
    private String contactId;


    public BlockedContact(String name, String phoneNumber, String contactId, Uri photoUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.contactId = contactId;
    }

    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getContactId() { return contactId; }

}