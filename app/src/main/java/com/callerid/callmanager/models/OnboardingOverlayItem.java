package com.callerid.callmanager.models;

import androidx.annotation.Keep;

@Keep
public class OnboardingOverlayItem {
    public String title, description, btnText;
    public int imageRes1;

    public OnboardingOverlayItem(String title,  String description, int imageRes1,String btnText) {
        this.title = title;
        this.description = description;
        this.imageRes1 = imageRes1;
        this.btnText = btnText;
    }
}
