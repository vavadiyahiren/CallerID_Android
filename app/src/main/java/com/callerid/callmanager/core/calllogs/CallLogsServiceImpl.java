package com.callerid.callmanager.core.calllogs;

import com.callerid.callmanager.database.CallLogEntity;

import java.util.List;

public class CallLogsServiceImpl implements CallLogsService {

    private static CallLogsServiceImpl instance;
    private final CallLogRepository callLogRepository;

    private CallLogsServiceImpl() {

        callLogRepository = CallLogRepository.getInstance();
    }

    public static CallLogsServiceImpl getInstance() {

        if (instance == null) {
            instance = new CallLogsServiceImpl();
        }
        return instance;
    }


    @Override
    public void insertAll(List<CallLogEntity> list) {
        callLogRepository.insertAll(list);
    }
}
