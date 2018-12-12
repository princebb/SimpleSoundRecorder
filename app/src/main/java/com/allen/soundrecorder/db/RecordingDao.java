package com.allen.soundrecorder.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * author:  Allen <br>
 * date:  2018/12/3 11:38<br>
 * description:
 */
@Dao
public interface RecordingDao {

    @Insert
    void addRecording(Recording item);

    //It uses the primary keys to find the entities to delete.
    @Delete
    void removeRecording(Recording item);

    @Update
    void renameRecording(Recording item);

    @Query("SELECT * from saved_recordings ORDER BY id DESC")
    LiveData<List<Recording>> getAllRecordings();

    @Query("SELECT count(*) from saved_recordings")
    int count();
}
