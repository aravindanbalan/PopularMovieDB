package udacity.com.popularmoviedb.fragments;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.activities.SettingsActivity;
import udacity.com.popularmoviedb.adapters.MovieListAdapter;
import udacity.com.popularmoviedb.adapters.MovieListCursorAdapter;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.data.MovieDataAsyncTask;
import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.utils.ScrollListener;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.PopularMovieApplication.getContext;

/**
 * Created by arbalan on 8/13/16.
 */

public class HomeFragment extends Fragment implements ScrollListener.LoadMoreListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private MovieListCursorAdapter mMovieListAdapter;
    private GridView mGridView;
    private String mSortOrder;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_POSITION = "selected_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieListAdapter = new MovieListCursorAdapter(getContext(), null, 0);
        mGridView = (GridView) rootView.findViewById(R.id.movie_list) ;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridView.setNumColumns(2);
        } else {
            mGridView.setNumColumns(3);
        }

        mGridView.setAdapter(mMovieListAdapter);
        mGridView.setOnScrollListener(new ScrollListener(this));
        mGridView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrder(getContext());

        //avoid extra call to the service on resume if the sort order hasn't changed
        if (!TextUtils.isEmpty(sortOrder) && !sortOrder.equals(mSortOrder)) {
            refreshMovieData();
            mSortOrder = sortOrder;
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

        }
    }

    @Override
    public void getNextPageOnScrolled(int nextPage) {
        new MovieDataAsyncTask(nextPage).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null && cursor.moveToPosition(position)) {
            // convert cursor into movie object
            final int MOVIE_ID_COL = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            Uri movieUri = MovieContract.MovieEntry.buildMovieUri(cursor.getInt(MOVIE_ID_COL));
            ((Callback) getActivity()).onItemSelected(movieUri);
        }

        mPosition = position;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrderSetting = Utility.getSortOrder(getContext());
        String sortOrder;

        if (sortOrderSetting.equals(getString(R.string.pref_sorting_default))) {
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            //sort by rating
            sortOrder = MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG + " DESC";
        }

        //FIXME Need help here, order differs between tablet and mobile
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieListAdapter.swapCursor(null);
    }

    private void refreshMovieData() {
        new MovieDataAsyncTask().execute();
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }
}
