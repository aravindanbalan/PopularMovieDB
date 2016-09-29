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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import udacity.com.popularmoviedb.ItemClickCallback;
import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.adapters.MovieListAdapter;
import udacity.com.popularmoviedb.data.MovieContract;

/**
 * Created by arbalan on 9/26/16.
 */

public class FavoritesFragment  extends Fragment implements MovieListAdapter.MovieOnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>  {
    private MovieListAdapter mMovieListAdapter;
    private RecyclerView mRecyclerView;
    private static final int FAVORITE_LOADER = 0;
    private int mPosition = -1;
    private static final String SELECTED_POSITION = "selected_position";
    private static final int PORTRAIT_COLUMNS = 2;
    private static final int PORTRAIT_COLUMNS_WITHOUT_DETAILFRAGMENT = 2;
    private static final int LANDSCAPE_COLUMNS = 3;
    private static final int LANDSCAPE_COLUMNS_WITHOUT_DETAILFRAGMENT = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieListAdapter = new MovieListAdapter(getContext(), null, this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_list);
        GridLayoutManager gridLayoutManager;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), PORTRAIT_COLUMNS_WITHOUT_DETAILFRAGMENT);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), LANDSCAPE_COLUMNS_WITHOUT_DETAILFRAGMENT);
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieListAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_POSITION)) {
            mPosition = savedInstanceState.getInt(SELECTED_POSITION);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.favorite_movies));
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != -1) {
            outState.putInt(SELECTED_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI_FAVORITE,
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

    public void onDetailsFragmentShown(){
        if(mRecyclerView!=null) {
            GridLayoutManager gridLayoutManager;
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager = new GridLayoutManager(getActivity(), PORTRAIT_COLUMNS);
            } else {
                gridLayoutManager = new GridLayoutManager(getActivity(), LANDSCAPE_COLUMNS);
            }
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
    }

}
