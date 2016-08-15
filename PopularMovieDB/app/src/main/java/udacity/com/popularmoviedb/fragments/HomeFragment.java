package udacity.com.popularmoviedb.fragments;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import udacity.com.popularmoviedb.ItemClickCallback;
import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.adapters.MovieListAdapter;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.sync.MovieSyncAdapter;
import udacity.com.popularmoviedb.utils.RecyclerViewScrollListener;
import udacity.com.popularmoviedb.utils.Utility;

/**
 * Created by arbalan on 8/13/16.
 */

public class HomeFragment extends Fragment implements MovieListAdapter.MovieOnItemClickListener, RecyclerViewScrollListener.LoadMoreListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private MovieListAdapter mMovieListAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = -1;
    private static final String SELECTED_POSITION = "selected_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieListAdapter = new MovieListAdapter(getContext(), null, this);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_list);
        GridLayoutManager gridLayoutManager;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieListAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        //FIXME picasso too slow to load images. Need help in fixing this image stutter on scroll. Temporarily added a cache for recyclerview.
        mRecyclerView.setItemViewCacheSize(50);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(this));

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_POSITION)) {
            mPosition = savedInstanceState.getInt(SELECTED_POSITION);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != -1) {
            outState.putInt(SELECTED_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSettingChanged() {

        String sortOrder = Utility.getSortOrder(getContext());

        if (!TextUtils.isEmpty(sortOrder)) {
            if (sortOrder.equalsIgnoreCase(getString(R.string.pref_sorting_default))) {
                getActivity().setTitle(getString(R.string.popular_movies));
            } else {
                getActivity().setTitle(getString(R.string.toprated_movies));
            }
        }

        refreshMovieData();
    }

    @Override
    public void getNextPageOnScrolled(int nextPage) {
        MovieSyncAdapter.syncImmediately(getActivity(), Integer.toString(nextPage));
    }

    @Override
    public void onItemClick(Cursor cursor, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            // convert cursor into movie object
            final int MOVIE_ID_COL = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            Uri movieUri = MovieContract.MovieEntry.buildMovieUri(cursor.getInt(MOVIE_ID_COL));
            ((ItemClickCallback) getActivity()).onItemSelected(movieUri);
        }

        mPosition = position;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                new String[] { MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER },
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter.swapCursor(data);

        if (mPosition != -1) {
            mRecyclerView.scrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }

    private void refreshMovieData() {
        deleteAllPreviousDataFromDB();
        MovieSyncAdapter.syncImmediately(getActivity(), Integer.toString(1));
    }

    private void deleteAllPreviousDataFromDB() {
        getContext().getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI_TRAILER, null, null);
        getContext().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI_REVIEW, null, null);
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI_MOVIE, null, null);
    }
}
