package udacity.com.popularmoviedb.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.util.List;
import java.util.Vector;

import udacity.com.popularmoviedb.BuildConfig;
import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.models.Movie;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.*;
import static udacity.com.popularmoviedb.utils.DataParser.getMovieDataFromJson;
import static udacity.com.popularmoviedb.utils.Utility.makeServiceCall;

/**
 * Created by arbalan on 9/24/16.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String page = extras.getString(PAGE_QUERY_EXTRA);
        Log.i(LOG_TAG, "**** starting to sync" + page);
        List<Movie> movies = getJsonDataFromApi(page);
        bulkInsertMoviesToDB(movies);
    }

    public static void syncImmediately(Context context, String page) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(PAGE_QUERY_EXTRA, page);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }

    private List<Movie> getJsonDataFromApi(String page) {
        String resultJson;
        String sortOrder = Utility.getSortOrder(getContext());

        Uri.Builder builder;
        if (!TextUtils.isEmpty(sortOrder) && sortOrder.equalsIgnoreCase(getContext().getString(R.string.sort_top_rated))) {
            builder = new Uri.Builder().encodedPath(BASE_URL_TOP_RATED)
                    .appendQueryParameter(PAGE_PARAM, page)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
        } else {
            //default to popular
            builder = new Uri.Builder().encodedPath(BASE_URL_POPULAR)
                    .appendQueryParameter(PAGE_PARAM, page)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY);
        }

        Uri uri = builder.build();

        resultJson = makeServiceCall(uri);
        Log.i(LOG_TAG, "Movie results output : json : " + resultJson);

        try {
            return getMovieDataFromJson(resultJson);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    private void bulkInsertMoviesToDB(List<Movie> movies) {
        if (movies != null) {
            Vector<ContentValues> cVVector = new Vector<>(movies.size());

            for (Movie movie : movies) {
                if (movie != null) {

                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, movie.getPosterUrl());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movie.getMovieOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, movie.getVoteAverage());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getMovieReleaseDate());

                    cVVector.add(movieValues);

                    int inserted = 0;
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI_MOVIE, cvArray);
                    }
                }
            }
        }
    }
}
