package com.allen.soundrecorder.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * author:  Allen <br>
 * date:  2018/12/3 14:12<br>
 * description:View Model to keep a reference to the recording repository and
 * an up-to-date list of all recordings.
 */
public class RecordingViewModel extends AndroidViewModel {
    private RecordingRepository mRepository;
    private LiveData<List<Recording>> mAllRecordings;

    public RecordingViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RecordingRepository(application);
        mAllRecordings = mRepository.getAllRecordings();
    }

    public LiveData<List<Recording>> getAllRecordings() {
        return mAllRecordings;
    }

    public void addRecording(Recording item) {
        mRepository.addRecording(item);
    }

    public void removeRecording(Recording item) {
        mRepository.removeRecording(item);
    }

    public void renameRecording(Recording item) {
        mRepository.renameRecording(item);
    }

}
