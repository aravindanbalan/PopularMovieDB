package udacity.com.popularmoviedb.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.adapters.ReviewAdapter;
import udacity.com.popularmoviedb.adapters.TrailerAdapter;
import udacity.com.popularmoviedb.data.MovieContract;
import udacity.com.popularmoviedb.data.ReviewAsyncTask;
import udacity.com.popularmoviedb.data.TrailerAsyncTask;
import udacity.com.popularmoviedb.models.Review;
import udacity.com.popularmoviedb.models.Trailer;
import udacity.com.popularmoviedb.utils.AppHandles;
import udacity.com.popularmoviedb.utils.ImageLoader;
import udacity.com.popularmoviedb.utils.Utility;

import static udacity.com.popularmoviedb.IConstants.*;

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
    private LinearLayout mTrailerListView;
    private LinearLayout mReviewsListView;
    private List<Trailer> mTrailers;
    private List<Review> mReviews;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private ToggleButton mFavorite;
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
        mTrailerListView = (LinearLayout) rootView.findViewById(R.id.list_view_trailers);
        mReviewsListView = (LinearLayout) rootView.findViewById(R.id.list_view_reviews);
        mFavorite = (ToggleButton) rootView.findViewById(R.id.fav_icon);

        mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(LOG_TAG, "********** toggle button : "+ isChecked);
                setFavoriteState(isChecked);
                updateDBWithStatus(isChecked);
            }
        });

        mTrailerAdapter = new TrailerAdapter(getContext(), null, 0);
        mReviewAdapter = new ReviewAdapter(getContext(), null, 0);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);

        if (savedInstanceState != null) {
            mMovieId = savedInstanceState.getString(MOVIE_ID_KEY);
            mReviews = savedInstanceState.getParcelableArrayList(REVIEW_KEY);
            mTrailers = savedInstanceState.getParcelableArrayList(TRAILER_KEY);

            restoreTrailerViewOnScreenRotation();
            restoreRestoreViewOnoScreenRotation();
        } else {
            mTrailers = new ArrayList<>();
            mReviews = new ArrayList<>();
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void onSettingChanged() {
        mReviews = new ArrayList<>();
        mTrailers = new ArrayList<>();
        mMovieDetailLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(MOVIE_ID_KEY, mMovieId);
        outState.putParcelableArrayList(TRAILER_KEY, new ArrayList<>(mTrailers));
        outState.putParcelableArrayList(REVIEW_KEY, new ArrayList<>(mReviews));
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
        if (loader.getId() == TRAILERS_LOADER) {
            mTrailerAdapter.swapCursor(null);
        } else if (loader.getId() == REVIEWS_LOADER) {
            mReviewAdapter.swapCursor(null);
        }
    }


    private void setFavoriteState(boolean status) {
        if (status) {
            mFavorite.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_on));
        } else {
            mFavorite.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star_big_off));
        }
    }

    public void updateDBWithStatus(boolean isChecked) {

        if (mFavorite != null) {
            Uri favoriteUri = MovieContract.MovieEntry.buildFavoriteUri(Long.parseLong(mMovieId), isChecked);
            int updated = getContext().getContentResolver().update(favoriteUri, null, null, null);

            // if record was successfully updated, display a toast
            if (updated == 1) {
//                Utility.displayFavoritesMessage(isChecked, mMovieTitle.getText().toString());
            }

            getLoaderManager().restartLoader(DETAILS_LOADER, null, this);
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

            if (loaderManager.getLoader(TRAILERS_LOADER) != null) {
                loaderManager.restartLoader(TRAILERS_LOADER, null, this);
            }

            if (loaderManager.getLoader(REVIEWS_LOADER) != null) {
                loaderManager.restartLoader(REVIEWS_LOADER, null, this);
            }

            String title = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
            String poster = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
            double rating = data.getDouble(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG));
            String releaseDate = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            String overview = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS));
            final int is_favorite = data.getInt(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));
            Log.i(LOG_TAG, " **** is favoarite : " + is_favorite);

            if (title != null && !TextUtils.isEmpty(title)) {
                getActivity().setTitle(title);
            }

            mMovieTitle.setText(title);
            mMovieReleaseDate.setText(releaseDate);
            mMovieOverview.setText(overview);
            String ratingLabel = getResources().getString(R.string.rating_label, Double.toString(rating));
            mMovieVoteAverage.setText(ratingLabel);
            setFavoriteState(is_favorite == 1);
            mFavorite.setChecked(is_favorite == 1);

            // gather values for saving movie to favorites
            ArrayList<String> favoriteValues = new ArrayList<>();
            favoriteValues.add(mMovieId);
            favoriteValues.add(title);
            mFavorite.setTag(favoriteValues);

            ImageLoader loader = AppHandles.getImageLoader();
            loader.loadImage(MOVIE_DB_URL_PREFIX_92 + poster, mMoviePoster, new Callback() {
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
        Cursor cursor = mTrailerAdapter.getCursor();
        addTrailerView(cursor);
    }

    private void reviewsOnLoadFinished(Cursor data) {
        mReviewAdapter.swapCursor(data);
        Cursor cursor = mReviewAdapter.getCursor();
        addReviewView(cursor);
    }


    //Dynamically add a trailer to the details page
    private void addTrailerView(final Cursor cursor) {

        if (cursor.getCount() != 0) {
            View trailerView;
            //Add the view only if the view is not already present
            while (cursor.moveToNext() && mTrailerListView.findViewById(cursor.getPosition()) == null) {
                // Read trailer from cursor
                trailerView = getActivity().getLayoutInflater().inflate(R.layout.item_trailer_layout, null);

                TextView trailerName = (TextView) trailerView.findViewById(R.id.trailer_name);
                String trailerString = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME));
                trailerName.setText(trailerString);
                trailerView.setId(cursor.getPosition());


                final String youTubeKey = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_YOUTUBE_KEY));

                Trailer trailer = new Trailer();
                trailer.setName(trailerString);
                trailer.setYoutubeKey(youTubeKey);

                if (mTrailers != null) {
                    mTrailers.add(trailer);
                }

                trailerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utility.watchYoutubeVideo(getActivity(), youTubeKey);
                    }
                });

                mTrailerListView.addView(trailerView);
            }
        }
    }

    //Dynamically add a review to the details page
    private void addReviewView(final Cursor cursor) {
        if (cursor.getCount() != 0) {
            View reviewView;
            while (cursor.moveToNext() && mReviewsListView.findViewById(cursor.getPosition()) == null) {
                // Read trailer from cursor
                reviewView = getActivity().getLayoutInflater().inflate(R.layout.item_review_layout, null);

                // Read review from cursor
                TextView authorName = (TextView) reviewView.findViewById(R.id.list_item_review_author);
                TextView contentView = (TextView) reviewView.findViewById(R.id.list_item_review_content);
                String author = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR_NAME));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT));

                String formatted_author_name = getResources().getString(R.string.review_by_text,author);

                authorName.setText(formatted_author_name);
                contentView.setText(content);
                reviewView.setId(cursor.getPosition());

                Review review = new Review();
                review.setReviewAuthor(author);
                review.setReviewContent(content);

                if (mReviews != null) {
                    mReviews.add(review);
                }

                mReviewsListView.addView(reviewView);
            }
        }
    }

    private void restoreTrailerViewOnScreenRotation() {
        View trailerView;
        for (Trailer trailer : mTrailers) {
            trailerView = getActivity().getLayoutInflater().inflate(R.layout.item_trailer_layout, null);

            TextView trailerName = (TextView) trailerView.findViewById(R.id.trailer_name);
            String trailerString = trailer.getName();
            trailerName.setText(trailerString);
            trailerView.setId(mTrailers.indexOf(trailer));

            final String youTubeKey = trailer.getYoutubeKey();

            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.watchYoutubeVideo(getActivity(), youTubeKey);
                }
            });
            mTrailerListView.addView(trailerView);
        }
    }

    private void restoreRestoreViewOnoScreenRotation() {
        View reviewView;
        for (Review review : mReviews) {
            reviewView = getActivity().getLayoutInflater().inflate(R.layout.item_review_layout, null);

            // Read review from cursor
            TextView authorName = (TextView) reviewView.findViewById(R.id.list_item_review_author);
            TextView contentView = (TextView) reviewView.findViewById(R.id.list_item_review_content);
            String author = review.getReviewAuthor();
            String content = review.getReviewContent();
            String formatted_author_name = getResources().getString(R.string.review_by_text,author);

            authorName.setText(formatted_author_name);
            contentView.setText(content);
            reviewView.setId(mReviews.indexOf(review));

            mReviewsListView.addView(reviewView);
        }
    }
}
