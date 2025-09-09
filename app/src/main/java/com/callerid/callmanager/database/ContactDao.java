package com.callerid.callmanager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContactEntity contact);

    @Query("SELECT * FROM contacts ORDER BY displayName COLLATE NOCASE ASC")
    LiveData<List<ContactEntity>> getAllContacts();

    @Query("DELETE FROM contacts")
    void deleteAll();

    @Update
    void updateContact(ContactEntity contactEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContactEntity> contacts);

    @Query("SELECT * FROM contacts WHERE contactId = :contactId")
    LiveData<ContactEntity> getContactByContactId(String contactId);

    @Query("SELECT * FROM contacts WHERE contactId = :contactId")
    ContactEntity getContactByContactIdNew(String contactId);



}