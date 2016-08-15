package udacity.com.popularmoviedb.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import udacity.com.popularmoviedb.BuildConfig;
import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.utils.DataParser;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.PopularMovieApplication.getContext;

/**
 * Created by arbalan on 9/24/16.
 */

@Deprecated
public class MovieFetchService extends IntentService {
    private final String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    private final String BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated";
    private final String PAGE_PARAM = "page";
    private final String API_KEY_PARAM = "api_key";
    public static final String PAGE_QUERY_EXTRA = "page";
    private final String LOG_TAG = MovieFetchService.class.getSimpleName();

    public MovieFetchService() {
        super("MovieFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String page = intent.getStringExtra(PAGE_QUERY_EXTRA);
        List<Movie> movies = getJsonDataFromWeatherApI(page);
        bulkInsertToDB(movies);
    }

    private List<Movie> getJsonDataFromWeatherApI(String page) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJson;
        String sortOrder = Utility.getSortOrder(getContext());

        try {
            Uri.Builder builder;
            if (!TextUtils.isEmpty(sortOrder) && sortOrder.equalsIgnoreCase(getContext().getString(R.string.sort_top_rated))) {
                builder = new Uri.Builder().encodedPath(BASE_URL_TOP_RATED)
                        .appendQueryParameter(PAGE_PARAM, page)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
            } else {
                //default to popular
                builder = new Uri.Builder().encodedPath(BASE_URL_POPULAR)
                        .appendQueryParameter(PAGE_PARAM, page)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
            }

            Uri uri = builder.build();
            Log.i(LOG_TAG, "********* URL : " + uri.toString());
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line)
                        .append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            resultJson = buffer.toString();
            Log.i(LOG_TAG, "Movie results output : json : " + resultJson);

            try {
                return DataParser.getMovieDataFromJson(resultJson);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    private void bulkInsertToDB(List<Movie> movies) {
        if (movies != null) {
            Vector<ContentValues> cVVector = new Vector<>(movies.size());

            for (Movie movie : movies) {
                if (movie != null) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, movie.getPosterUrl());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movie.getMovieOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, movie.getVoteAverage());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getMovieReleaseDate());

                    cVVector.add(movieValues);
                }
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI_MOVIE, cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, MovieFetchService.class);
            sendIntent.putExtra(PAGE_QUERY_EXTRA, intent.getStringArrayExtra(PAGE_QUERY_EXTRA));
            context.startService(sendIntent);
        }
    }
}
