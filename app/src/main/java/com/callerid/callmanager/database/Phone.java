package com.callerid.callmanager.database;

import androidx.annotation.Keep;

@Keep
public class Phone {
    public String number;
    public String normalizedNumber;
    public int type;
    public String label;
}
