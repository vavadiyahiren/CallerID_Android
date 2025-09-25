package com.callerid.callmanager.core.calllogs;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.callerid.callmanager.database.AppDatabase;
import com.callerid.callmanager.database.CallLogDao;
import com.callerid.callmanager.database.CallLogEntity;
import com.callerid.callmanager.utilities.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CallLogRepository {

    private static CallLogRepository instance;
    private CallLogDao callLogDao;
    private Executor executor = Executors.newSingleThreadExecutor();

    private CallLogRepository() {
        callLogDao = MyApplication.getDatabase().callLogDao();
    }

    public static synchronized CallLogRepository getInstance() {

        if (instance == null) {
            instance = new CallLogRepository();
        }
        return instance;
    }

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
    public LiveData<List<CallLogEntity>> getCallLogsByType(int type) {
        return callLogDao.getCallLogsByType(type);
    }

    public LiveData<List<CallLogEntity>> getAllCallLogsByContact(String number) {
        return callLogDao.getCallLogsByContact(number);
    }

    public LiveData<List<CallLogEntity>> getAllCallLogsByContactList(ArrayList<String> number) {
        return callLogDao.getCallLogsByContactList(number);
    }
 public List<CallLogEntity> getAllCallLogsByContactListNew(ArrayList<String> number,Long limit) {
        return callLogDao.getCallLogsByContactListNew(number,limit);
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

    public void deleteNumber(String number) {
        executor.execute(() -> callLogDao.deleteNumber(number));
    }


}
