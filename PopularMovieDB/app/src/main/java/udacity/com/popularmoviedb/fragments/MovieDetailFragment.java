package udacity.com.popularmoviedb.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX;
import static udacity.com.popularmoviedb.IConstants.MOVIE_PARAMS;

/**
 * Created by arbalan on 8/14/16.
 */

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private LinearLayout mMovieDetailLayout;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieOverview;
    private TextView mMovieVoteAverage;
    private ImageView mMoviePoster;
    private static final int DETAILS_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mMovieDetailLayout = (LinearLayout) rootView.findViewById(R.id.movie_detail_layout);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        mMovieVoteAverage = (TextView) rootView.findViewById(R.id.movie_rating);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_image);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(getArguments() == null) return null;

        Uri movieUri = getArguments().getParcelable(MOVIE_PARAMS);
        String movieId = Utility.fetchMovieIdFromUri(getActivity(), movieUri);

        return new CursorLoader(
                getActivity(),
                movieUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        if (data != null) {
            if (!data.moveToFirst()) {
                return;
            }

            String title = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
            String poster = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
            double rating = data.getDouble(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG));
            String releaseDate = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            String overview = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS));
            final int is_favorite = data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));

            if (title != null && !TextUtils.isEmpty(title)) {
                getActivity().setTitle(title);
            }

            mMovieTitle.setText(title);
            mMovieReleaseDate.setText(releaseDate);
            mMovieOverview.setText(overview);
            String ratingLabel = getResources().getString(R.string.rating_label, Double.toString(rating));
            mMovieVoteAverage.setText(ratingLabel);

            ImageLoader loader = AppHandles.getImageLoader();
            loader.loadImage(MOVIE_DB_URL_PREFIX + poster, mMoviePoster, new Callback() {
                @Override
                public void onSuccess() {
                    if (mMovieDetailLayout != null) {
                        mMovieDetailLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError() {
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
