package com.allen.soundrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.allen.soundrecorder.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author:  Allen <br>
 * date:  2018/12/3 16:23<br>
 * description:
 */
public class RecordingService extends Service {
    public static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/";
    private static final String TAG = RecordingService.class.getSimpleName();
    private MediaRecorder mRecorder = null;
    private String mFileName;
    private String mFilePath;
    private long mStartingTimeMillis = 0;
    private Callback mCallback;

    @Override
    public void onCreate() {
        Log.d(TAG, " service onCreate()");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, " service onBind()");
        startRecording();
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " service onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " service onDestroy()");
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void startRecording() {
        setFilePath();
        startMediaRecorder();
    }

    private void startMediaRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Log.e(TAG, "start recording failed");
        }
    }

    private void setFilePath() {
        int count = 0;
        File file;
        do {
            count++;
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("M月d日 H点m分");
            mFileName = formatter.format(date) + "_" + count;
            mFilePath = rootPath + mFileName;
            file = new File(mFilePath);
        } while (file.exists() && !file.isDirectory());
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void stopRecording() {
        mRecorder.stop();
        long elapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        mRecorder = null;
        try {
            if (mCallback != null) {
                mCallback.addRecording(mFileName, mFilePath, elapsedMillis);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception", e);
        }
    }

    public interface Callback {
        void addRecording(String fileName, String filePath, long elapsedMillis);
    }

    public class Binder extends android.os.Binder {
        public RecordingService getService() {
            return RecordingService.this;
        }
    }
}
