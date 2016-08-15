package udacity.com.popularmoviedb.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.models.Movie;

import static android.R.attr.order;
import static udacity.com.popularmoviedb.IConstants.*;
import static udacity.com.popularmoviedb.PopularMovieApplication.getContext;

/**
 * Created by arbalan on 9/23/16.
 */

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getSortOrder(Context context) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sharedPrefs.getString(
                context.getString(R.string.pref_sorting_key),
                context.getString(R.string.pref_sorting_default));
        return sortOrder;
    }

    public static String getSavedSortOrder(Context context){
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sharedPrefs.getString(
                SORT_ORDER_ID_KEY,
                null);
        return sortOrder;
    }

    public static void saveSortOrder(Context context, String order){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SORT_ORDER_ID_KEY, order);
        editor.commit();
    }

    public static void clearStoredSortOrder(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(SORT_ORDER_ID_KEY);
        editor.commit();
    }

    public static Movie convertCursorToMovie(Cursor movieCursor) {
        if (movieCursor == null) {
            return null;
        }

        Movie movie = new Movie();

        int column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        String movieId = movieCursor.getString(column);

        column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        String moviePoster = movieCursor.getString(column);

        column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS);
        String movieSynopsis = movieCursor.getString(column);

        column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG);
        Double movieVoteAvg = movieCursor.getDouble(column);

        column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        String movieTitle = movieCursor.getString(column);

        column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        String movieReleaseDate = movieCursor.getString(column);

        column = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY);
        Double moviePopularity = movieCursor.getDouble(column);

        movie.setId(movieId);
        movie.setPosterUrl(moviePoster);
        movie.setMovieOverview(movieSynopsis);
        movie.setVoteAverage(movieVoteAvg);
        movie.setTitle(movieTitle);
        movie.setMovieReleaseDate(movieReleaseDate);
        movie.setPopularity(moviePopularity);
        return movie;
    }

    public static String fetchMovieIdFromUri(Context context, Uri movieUri) {
        long _id = MovieContract.MovieEntry.getIdFromUri(movieUri);

        Cursor c = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                new String[] { MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID },
                MovieContract.MovieEntry._ID + " = ?",
                new String[] { String.valueOf(_id) },
                null);

        if (c != null && c.moveToFirst()) {
            int movieIdIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            String movieId = c.getString(movieIdIndex);
            c.close();
            return movieId;
        } else {
            return null;
        }
    }

    public static void watchYoutubeVideo(Activity activity, String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_INTENT + id));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(YOUTUBE_TRAILER_URL + id));
            activity.startActivity(intent);
        }
    }

    public static String makeServiceCall(Uri uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJson;

        try {
            Log.i(LOG_TAG, "********* URL : " + uri.toString());
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                Log.e(LOG_TAG, "inputStream ");
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
                Log.e(LOG_TAG, "buffer length zero ");

                return null;
            }
            resultJson = buffer.toString();
            return resultJson;
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
    }

    // displays favorited/unfavorited toast based on record's current state
    public static void displayFavoritesMessage(boolean favoriteFlag, String movieTitle){

        String favoriteMessage = "";
        if (favoriteFlag)
            favoriteMessage = movieTitle + FAV_ADDED;
        else
            favoriteMessage = movieTitle + FAV_REMOVED;

        Toast appStart = Toast.makeText(getContext(), favoriteMessage, Toast.LENGTH_SHORT);
        appStart.show();
    }

}
