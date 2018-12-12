package com.allen.soundrecorder.ui.history;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.allen.soundrecorder.R;
import com.allen.soundrecorder.db.Recording;

import java.io.File;

import static com.allen.soundrecorder.service.RecordingService.rootPath;

/**
 * author:  Allen <br>
 * date:  2018/11/21 18:15<br>
 * description:history file's presenter
 */
public class HistoryFilePresenter implements HistoryFileContract.Presenter {
    private HistoryFileContract.View mView;

    public HistoryFilePresenter(HistoryFileContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void rename(Recording item) {
        String name = item.getName();
        String mFilePath = rootPath + name;
        File f = new File(mFilePath);
        if (f.exists() && !f.isDirectory()) {
            //file name is not unique, cannot rename file.
            mView.showFileExistTips(name);
        } else {
            //file name is unique, rename file
            File oldFilePath = new File(item.getFilePath());
            oldFilePath.renameTo(f);
            item.setFilePath(mFilePath);
            //del the database data
            mView.getViewMode().renameRecording(item);
        }
    }

    @Override
    public void remove(Recording item) {
        //del the recording file
        File file = new File(item.getFilePath());
        file.delete();
        //del the database data
        mView.getViewMode().removeRecording(item);
    }
}
