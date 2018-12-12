package com.allen.soundrecorder.ui.playback;


import com.allen.soundrecorder.BasePresenter;
import com.allen.soundrecorder.BaseView;

/**
 * author:  Allen <br>
 * date:  2018/12/3 20:09<br>
 * description:Playback's contract
 */
public interface PlaybackContract {
    interface View extends BaseView<Presenter> {

        void stopPlaying();

        void updateProgressView(CharSequence text);

        void updateSeekBarProgress(int progress);

        void updateSeekBarMax(int max);

        void setPlayButtonImage(int state);

        void keepScreenOn();
    }

    interface Presenter extends BasePresenter {
        void onSeekBarProgressChanged(int progress, boolean fromUser);

        void resumePlaying();

        void stop();

        void play();

        void pause();

        void startPlaying();

        void startSeek();

        void stopSeek(int progress);
    }
}