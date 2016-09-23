package udacity.com.popularmoviedb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.fragments.MovieDetailFragment;
import udacity.com.popularmoviedb.models.Movie;

import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;

/**
 * Created by arbalan on 8/14/16.
 */

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent != null) {
                Movie movie = intent.getParcelableExtra(MOVIE_PARAMS);
                if (movie != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MOVIE_PARAMS, movie);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(bundle);
                    ft.add(R.id.movie_detail_container, fragment, "MovieDetailFragment");
                    ft.commit();
                }
            }
        }
    }
}
