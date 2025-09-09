package com.callerid.callmanager.database;

import android.provider.ContactsContract;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromPhoneList(List<Phone> phones) {
        return gson.toJson(phones);
    }

    @TypeConverter
    public static List<Phone> toPhoneList(String data) {
        Type listType = new TypeToken<List<Phone>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String fromAccountList(List<Account> accounts) {
        return gson.toJson(accounts);
    }

    @TypeConverter
    public static List<Account> toAccountList(String data) {
        Type listType = new TypeToken<List<Account>>() {}.getType();
        return gson.fromJson(data, listType);
    }
}
