package udacity.com.popularmoviedb.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arbalan on 9/25/16.
 */

public class Trailer implements Parcelable {
    private String mId;
    private String mName;
    private String mYoutubeKey;

    public Trailer(){
    }

    public Trailer(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mYoutubeKey = source.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getYoutubeKey() {
        return mYoutubeKey;
    }

    public void setYoutubeKey(String youtubeKey) {
        mYoutubeKey = youtubeKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mYoutubeKey);
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
