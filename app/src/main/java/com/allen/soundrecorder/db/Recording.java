package com.allen.soundrecorder.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * author:  Allen <br>
 * date:  2018/12/3 10:15<br>
 * description:Element of the database
 */
@Entity(tableName = "saved_recordings")
public class Recording implements Parcelable {
    public static final Creator<Recording> CREATOR = new Creator<Recording>() {
        @Override
        public Recording createFromParcel(Parcel source) {
            return new Recording(source);
        }

        @Override
        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };
    //file name
    @NonNull
    @ColumnInfo(name = "recording_name")
    private String mName;
    //file path
    @NonNull
    @ColumnInfo(name = "file_path")
    private String mFilePath;
    //id in database
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;
    // length of recording in seconds
    @NonNull
    @ColumnInfo(name = "length")
    private long mLength;
    // date/time of the recording
    @NonNull
    @ColumnInfo(name = "time_added")
    private long mTime;

    public Recording() {
    }

    protected Recording(Parcel in) {
        this.mName = in.readString();
        this.mFilePath = in.readString();
        this.mId = in.readInt();
        this.mLength = in.readLong();
        this.mTime = in.readLong();
    }

    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(@NonNull String filePath) {
        mFilePath = filePath;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public long getLength() {
        return mLength;
    }

    public void setLength(long length) {
        mLength = length;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mFilePath);
        dest.writeInt(this.mId);
        dest.writeLong(this.mLength);
        dest.writeLong(this.mTime);
    }
}
