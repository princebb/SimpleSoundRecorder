package com.allen.soundrecorder.ui.playback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allen.soundrecorder.R;
import com.allen.soundrecorder.db.Recording;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.allen.soundrecorder.utils.Preconditions.checkNotNull;

/**
 * author:  Allen <br>
 * date:  2018/12/3 20:09<br>
 * description:This page for play recording
 */
public class PlaybackFragment extends DialogFragment implements PlaybackContract.View {
    static final int PS_PAUSE = 0x0002;
    static final int PS_PLAYING = 0x0001;
    private static final String TAG = PlaybackFragment.class.getSimpleName();
    private static final String ARG_ITEM = "recording_item";
    //stores minutes and seconds of the length of the file.
    long minutes = 0;
    long seconds = 0;
    private Recording item;
    private SeekBar mSeekBar = null;
    private FloatingActionButton mPlayButton = null;
    private TextView mCurrentProgressTextView = null;
    private TextView mFileLengthTextView = null;
    //stores whether or not the mediaplayer is currently playing audio
    private boolean isPlaying = false;
    private PlaybackContract.Presenter mPresenter;

    public PlaybackFragment newInstance(Recording item) {
        PlaybackFragment f = new PlaybackFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM, item);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable(ARG_ITEM);

        long itemDuration = item.getLength();
        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialogNoTitleBase);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_media_playback, null);
        TextView fileNameTextView = view.findViewById(R.id.file_name_text_view);
        mFileLengthTextView = view.findViewById(R.id.file_length_text_view);
        mCurrentProgressTextView = view.findViewById(R.id.current_progress_text_view);

        mSeekBar = view.findViewById(R.id.seekbar);
        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPresenter.onSeekBarProgressChanged(progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPresenter.startSeek();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.stopSeek(seekBar.getProgress());
            }
        });

        mPlayButton = view.findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
        });

        fileNameTextView.setText(item.getName());
        mFileLengthTextView.setText(String.format("%02d:%02d", minutes, seconds));

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
    }

    // Play start/stop
    private void onPlay(boolean isPlaying) {
        if (!isPlaying) {
            mPresenter.play();
        } else {
            mPresenter.pause();
        }
    }

    @Override
    public void stopPlaying() {
        setPlayButtonImage(PS_PLAYING);
        updateSeekBarProgress(mSeekBar.getMax());
        isPlaying = !isPlaying;

        updateProgressView(mFileLengthTextView.getText());
        updateSeekBarProgress(mSeekBar.getMax());

        //allow the screen to turn off again once audio is finished playing
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void updateProgressView(CharSequence text) {
        mCurrentProgressTextView.setText(text);
    }

    @Override
    public void updateSeekBarProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    @Override
    public void updateSeekBarMax(int max) {
        mSeekBar.setMax(max);
    }

    @Override
    public void setPlayButtonImage(int state) {
        switch (state) {
            case PS_PLAYING://playing
                mPlayButton.setImageResource(R.mipmap.play);
                break;
            case PS_PAUSE://pause
                mPlayButton.setImageResource(R.mipmap.pause);
                break;
        }
    }

    @Override
    public void keepScreenOn() {
        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void setPresenter(PlaybackContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
