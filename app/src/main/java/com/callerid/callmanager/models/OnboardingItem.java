package com.callerid.callmanager.models;

import androidx.annotation.Keep;

@Keep
public class OnboardingItem {
    public String title, number, subtitle, description;
    public int imageRes1,imageRes2,imageRes3;
    public boolean showWarning;

    public OnboardingItem(String title, String number, String subtitle, String description, int imageRes1,int imageRes2,int imageRes3, boolean showWarning) {
        this.title = title;
        this.number = number;
        this.subtitle = subtitle;
        this.description = description;
        this.imageRes1 = imageRes1;
        this.imageRes2 = imageRes2;
        this.imageRes3 = imageRes3;
        this.showWarning = showWarning;
    }
}
