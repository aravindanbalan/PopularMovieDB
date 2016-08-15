package udacity.com.popularmoviedb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import udacity.com.popularmoviedb.CursorRecyclerAdapter;
import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX;

/**
 * Created by arbalan on 9/23/16.
 */

public class MovieListAdapter extends CursorRecyclerAdapter {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private WeakReference<Context> mContextRef;
    private MovieOnItemClickListener mMovieOnItemClickListener;

    public MovieListAdapter(Context context, Cursor c, MovieOnItemClickListener itemClickListener) {
        super(c);
        mContextRef = new WeakReference<>(context);
        mMovieOnItemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        ((MovieListAdapter.MovieViewHolder) holder).bindMovieObject(cursor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.movie_layout, parent, false);
        return new MovieListAdapter.MovieViewHolder(mContextRef.get(), rootView, this, mMovieOnItemClickListener);
    }

    private static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MovieListAdapter mAdapter;
        private MovieOnItemClickListener mOnItemClickListener;
        private WeakReference<Context> mContextRef;
        private View rootView;

        MovieViewHolder(Context context, View containerView, MovieListAdapter adapter, MovieOnItemClickListener itemClickListener) {
            super(containerView);
            rootView = containerView;
            itemView.setOnClickListener(this);
            mAdapter = adapter;
            mOnItemClickListener = itemClickListener;
            mContextRef = new WeakReference<>(context);
        }

        @Override
        public void onClick(View view) {
            mAdapter.onItemHolderClick(mOnItemClickListener, this, mAdapter.getCursor());
        }

        private void bindMovieObject(Cursor cursor) {
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_poster);

            int moviePosterColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
            String moviePoster = cursor.getString(moviePosterColumn);

            Log.i(LOG_TAG + " ***** Image uri:", MOVIE_DB_URL_PREFIX + moviePoster);
            Context context = mContextRef.get();

            int width = context.getResources().getDisplayMetrics().widthPixels;
            int height = context.getResources().getDisplayMetrics().heightPixels;

            ImageLoader loader = AppHandles.getImageLoader();
            loader.loadImage(MOVIE_DB_URL_PREFIX + moviePoster, posterImageView, width / 2, height / 2);
        }
    }

    private void onItemHolderClick(MovieOnItemClickListener itemClickListener, MovieListAdapter.MovieViewHolder movieViewHolder, Cursor cursor) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(cursor, movieViewHolder.getAdapterPosition());
        }
    }

    public interface MovieOnItemClickListener {
        void onItemClick(Cursor cursor, int position);
    }
}
