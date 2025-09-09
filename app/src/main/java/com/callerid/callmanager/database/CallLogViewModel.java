package com.callerid.callmanager.database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class CallLogViewModel extends AndroidViewModel {

    private CallLogRepository repository;
    private LiveData<List<CallLogEntity>> allCallLogs;

    public CallLogViewModel(@NonNull Application application) {
        super(application);
        repository = new CallLogRepository(application);
    }

    public LiveData<List<CallLogEntity>> getAllCallLogs() {
          return repository.getAllCallLogs();
    }
    public LiveData<List<CallLogEntity>> getCallLogsByType(int type) {
        return repository.getAllCallLogsByType(type);
    }
    public LiveData<List<CallLogEntity>> getCallLogsByContact(String number) {
        return repository.getAllCallLogsByContact(number);
    }
    public LiveData<List<CallLogEntity>> getCallLogsByContactList(ArrayList<String> number) {
        return repository.getAllCallLogsByContactList(number);
    }
 public long getLastSavedCallDate() {
        return repository.getLastSavedCallDate();
    }

    public void insert(CallLogEntity entity) {
        repository.insert(entity);
    }

    public void insertAll(List<CallLogEntity> list) {
        repository.insertAll(list);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}