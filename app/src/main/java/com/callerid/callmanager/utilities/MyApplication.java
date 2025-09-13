package com.callerid.callmanager.utilities;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {

    public static MyApplication instance;
    public static Context context;

    public static synchronized MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        context = this;

       // AppPref.setBooleanPref(this, Constant.THEME_MODE,true); //testing purpose

        boolean isDark = AppPref.getBooleanPref(this, Constant.THEME_MODE, false);
        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

    }
}
