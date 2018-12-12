package com.allen.soundrecorder.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * author:  Allen <br>
 * date:  2018/12/3 12:54<br>
 * description:
 */

@Database(entities = {Recording.class}, version = 1, exportSchema = false)
public abstract class RecordingRoomDatabase extends RoomDatabase {
    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile RecordingRoomDatabase INSTANCE;

    static RecordingRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordingRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordingRoomDatabase.class, "saved_recordings")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract RecordingDao recordingDao();
}
