package udacity.com.popularmoviedb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.fragments.HomeFragment;
import udacity.com.popularmoviedb.fragments.MovieDetailFragment;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;
import static udacity.com.popularmoviedb.PopularMovieApplication.getContext;

public class HomeActivity extends AppCompatActivity implements HomeFragment.Callback {
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "DFTAG";
    private boolean mIsTablet;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mIsTablet = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mIsTablet = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortOrder(getContext());

        //avoid extra call to the service on resume if the sort order hasn't changed
        if (!TextUtils.isEmpty(sortOrder) && !sortOrder.equalsIgnoreCase(mSortOrder)) {
            HomeFragment ff = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_home);
            if (null != ff) {
                ff.onSettingChanged();
            }

            if(mIsTablet) {
                MovieDetailFragment df = (MovieDetailFragment) getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
                if (null != df) {
                    df.onSettingChanged();
                }
            }
            mSortOrder = sortOrder;
        }
    }

    @Override
    public void onItemSelected(Uri uri) {
        if (mIsTablet) {
            Bundle args = new Bundle();
            args.putParcelable(MOVIE_PARAMS, uri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent activityIntent = new Intent(this, DetailsActivity.class);
            activityIntent.putExtra(MOVIE_PARAMS, uri);
            startActivity(activityIntent);
        }
    }
}
