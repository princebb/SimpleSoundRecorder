package com.allen.soundrecorder.ui.record;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.allen.soundrecorder.R;
import com.allen.soundrecorder.db.Recording;
import com.allen.soundrecorder.service.RecordingService;
import com.allen.soundrecorder.ui.MainActivity;
import com.allen.soundrecorder.ui.history.HistoryFileFragment;
import com.allen.soundrecorder.utils.Preconditions;

import static com.allen.soundrecorder.ui.MainActivity.*;
import static com.allen.soundrecorder.ui.MainActivity.CustomAdapter.HISTORY_FRAGMENT_TAG;
import static com.allen.soundrecorder.ui.MainActivity.CustomAdapter.RECORD_FRAGMENT_TAG;

/**
 * author:  Allen <br>
 * date:  2018/11/21 18:01<br>
 * description:sound recording's presenter
 */
public class RecordPresenter implements RecordContract.Presenter {
    private static final String TAG = RecordPresenter.class.getSimpleName();
    private RecordContract.View mView;
    private RecordingConnection mConnection = null;
    public RecordPresenter(RecordContract.View view) {
        Preconditions.checkNotNull(view);
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void startRecord() {
        FragmentActivity activity = ((Fragment) mView).getActivity();
        Intent intent = new Intent(activity, RecordingService.class);
        mConnection = new RecordingConnection();
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopRecord() {
        if (mConnection != null) {
            ((Fragment) mView).getActivity().unbindService(mConnection);
        }
    }

    private class RecordingConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected()");
            RecordingService.Binder binder = (RecordingService.Binder) iBinder;
            binder.getService().setCallback(new RecordingService.Callback() {
                @Override
                public void addRecording(String fileName, String filePath, long elapsedMillis) {
                    ViewPager pager = ((Fragment) mView).getActivity().findViewById(R.id.pager);
                    Fragment fragment = ((MainActivity.CustomAdapter) pager.getAdapter()).getItem(HISTORY_FRAGMENT_TAG);
                    if (fragment instanceof HistoryFileFragment) {
                        Recording item = new Recording();
                        item.setName(fileName);
                        item.setFilePath(filePath);
                        item.setLength(elapsedMillis);
                        item.setTime(System.currentTimeMillis());
                        ((HistoryFileFragment) fragment).addRecording(item);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected()");
        }
    }
}
