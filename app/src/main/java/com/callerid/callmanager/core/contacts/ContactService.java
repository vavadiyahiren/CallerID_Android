package com.callerid.callmanager.core.contacts;

import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.database.ContactEntity;

import java.util.List;

public interface ContactService {
    void insertAll(List<ContactEntity> contacts);

}
