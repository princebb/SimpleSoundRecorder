package com.allen.soundrecorder.ui.record;


import android.app.Activity;
import android.content.Context;

import com.allen.soundrecorder.BasePresenter;
import com.allen.soundrecorder.BaseView;

/**
 * author:  Allen <br>
 * date:  2018/11/21 18:02<br>
 * description:sound recording's contract
 */
public interface RecordContract {
    interface View extends BaseView<Presenter> {
        void showRecordingState();

        void showNormalState();
    }

    interface Presenter extends BasePresenter {
        void startRecord();

        void stopRecord();
    }
}