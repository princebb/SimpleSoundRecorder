package com.allen.soundrecorder.ui.playback;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.allen.soundrecorder.ui.playback.PlaybackFragment.PS_PAUSE;

/**
 * author:  Allen <br>
 * date:  2018/12/3 20:09<br>
 * description:Playback's presenter
 */
public class PlaybackPresenter implements PlaybackContract.Presenter {
    private static final String TAG = PlaybackPresenter.class.getSimpleName();
    private static String mFilePath;
    private PlaybackContract.View mView;
    private Handler mHandler = new Handler();
    private MediaPlayer mMediaPlayer = null;
    //updating mSeekBar
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mView.updateSeekBarProgress(mCurrentPosition);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
                mView.updateProgressView(String.format("%02d:%02d", minutes, seconds));
                postRunnable();
            }
        }
    };

    public PlaybackPresenter(PlaybackContract.View view, String filePath) {
        mView = view;
        mView.setPresenter(this);
        mFilePath = filePath;
    }

    @Override
    public void start() {

    }

    @Override
    public void onSeekBarProgressChanged(int progress, boolean fromUser) {
        if (mMediaPlayer != null && fromUser) {
            mMediaPlayer.seekTo(progress);
            mHandler.removeCallbacks(mRunnable);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
            long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                    - TimeUnit.MINUTES.toSeconds(minutes);
            mView.updateProgressView(String.format("%02d:%02d", minutes, seconds));
        } else if (mMediaPlayer == null && fromUser) {
            prepareMediaPlayerFromPoint(progress);
        }
        postRunnable();
    }

    private void prepareMediaPlayerFromPoint(int progress) {
        //set mediaPlayer to start from middle of the audio file
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mFilePath);
            mMediaPlayer.prepare();
            mView.updateSeekBarMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(progress);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                    mView.stopPlaying();
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mView.keepScreenOn();
    }


    private void postRunnable() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void resumePlaying() {
        mView.setPlayButtonImage(PS_PAUSE);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        postRunnable();
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mHandler.removeCallbacks(mRunnable);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void play() {
        //currently MediaPlayer is not playing audio
        if (mMediaPlayer == null) {
            startPlaying(); //start from beginning
        } else {
            resumePlaying(); //resume the currently paused MediaPlayer
        }
    }

    @Override
    public void pause() {
        mHandler.removeCallbacks(mRunnable);
        //pause the MediaPlayer
        mMediaPlayer.pause();
    }

    @Override
    public void startPlaying() {
        mView.setPlayButtonImage(PS_PAUSE);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mFilePath);
            mMediaPlayer.prepare();
            mView.updateSeekBarMax(mMediaPlayer.getDuration());
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
                mView.stopPlaying();
            }
        });
        postRunnable();
        mView.keepScreenOn();
    }

    @Override
    public void startSeek() {
        if (mMediaPlayer != null) {
            // remove message Handler from updating progress bar
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void stopSeek(int progress) {
        if (mMediaPlayer != null) {
            mHandler.removeCallbacks(mRunnable);
            mMediaPlayer.seekTo(progress);

            long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
            long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                    - TimeUnit.MINUTES.toSeconds(minutes);
            mView.updateProgressView(String.format("%02d:%02d", minutes, seconds));
            postRunnable();
        }
    }
}
