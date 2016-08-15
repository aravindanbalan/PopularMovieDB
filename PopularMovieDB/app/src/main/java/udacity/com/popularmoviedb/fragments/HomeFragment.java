package udacity.com.popularmoviedb.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
import udacity.com.popularmoviedb.activities.DetailsActivity;
import udacity.com.popularmoviedb.activities.SettingsActivity;
import udacity.com.popularmoviedb.adapters.MovieListAdapterRecycler;
import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.utils.MovieDataParser;
import udacity.com.popularmoviedb.utils.ScrollListener;

import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;

/**
 * Created by arbalan on 8/13/16.
 */

public class HomeFragment extends Fragment implements ScrollListener.LoadMoreListener, AdapterView.OnItemClickListener {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();
    private MovieListAdapterRecycler mMovieListAdapter;
    private RecyclerView mRecyclerView;

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
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
        Log.i(LOG_TAG, "***** on resume");
        refreshMovieData();
    }

    @Override
    public void getNextPageOnScrolled(int nextPage) {
        new MovieDataAsyncTask(nextPage).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = mMovieListAdapter.getMovieFromPosition(position);
        if (movie != null) {
            Log.i(LOG_TAG, "***** onItemClick");
            Intent activityIntent = new Intent(getActivity(), DetailsActivity.class);
            activityIntent.putExtra(MOVIE_PARAMS, movie);
            startActivity(activityIntent);
        }
    }

    private void refreshMovieData() {
        new MovieDataAsyncTask().execute();
    }

    private class MovieDataAsyncTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = MovieDataAsyncTask.class.getSimpleName();
        private final String BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
        private final String BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated";
        private final String PAGE_PARAM = "page";
        private final String API_KEY_PARAM = "api_key";
        private int page;

        MovieDataAsyncTask() {
            page = 1;
        }

        MovieDataAsyncTask(int pageNum) {
            page = pageNum;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                if (page == 1) {
                    mMovieListAdapter.swapData(movies);
                } else {
                    mMovieListAdapter.appendData(movies);
                }
            }
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            return getJsonDataFromWeatherApI();
        }

        private List<Movie> getJsonDataFromWeatherApI() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJson;

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = sharedPrefs.getString(
                    getString(R.string.pref_sorting_key),
                    getString(R.string.pref_sorting_default));

            try {
                Uri.Builder builder;
                if (sortOrder.equalsIgnoreCase(getString(R.string.sort_top_rated))) {
                    Log.i(LOG_TAG, "****** Movie results uri : " + BASE_URL_TOP_RATED);
                    builder = new Uri.Builder().encodedPath(BASE_URL_TOP_RATED)
                            .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
                } else {
                    //default to popular
                    Log.i(LOG_TAG, "****** Movie results uri : " + BASE_URL_POPULAR);
                    builder = new Uri.Builder().encodedPath(BASE_URL_POPULAR)
                            .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                            .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
                }

                Uri uri = builder.build();
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line)
                            .append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                resultJson = buffer.toString();
                Log.i(LOG_TAG, "Movie results output : json : " + resultJson);

                try {
                    return MovieDataParser.getMovieDataFromJson(resultJson);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
