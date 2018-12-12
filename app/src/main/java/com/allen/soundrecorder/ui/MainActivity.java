package com.allen.soundrecorder.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.allen.soundrecorder.R;
import com.allen.soundrecorder.ui.history.HistoryFileFragment;
import com.allen.soundrecorder.ui.history.HistoryFilePresenter;
import com.allen.soundrecorder.ui.record.RecordFragment;
import com.allen.soundrecorder.ui.record.RecordPresenter;
import com.astuetz.PagerSlidingTabStrip;

/**
 * author:  Allen <br>
 * date:  2018/11/21 14:32<br>
 * description:
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new CustomAdapter(getSupportFragmentManager()));
        mTabs = findViewById(R.id.tabs);
        mTabs.setViewPager(mPager);
    }

    public class CustomAdapter extends FragmentPagerAdapter {
        private String[] tabTitles = {getString(R.string.tab_title_record),
                getString(R.string.tab_title_file_viewer)};
        private RecordFragment mRecordFragment;
        private HistoryFileFragment mHistoryFileFragment;
        public static final int RECORD_FRAGMENT_TAG = 0;
        public static final int HISTORY_FRAGMENT_TAG = 1;

        public CustomAdapter(FragmentManager fm) {
            super(fm);
            mRecordFragment = RecordFragment.newInstance();
            mHistoryFileFragment = HistoryFileFragment.newInstance();
            new RecordPresenter(mRecordFragment);
            new HistoryFilePresenter(mHistoryFileFragment);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case RECORD_FRAGMENT_TAG:
                    return mRecordFragment;
                case HISTORY_FRAGMENT_TAG:
                    return mHistoryFileFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
