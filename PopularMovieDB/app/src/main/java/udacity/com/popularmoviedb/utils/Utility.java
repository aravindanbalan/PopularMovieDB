package udacity.com.popularmoviedb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.models.Movie;

/**
 * Created by arbalan on 9/23/16.
 */

public class Utility {
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_SYNOPSIS = 3;
    static final int COL_MOVIE_VOTE_AVG = 4;
    static final int COL_MOVIE_TITLE = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int COL_MOVIE_FAV = 7;

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
        String movieId = movieCursor.getString(COL_MOVIE_ID);
        String moviePoster = movieCursor.getString(COL_MOVIE_POSTER);
        String movieSynopsis = movieCursor.getString(COL_MOVIE_SYNOPSIS);
        Double movieVoteAvg = movieCursor.getDouble(COL_MOVIE_VOTE_AVG);
        String movieTitle = movieCursor.getString(COL_MOVIE_TITLE);
        String movieReleaseDate = movieCursor.getString(COL_MOVIE_RELEASE_DATE);

        movie.setId(movieId);
        movie.setPosterUrl(moviePoster);
        movie.setMovieOverview(movieSynopsis);
        movie.setVoteAverage(movieVoteAvg);
        movie.setTitle(movieTitle);
        movie.setMovieReleaseDate(movieReleaseDate);
        return movie;
    }
}
