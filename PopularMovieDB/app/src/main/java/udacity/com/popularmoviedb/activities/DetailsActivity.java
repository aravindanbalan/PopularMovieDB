package udacity.com.popularmoviedb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.fragments.MovieDetailFragment;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;
import static udacity.com.popularmoviedb.IConstants.YES;

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
                Uri movieUri = intent.getParcelableExtra(MOVIE_PARAMS);
                if (movieUri != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MOVIE_PARAMS, movieUri);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(bundle);
                    ft.add(R.id.movie_detail_container, fragment, "MovieDetailFragment");
                    ft.commit();
                }
            }
        }
    }

//    public void setFavorite(View view) {
//        if (view != null) {
//            List<String> favoriteValues = (ArrayList<String>) view.getTag();
//            String movieId = favoriteValues.get(0);
////            String favorite = favoriteValues.get(1);
//            String movieTitle = favoriteValues.get(2);
//
//            ImageButton button = (ImageButton) view;
//            boolean isFavorite = button.isEnabled();
//
//            Uri favoriteUri = MovieContract.MovieEntry.buildFavoriteUri(Long.parseLong(movieId), isFavorite);
//            int updated = getContentResolver().update(favoriteUri, null, null, null);
//
//            // if record was successfully updated, display a toast
//            if (updated == 1) {
//                Utility.displayFavoritesMessage(isFavorite, movieTitle);
//            }
//
//            MovieDetailFragment detailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
//            if (detailFragment != null) {
//                // notify detail fragment
//                detailFragment.onFavoriteToggle();
//            }
//        }
//    }
}
