package com.allen.soundrecorder.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allen.soundrecorder.R;
import com.allen.soundrecorder.db.Recording;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author:  Allen <br>
 * date:  2018/11/21 19:24<br>
 * description:The adapter for recyclerView
 */
public class HistoryFileAdapter extends RecyclerView.Adapter<HistoryFileAdapter.RecordingsViewHolder> {
    private static final String TAG = HistoryFileAdapter.class.getSimpleName();
    //Cached copy of recording
    private List<Recording> mRecordings = Collections.emptyList();

    private Context mContext;
    private Callback mCallback;

    public HistoryFileAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecordingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new RecordingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordingsViewHolder holder, int position) {
        final Recording item = mRecordings.get(position);
        long length = item.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(length);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(minutes);
        holder.mName.setText(item.getName());
        holder.mLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.mDateAdded.setText(DateUtils.formatDateTime(mContext, item.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)
        );
        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.itemOnLongClick(item);
                }
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //play recording
                if (mCallback != null) {
                    mCallback.itemOnClick(item);
                }
                Log.d(TAG, "cardView Click");
            }
        });
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mCallback != null) {
                    mCallback.itemOnLongClick(item);
                }
                Log.d(TAG, "cardView on long Click");
                return false;
            }
        });
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return mRecordings.size();
    }

    public void setRecordings(List<Recording> recordings) {
        mRecordings = recordings;
        notifyDataSetChanged();
    }


    public interface Callback {
        void itemOnLongClick(Recording item);

        void itemOnClick(Recording item);
    }

    static class RecordingsViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mLength;
        private final TextView mDateAdded;
        private final TextView mMore;
        private final View mCardView;

        public RecordingsViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.file_name_text);
            mLength = itemView.findViewById(R.id.file_length_text);
            mDateAdded = itemView.findViewById(R.id.file_date_added_text);
            mMore = itemView.findViewById(R.id.file_more);
            mCardView = itemView.findViewById(R.id.card_view);
        }
    }
}
