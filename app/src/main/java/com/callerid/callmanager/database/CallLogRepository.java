package com.callerid.callmanager.database;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CallLogRepository {

    private CallLogDao callLogDao;
    private Executor executor = Executors.newSingleThreadExecutor();

    public CallLogRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        callLogDao = db.callLogDao();
    }

    public LiveData<List<CallLogEntity>> getAllCallLogs() {
        return callLogDao.getAllCallLogs();
    }
    public LiveData<List<CallLogEntity>> getAllCallLogsByType(int callType) {
        return callLogDao.getCallLogsByType(callType);
    }
    public LiveData<List<CallLogEntity>> getAllCallLogsByContact(String number) {
        return callLogDao.getCallLogsByContact(number);
    }
    public LiveData<List<CallLogEntity>> getAllCallLogsByContactList(ArrayList<String> number) {
        return callLogDao.getCallLogsByContactList(number);
    }
    public long getLastSavedCallDate() {
        return callLogDao.getLastSavedCallDate();
    }

    public void insert(CallLogEntity entity) {
        executor.execute(() -> callLogDao.insertCallLog(entity));
    }

    public void insertAll(List<CallLogEntity> list) {
        executor.execute(() -> callLogDao.insertCallLogs(list));
    }
    public void updateCallLog(CallLogEntity callLogEntity) {
        executor.execute(() -> callLogDao.updateCallLog(callLogEntity));
    }

    public void deleteAll() {
        executor.execute(() -> callLogDao.deleteAll());
    }
}
