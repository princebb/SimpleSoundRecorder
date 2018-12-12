package com.allen.soundrecorder.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Update;
import android.os.AsyncTask;

import java.util.List;

/**
 * author:  Allen <br>
 * date:  2018/12/3 13:22<br>
 * description:
 */
public class RecordingRepository {
    private RecordingDao mRecordingDao;
    private LiveData<List<Recording>> mAllRecordings;
    private static final int ADD_RECORDING_TAG = 0;
    private static final int REMOVE_RECORDING_TAG = 1;
    private static final int RENAME_RECORDING_TAG = 2;
    public RecordingRepository(Application application) {
        RecordingRoomDatabase db = RecordingRoomDatabase.getDatabase(application);
        mRecordingDao = db.recordingDao();
        mAllRecordings = mRecordingDao.getAllRecordings();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Recording>> getAllRecordings() {
        return mAllRecordings;
    }

    void addRecording(Recording item) {
        new doAsyncTask(mRecordingDao, ADD_RECORDING_TAG).execute(item);
    }

    void removeRecording(Recording item) {
        new doAsyncTask(mRecordingDao, REMOVE_RECORDING_TAG).execute(item);
    }

    void renameRecording(Recording item) {
        new doAsyncTask(mRecordingDao, RENAME_RECORDING_TAG).execute(item);
    }

    private static class doAsyncTask extends AsyncTask<Recording, Void, Void> {

        private RecordingDao mAsyncTaskDao;
        private int mType;

        doAsyncTask(RecordingDao dao, int type) {
            mAsyncTaskDao = dao;
            mType = type;
        }

        @Override
        protected Void doInBackground(final Recording... params) {
            switch (mType) {
                case ADD_RECORDING_TAG:
                    mAsyncTaskDao.addRecording(params[0]);
                    break;
                case REMOVE_RECORDING_TAG:
                    mAsyncTaskDao.removeRecording(params[0]);
                    break;
                case RENAME_RECORDING_TAG:
                    mAsyncTaskDao.renameRecording(params[0]);
                    break;
                default:
                    break;
            }
            return null;
        }
    }
}
