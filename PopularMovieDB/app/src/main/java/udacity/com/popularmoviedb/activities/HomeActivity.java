package udacity.com.popularmoviedb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.fragments.HomeFragment;
import udacity.com.popularmoviedb.fragments.MovieDetailFragment;
import udacity.com.popularmoviedb.models.Movie;

import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;

public class HomeActivity extends AppCompatActivity implements HomeFragment.Callback {
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "DFTAG";
    private boolean mIsTablet;

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
