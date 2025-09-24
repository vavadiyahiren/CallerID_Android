package com.callerid.callmanager.core.contacts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.callerid.callmanager.database.AppDatabase;
import com.callerid.callmanager.database.ContactDao;
import com.callerid.callmanager.database.ContactEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class ContactViewModel extends AndroidViewModel {

    private final ContactDao contactDao;
    private final LiveData<List<ContactEntity>> allContacts;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public ContactViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        contactDao = db.contactDao();
        allContacts = contactDao.getAllContacts(); // Observed by UI
    }

    public LiveData<List<ContactEntity>> getAllContacts() {
        return allContacts;
    }

    public LiveData<ContactEntity> getContactsByContactId(String contactId) {
        return contactDao.getContactByContactId(contactId);
    }

    public void insertAll(List<ContactEntity> contacts) {
        executor.execute(() -> contactDao.insertAll(contacts));
    }

    public void deleteAll() {
        executor.execute(contactDao::deleteAll);
    }

    public void deleteContact(ContactEntity contactEntity) {
        executor.execute(() -> contactDao.deleteContactById(contactEntity.contactId));
    }

    public void updateContact(ContactEntity contactEntity) {
        executor.execute(() -> contactDao.updateContact(contactEntity));
    }
}
