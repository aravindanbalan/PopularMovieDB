package udacity.com.popularmoviedb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX_500;

/**
 * Created by arbalan on 9/24/16.
 */

@Deprecated
public class MovieListCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = MovieListCursorAdapter.class.getSimpleName();

    public MovieListCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView posterImageView = (ImageView) view.findViewById(R.id.movie_poster);

        int moviePosterColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        String moviePoster = cursor.getString(moviePosterColumn);

        Log.i(LOG_TAG + " ***** Image uri:", MOVIE_DB_URL_PREFIX_500 + moviePoster);

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        ImageLoader loader = AppHandles.getImageLoader();
        loader.loadImage(MOVIE_DB_URL_PREFIX_500 + moviePoster, posterImageView, width / 2, height / 2);
    }
}
