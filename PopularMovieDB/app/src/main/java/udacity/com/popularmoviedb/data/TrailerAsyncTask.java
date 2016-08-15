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
import udacity.com.popularmoviedb.models.Trailer;
import udacity.com.popularmoviedb.utils.DataParser;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.*;
import static udacity.com.popularmoviedb.PopularMovieApplication.getContext;

/**
 * Created by arbalan on 9/25/16.
 */
public class TrailerAsyncTask extends AsyncTask<String, Void, List<Trailer>> {
    private static final String LOG_TAG = TrailerAsyncTask.class.getSimpleName();
    public String movieId;

    @Override
    protected List<Trailer> doInBackground(String[] params) {

        if (params.length < 1) {
            return null;
        }

        movieId = params[0];
        if (!TextUtils.isEmpty(movieId)) {
            Uri.Builder builder;
            String baseTrailerUrl = BASE_URL + movieId + RESOURCE_TRAILER;
            builder = new Uri.Builder().encodedPath(baseTrailerUrl)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);

            Uri uri = builder.build();

            String resultJson = Utility.makeServiceCall(uri);
            try {
                return DataParser.getTrailerDataFromJson(resultJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailerList) {
        bulkInsertTrailersToDB(trailerList, movieId);
    }

    private void bulkInsertTrailersToDB(List<Trailer> trailers, String movieId) {
        if (trailers != null && trailers.size() > 0) {
            Vector<ContentValues> cVVector = new Vector<>(trailers.size());

            for (Trailer trailer : trailers) {
                if (trailer != null) {
                    ContentValues trailerValues = new ContentValues();
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE_KEY, trailer.getYoutubeKey());
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID, movieId);

                    cVVector.add(trailerValues);
                }
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI_TRAILER, cvArray);
            }

            Log.d(LOG_TAG, "bulk insert Complete. " + inserted + " Inserted all trailers for movie id : "+ movieId);
        }
    }
}
