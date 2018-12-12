package com.allen.soundrecorder.ui.history;


import com.allen.soundrecorder.BasePresenter;
import com.allen.soundrecorder.BaseView;
import com.allen.soundrecorder.db.Recording;
import com.allen.soundrecorder.db.RecordingViewModel;

/**
 * author:  Allen <br>
 * date:  2018/11/21 18:15<br>
 * description:history file's contract
 */
public interface HistoryFileContract {
    interface View extends BaseView<Presenter> {
        void showOptionsDialog(Recording item);

        void showRenameDialog(Recording item);

        void showRemoveDialog(Recording item);

        void showPlaybackDialog(Recording item);

        void showFileExistTips(String name);

        void showToastTips(String tips);

        RecordingViewModel getViewMode();
    }

    interface Presenter extends BasePresenter {
        void rename(Recording item);

        void remove(Recording item);
    }
}