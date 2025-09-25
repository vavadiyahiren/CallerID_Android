package com.callerid.callmanager.core.contacts;

import androidx.lifecycle.LiveData;

import com.callerid.callmanager.database.AppDatabase;
import com.callerid.callmanager.database.ContactDao;
import com.callerid.callmanager.database.ContactEntity;
import com.callerid.callmanager.utilities.MyApplication;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContactRepository {

    private static ContactRepository instance;
    private final ContactDao contactDao;

    private static final Executor executor = Executors.newSingleThreadExecutor();

    private ContactRepository() {
        contactDao = MyApplication.getDatabase().contactDao();
    }

    public static synchronized ContactRepository getInstance() {

        if (instance == null) {
            instance = new ContactRepository();
        }
        return instance;
    }
    public LiveData<List<ContactEntity>> getAllContacts() {
        return contactDao.getAllContacts();
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
