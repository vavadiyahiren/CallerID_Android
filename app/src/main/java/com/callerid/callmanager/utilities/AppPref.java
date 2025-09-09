package com.callerid.callmanager.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPref {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String PREF = "caller_pref";

    public static String getStringPref(Context context, String key, String defVal){
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(key, defVal);
    }

    public static void setStringPref(Context context, String key, String value){
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static long getLongPref(Context context, String key, long defVal){
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getLong(key, defVal);
    }

    public static void setLongPref(Context context, String key, long value){
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static int getIntegerPref(Context context, String key, int defVal){
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(key, defVal);
    }

    public static void setIntegerPref(Context context, String key, int value){
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean getBooleanPref(Context context, String key, boolean defVal){
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(key, defVal);
    }

    public static void setBooleanPref(Context context, String key, boolean value){
        sharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}
