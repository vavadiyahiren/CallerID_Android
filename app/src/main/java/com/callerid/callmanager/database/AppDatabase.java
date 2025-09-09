package com.callerid.callmanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CallLogEntity.class, ContactEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "call_log_db"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract CallLogDao callLogDao();
    public abstract ContactDao contactDao();
}
