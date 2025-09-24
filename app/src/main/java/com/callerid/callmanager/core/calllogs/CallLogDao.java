package com.callerid.callmanager.core.calllogs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.callerid.callmanager.database.CallLogEntity;

import java.util.List;

@Dao
public interface CallLogDao {

    @Insert
    long insertCallLog(CallLogEntity callLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCallLogs(List<CallLogEntity> callLogs);

    @Query("SELECT * FROM CallLogsTable ORDER BY date DESC")
    LiveData<List<CallLogEntity>> getAllCallLogs();

    @Query("SELECT * FROM CallLogsTable WHERE isFavourite = 1")
    LiveData<List<CallLogEntity>> getFavouriteCallLogs();

    @Query("SELECT * FROM CallLogsTable WHERE type = :callType ORDER BY date DESC")
    LiveData<List<CallLogEntity>> getCallLogsByType(int callType);

    @Update
    void updateCallLog(CallLogEntity callLog);

    @Delete
    void deleteCallLog(CallLogEntity callLog);

    @Query("DELETE FROM CallLogsTable")
    void deleteAll();

    @Query("SELECT MAX(date) FROM CallLogsTable") // Replace with your table name
    Long getLastSavedCallDate();

    @Query("SELECT * FROM CallLogsTable WHERE number = :number ORDER BY date DESC")
    LiveData<List<CallLogEntity>> getCallLogsByContact(String number);


    @Query("SELECT * FROM CallLogsTable WHERE number IN (:numbers) ORDER BY date DESC")
    LiveData<List<CallLogEntity>> getCallLogsByContactList(List<String> numbers);


    @Query("SELECT * FROM CallLogsTable WHERE number = :number ORDER BY date DESC limit :limit")
    List<CallLogEntity> getCallLogsByContactNew(String number,Long limit);
    @Query("SELECT * FROM CallLogsTable WHERE number IN (:numbers) ORDER BY date DESC limit :limit")
    List<CallLogEntity> getCallLogsByContactListNew(List<String> numbers,Long limit);


    @Query("UPDATE CallLogsTable SET name = '', contactId='' WHERE number = :number")
    void deleteNumber(String number);
}
