package udacity.com.popularmoviedb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.models.Movie;

/**
 * Created by arbalan on 9/23/16.
 */

public class Utility {
    public static String getSortOrder(Context context){
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sharedPrefs.getString(
                context.getString(R.string.pref_sorting_key),
                context.getString(R.string.pref_sorting_default));
        return sortOrder;
    }

    public static Movie convertCursorToMovie(Cursor movieCursor){
        if(movieCursor == null) {
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
}
