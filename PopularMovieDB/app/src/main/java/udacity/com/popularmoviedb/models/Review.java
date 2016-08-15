package udacity.com.popularmoviedb.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arbalan on 9/25/16.
 */

public class Review implements Parcelable {
    private String mId;
    private String mReviewAuthor;
    private String mReviewContent;
    private String mReviewUrl;

    public Review(){
    }

    public Review(Parcel source) {
        mId = source.readString();
        mReviewAuthor = source.readString();
        mReviewContent = source.readString();
        mReviewUrl = source.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        mReviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

    public void setReviewContent(String reviewContent) {
        mReviewContent = reviewContent;
    }

    public String getReviewUrl() {
        return mReviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        mReviewUrl = reviewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mReviewAuthor);
        dest.writeString(mReviewContent);
        dest.writeString(mReviewUrl);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
