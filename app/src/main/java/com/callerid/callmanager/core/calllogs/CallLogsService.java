package com.callerid.callmanager.core.calllogs;

import com.callerid.callmanager.database.CallLogEntity;

import java.util.List;

public interface CallLogsService {

    void insertAll(List<CallLogEntity> list);


}
