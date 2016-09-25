package udacity.com.popularmoviedb.data;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.util.List;
import java.util.Vector;

import udacity.com.popularmoviedb.BuildConfig;
import udacity.com.popularmoviedb.models.Review;
import udacity.com.popularmoviedb.utils.DataParser;

import static udacity.com.popularmoviedb.IConstants.API_KEY_PARAM;
import static udacity.com.popularmoviedb.IConstants.BASE_URL;
import static udacity.com.popularmoviedb.IConstants.RESOURCE_REVIEW;
import static udacity.com.popularmoviedb.PopularMovieApplication.getContext;
import static udacity.com.popularmoviedb.adapters.MovieListCursorAdapter.LOG_TAG;
import static udacity.com.popularmoviedb.utils.Utility.makeServiceCall;

/**
 * Created by arbalan on 9/25/16.
 */

public class ReviewAsyncTask extends AsyncTask<String, Void, List<Review>> {
    public String movieId;

    @Override
    protected List<Review> doInBackground(String[] params) {

        if (params.length < 1) {
            return null;
        }
        movieId = params[0];
        if (!TextUtils.isEmpty(movieId)) {
            Uri.Builder builder;
            String baseReviewUrl = BASE_URL + movieId + RESOURCE_REVIEW;
            builder = new Uri.Builder().encodedPath(baseReviewUrl)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

            Uri uri = builder.build();

            String resultJson = makeServiceCall(uri);
            try {
                return DataParser.getReviewDataFromJson(resultJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviewList) {
        bulkInsertReviewToDB(reviewList, movieId);
    }

    private void bulkInsertReviewToDB(List<Review> reviews, String movieId) {
        if (reviews != null && reviews.size() > 0) {
            Vector<ContentValues> cVVector = new Vector<>(reviews.size());

            for (Review review : reviews) {
                if (review != null) {
                    ContentValues reviewValues = new ContentValues();
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR_NAME, review.getReviewAuthor());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getReviewContent());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, review.getReviewUrl());
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID, movieId);

                    cVVector.add(reviewValues);
                }
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI_REVIEW, cvArray);
            }

            Log.d(LOG_TAG, "bulk insert Complete. " + inserted + " Inserted all reviews for movie id : " + movieId);
        }
    }
}