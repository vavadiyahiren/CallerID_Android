package com.callerid.callmanager.core.contacts;

import com.callerid.callmanager.core.calllogs.CallLogRepository;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;

import java.util.List;

public class ContactServiceImpl implements ContactService {

    private static ContactServiceImpl instance;
    private final ContactRepository contactRepository;

    private ContactServiceImpl() {
        contactRepository = ContactRepository.getInstance();
    }

    public static ContactServiceImpl getInstance() {

        if (instance == null) {
            instance = new ContactServiceImpl();
        }
        return instance;
    }


    @Override
    public void insertAll(List<ContactEntity> list) {
        contactRepository.insertAll(list);
    }
}
