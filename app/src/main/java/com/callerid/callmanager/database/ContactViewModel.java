package com.callerid.callmanager.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

public class ContactViewModel extends AndroidViewModel {

    private final ContactDao contactDao;
    private final LiveData<List<ContactEntity>> allContacts;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        contactDao = db.contactDao();
        allContacts = contactDao.getAllContacts(); // Automatically observed
    }

    public LiveData<List<ContactEntity>> getAllContacts() {
        return allContacts;
    }

    public LiveData<ContactEntity> getContactsByContactId(String contactId) {
        return contactDao.getContactByContactId(contactId);
    }

    public void insertAll(List<ContactEntity> contacts) {
        Executors.newSingleThreadExecutor().execute(() -> contactDao.insertAll(contacts));
    }

    public void deleteAll() {
        Executors.newSingleThreadExecutor().execute(contactDao::deleteAll);
    }

    public void updateContact(ContactEntity contactEntity) {
        Executors.newSingleThreadExecutor().execute(() -> contactDao.updateContact(contactEntity));
    }
}
