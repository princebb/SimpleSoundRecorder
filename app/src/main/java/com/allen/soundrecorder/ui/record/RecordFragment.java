package com.allen.soundrecorder.ui.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.allen.soundrecorder.R;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

import static com.allen.soundrecorder.utils.Preconditions.checkNotNull;

/**
 * author:  Allen <br>
 * date:  2018/11/21 18:00<br>
 * description:sound recording
 */
public class RecordFragment extends Fragment implements RecordContract.View {


    private static final String TAG = RecordFragment.class.getSimpleName();
    private Chronometer mChronometer;
    private TextView mRecordingTips;
    private FloatingActionButton mRecordButton;
    private boolean mStartRecording = true;
    private RecordContract.Presenter mPresenter;
    private int mRecordPromptCount = 0;
    public static RecordFragment newInstance() {
        return new RecordFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);
        mChronometer = recordView.findViewById(R.id.chronometer);
        mRecordingTips = recordView.findViewById(R.id.recording_tips);
        mRecordButton = recordView.findViewById(R.id.btn_record);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRecordLogic();
            }
        });
        return recordView;
    }

    private void doRecordLogic() {
        Acp.getInstance(getContext()).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.RECORD_AUDIO)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        if (mStartRecording) {
                            mPresenter.startRecord();
                            showRecordingState();
                            Log.d(TAG, "开始录音");
                        } else {
                            showNormalState();
                            mPresenter.stopRecord();
                            Log.d(TAG, "停止录音");
                        }
                        mStartRecording = !mStartRecording;
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Log.d(TAG, permissions.toString() + "权限拒绝");
                    }
                });
    }


    @Override
    public void setPresenter(RecordContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showRecordingState() {
        mRecordButton.setImageResource(R.mipmap.stop);
        //start chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                startRecordingTipsAnim();
            }
        });
        startRecordingTipsAnim();
    }

    private void startRecordingTipsAnim() {
        mRecordPromptCount++;
        StringBuilder sb = new StringBuilder(getString(R.string.recording));
        for (int i = 0; i < mRecordPromptCount; i++) {
            sb.append(".");
        }
        mRecordingTips.setText(sb.toString());
        if (mRecordPromptCount > 2) {
            mRecordPromptCount = 0;
        }
    }

    @Override
    public void showNormalState() {
        //stop chronometer
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mRecordingTips.setText(getString(R.string.stop_recording));
        mRecordingTips.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecordingTips.setText(getString(R.string.record_tips));
            }
        }, 3000);
        mRecordButton.setImageResource(R.mipmap.mic);
    }
}
