package udacity.com.popularmoviedb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX_500;

/**
 * Created by arbalan on 8/14/16.
 */

@Deprecated
public class MovieListAdapterRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = MovieListAdapterRecycler.class.getSimpleName();
    private WeakReference<Context> mContextRef;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private List<Movie> mMovieList;

    public MovieListAdapterRecycler(Context context, AdapterView.OnItemClickListener itemClickListener) {
        mMovieList = new ArrayList<>();
        mContextRef = new WeakReference<>(context);
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Movie item = mMovieList.get(position);
        ((MovieViewHolder) holder).bindMovieObject(item);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.movie_layout, parent, false);
        return new MovieViewHolder(mContextRef.get(), rootView, this, mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void swapData(List<Movie> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }

    public void appendData(List<Movie> movies) {
        int size_before = mMovieList.size();
        mMovieList.addAll(movies);
        notifyItemRangeInserted(size_before, movies.size());
    }

    public Movie getMovieFromPosition(int position) {
        return mMovieList.get(position);
    }

    private void onItemHolderClick(AdapterView.OnItemClickListener onItemClickListener, MovieViewHolder movieViewHolder) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(null, movieViewHolder.itemView, movieViewHolder.getAdapterPosition(), movieViewHolder.getItemId());
        }
    }

    private static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MovieListAdapterRecycler mAdapter;
        private AdapterView.OnItemClickListener mOnItemClickListener;
        private WeakReference<Context> mContextRef;
        private View rootView;

        MovieViewHolder(Context context, View containerView, MovieListAdapterRecycler adapter, AdapterView.OnItemClickListener onItemClickListener) {
            super(containerView);
            rootView = containerView;
            itemView.setOnClickListener(this);
            mAdapter = adapter;
            mOnItemClickListener = onItemClickListener;
            mContextRef = new WeakReference<>(context);
        }

        @Override
        public void onClick(View view) {
            mAdapter.onItemHolderClick(mOnItemClickListener, this);
        }

        private void bindMovieObject(Movie movie) {
            if (movie != null) {
                ImageView movie_poster = (ImageView) rootView.findViewById(R.id.movie_poster);
                Context context = mContextRef.get();
                int width = context.getResources().getDisplayMetrics().widthPixels;
                int height = context.getResources().getDisplayMetrics().heightPixels;

                ImageLoader loader = AppHandles.getImageLoader();
                loader.loadImage(MOVIE_DB_URL_PREFIX_500 + movie.getPosterUrl(), movie_poster, width / 2, height / 2);
            }
        }
    }
}
