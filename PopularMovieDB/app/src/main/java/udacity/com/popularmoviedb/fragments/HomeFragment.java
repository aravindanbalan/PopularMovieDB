package udacity.com.popularmoviedb.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import udacity.com.popularmoviedb.BuildConfig;
import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.activities.SettingsActivity;
import udacity.com.popularmoviedb.adapters.MovieListAdapterRecycler;
import udacity.com.popularmoviedb.data.MovieDataAsyncTask;
import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.utils.MovieDataParser;
import udacity.com.popularmoviedb.utils.ScrollListener;
import udacity.com.popularmoviedb.utils.Utility;

/**
 * Created by arbalan on 8/13/16.
 */

public class HomeFragment extends Fragment implements ScrollListener.LoadMoreListener, AdapterView.OnItemClickListener {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private MovieListAdapterRecycler mMovieListAdapter;
    private RecyclerView mRecyclerView;
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

        mMovieListAdapter = new MovieListAdapterRecycler(getContext(), this);
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
        mRecyclerView.addOnScrollListener(new ScrollListener(this));

        return rootView;
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
        }
    }

    @Override
    public void getNextPageOnScrolled(int nextPage) {
        new MovieDataAsyncTask(nextPage).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = mMovieListAdapter.getMovieFromPosition(position);
        if (movie != null) {
            ((Callback) getActivity()).onItemSelected(movie);
        }
        mPosition = position;
    }

    private void refreshMovieData() {
        new MovieDataAsyncTask().execute();
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }
}
