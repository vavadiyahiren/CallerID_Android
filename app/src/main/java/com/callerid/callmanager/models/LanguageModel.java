package com.callerid.callmanager.models;

import androidx.annotation.Keep;

@Keep
public class LanguageModel {
    private String englishName;
    private String localizedName;
    private String code;
    private boolean isSelected;

    public LanguageModel(String englishName, String localizedName, String code,Boolean isSelected) {
        this.englishName = englishName;
        this.localizedName = localizedName;
        this.code = code;
        this.isSelected = isSelected;
    }
    public LanguageModel(String englishName, String localizedName, String code) {
        this.englishName = englishName;
        this.localizedName = localizedName;
        this.code = code;
        this.isSelected = false;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getCode() {
        return code;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
