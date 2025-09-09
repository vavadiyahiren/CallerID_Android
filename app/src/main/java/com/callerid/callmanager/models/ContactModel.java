package com.callerid.callmanager.models;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

@Keep
public class ContactModel {
    private String name;
    private String number;
    private String normalizeNumber;
    private String threadId;
    private int backgroundColor;
    private int textColor;
    private String contactId;
    private String typeNumber;

    public String getTypeNumber() {
        return typeNumber;
    }

    public void setTypeNumber(String typeNumber) {
        this.typeNumber = typeNumber;
    }

    private List<String> numbers= new ArrayList<>();

    public List<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        this.numbers = numbers;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getNormalizeNumber() {
        return normalizeNumber;
    }

    public void setNormalizeNumber(String normalizeNumber) {
        this.normalizeNumber = normalizeNumber;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public ContactModel(String contactId, String name, String number, String threadId) {
        this.contactId = contactId;
        this.name = name;
        this.number = number;
        this.threadId = threadId;
    }


    public ContactModel(String contactId, String name, String number) {
        this.contactId = contactId;
        this.name = name;
        this.number = number;
    }

    public ContactModel(String contactId, String name, String number, int backgroundColor, int textColor) {
        this.contactId = contactId;
        this.name = name;
        this.number = number;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
