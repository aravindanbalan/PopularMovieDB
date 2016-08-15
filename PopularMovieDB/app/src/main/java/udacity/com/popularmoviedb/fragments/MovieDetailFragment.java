package udacity.com.popularmoviedb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.models.Movie;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX;
import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;

/**
 * Created by arbalan on 8/14/16.
 */

public class MovieDetailFragment extends Fragment {
    private Movie mMovie;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieOverview;
    private TextView mMovieVoteAverage;
    private ImageView mMoviePoster;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        mMovieVoteAverage = (TextView) rootView.findViewById(R.id.movie_rating);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_image);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            mMovie = getArguments().getParcelable(MOVIE_PARAMS);
        }
        if (mMovie != null && !TextUtils.isEmpty(mMovie.getTitle())) {
            getActivity().setTitle(mMovie.getTitle());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovieDetails();
    }

    private void updateMovieDetails() {
        if (mMovie != null) {
            mMovieTitle.setText(mMovie.getTitle());
            mMovieReleaseDate.setText(mMovie.getMovieReleaseDate());
            mMovieOverview.setText(mMovie.getMovieOverview());
            String rating = getResources().getString(R.string.rating_label, Double.toString(mMovie.getVoteAverage()));
            mMovieVoteAverage.setText(rating);

            int width = getContext().getResources().getDisplayMetrics().widthPixels;
            int height = getContext().getResources().getDisplayMetrics().heightPixels;
            Picasso.with(getContext()).load(MOVIE_DB_URL_PREFIX + mMovie.getPosterUrl()).centerCrop().resize(width / 2, height / 2).into(mMoviePoster);
        }
    }
}
