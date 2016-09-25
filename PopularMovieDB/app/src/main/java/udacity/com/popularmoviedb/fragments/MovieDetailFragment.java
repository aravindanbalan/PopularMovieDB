package udacity.com.popularmoviedb.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.adapters.ReviewAdapter;
import udacity.com.popularmoviedb.adapters.TrailerAdapter;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.data.ReviewAsyncTask;
import udacity.com.popularmoviedb.data.TrailerAsyncTask;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.MOVIE_DB_URL_PREFIX;
import static udacity.com.popularmoviedb.IConstants.MOVIE_ID_KEY;
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
    private String mMovieId;
    private ListView mTrailerListView;
    private ListView mReviewsListView;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private static final int DETAILS_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mMovieDetailLayout = (LinearLayout) rootView.findViewById(R.id.movie_detail_layout);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        mMovieReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        mMovieVoteAverage = (TextView) rootView.findViewById(R.id.movie_rating);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_image);
        mTrailerListView = (ListView) rootView.findViewById(R.id.list_view_trailers);
        mReviewsListView = (ListView) rootView.findViewById(R.id.list_view_reviews);


        if (savedInstanceState != null) {
            mMovieId = savedInstanceState.getString(MOVIE_ID_KEY);
        }

        mTrailerAdapter = new TrailerAdapter(getContext(), null, 0);
        mTrailerListView.setAdapter(mTrailerAdapter);

        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String youTubeKey = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE_KEY));
                    Utility.watchYoutubeVideo(getActivity(), youTubeKey);
                }
            }
        });


        //FIXME
//        mReviewAdapter = new ReviewAdapter(getContext(), null, 0);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(MOVIE_ID_KEY, mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (getArguments() == null) return null;
        Uri movieUri = getArguments().getParcelable(MOVIE_PARAMS);
        mMovieId = Utility.fetchMovieIdFromUri(getActivity(), movieUri);

        switch (id) {
            case DETAILS_LOADER:

                return new CursorLoader(
                        getActivity(),
                        movieUri,
                        null,
                        null,
                        null,
                        null
                );


            case TRAILERS_LOADER:
                Uri trailerUri = MovieContract.TrailerEntry.buildTrailerUri(Long.parseLong(mMovieId));

                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        null,
                        null,
                        null,
                        null
                );
            case REVIEWS_LOADER:
                Uri reviewUri = MovieContract.ReviewEntry.buildReviewUri(Long.parseLong(mMovieId));

                return new CursorLoader(
                        getActivity(),
                        reviewUri,
                        null,
                        null,
                        null,
                        null
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        int id = cursorLoader.getId();

        switch (id) {
            case DETAILS_LOADER:
                detailsOnLoadFinished(data);
                break;
            case TRAILERS_LOADER:
                trailersOnLoadFinished(data);
                break;
            case REVIEWS_LOADER:
                reviewsOnLoadFinished(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == TRAILERS_LOADER){
            mTrailerAdapter.swapCursor(null);
        }
    }

    private void detailsOnLoadFinished(Cursor data) {
        if (data != null) {
            if (!data.moveToFirst()) {
                return;
            }

            new TrailerAsyncTask().execute(mMovieId);
            new ReviewAsyncTask().execute(mMovieId);

            LoaderManager loaderManager = getLoaderManager();

            if (loaderManager.getLoader(TRAILERS_LOADER) != null){
                loaderManager.restartLoader(TRAILERS_LOADER, null, this);
            }

            if (loaderManager.getLoader(REVIEWS_LOADER) != null){
                loaderManager.restartLoader(REVIEWS_LOADER, null, this);
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

    private void trailersOnLoadFinished(Cursor data) {
        mTrailerAdapter.swapCursor(data);
        mTrailerListView.setVisibility(View.VISIBLE);
    }

    private void reviewsOnLoadFinished(Cursor data) {
//        if (data != null) {
//            if (!data.moveToFirst()) {
//                return;
//            }
//        }

        //TODO reviewAdapter swapcursor
    }
}
