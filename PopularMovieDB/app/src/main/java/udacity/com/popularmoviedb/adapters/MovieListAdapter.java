package udacity.com.popularmoviedb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX;

/**
 * Created by arbalan on 9/23/16.
 */

public class MovieListAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    public MovieListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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

        Uri imageUri = Uri.parse(MOVIE_DB_URL_PREFIX).buildUpon()
                .appendPath(moviePoster)
                .build();

        Log.d(LOG_TAG + " - Image uri:", imageUri.toString());

        Picasso.with(context).load(imageUri)
                .into(posterImageView);

        ImageLoader loader = AppHandles.getImageLoader();
        loader.loadImage(imageUri.toString(), posterImageView);
    }
}
