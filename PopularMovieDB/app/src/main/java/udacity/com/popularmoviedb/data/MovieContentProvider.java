package udacity.com.popularmoviedb.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static udacity.com.popularmoviedb.IConstants.FAV_STATUS;

/**
 * Created by arbalan on 9/23/16.
 */

public class MovieContentProvider extends ContentProvider {
    private final String LOG_TAG = MovieContentProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITE = 102;
    static final int FAVORITE_WITH_ID_STATUS = 103;
    static final int TRAILER = 104;
    static final int TRAILER_WITH_ID = 105;
    static final int REVIEW = 106;
    static final int REVIEW_WITH_ID = 107;

    private MovieDBHelper mMovieDBHelper;

    @Override
    public boolean onCreate() {
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;
        long _id;
        switch (match) {
            case MOVIE:
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIE_WITH_ID:
                _id = MovieContract.MovieEntry.getIdFromUri(uri);
                Log.i(LOG_TAG, "**** movie with id : " + _id);
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + " = ?",
                        new String[] { Long.toString(_id) },
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE:
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_FAVORITE + " = 1",
                        null,
                        null,
                        null,
                        sortOrder);
                break;

            //TODO to be moved to update
//            case FAVORITE_WITH_ID_STATUS:
//                _id = MovieContract.MovieEntry.getIdFromUri(uri);
//                cursor = db.query(
//                        MovieContract.MovieEntry.TABLE_NAME,
//                        projection,
//                        MovieContract.MovieEntry._ID + " = ? AND " + MovieContract.MovieEntry.COLUMN_FAVORITE + " = 1",
//                        new String[] { Long.toString(_id) },
//                        null,
//                        null,
//                        sortOrder);
//                break;

            case TRAILER:
                cursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TRAILER_WITH_ID: {
                _id = MovieContract.TrailerEntry.getIdFromUri(uri);
                cursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID + " = ?",
                        new String[] { Long.toString(_id) },
                        null,
                        null,
                        sortOrder);
                break;
            }

            case REVIEW:
                cursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEW_WITH_ID: {
                _id = MovieContract.ReviewEntry.getIdFromUri(uri);
                cursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID + " = ?",
                        new String[] { Long.toString(_id) },
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE_MOVIE;

            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE_MOVIE;

            case FAVORITE:
                return MovieContract.MovieEntry.CONTENT_TYPE_FAVORITE;

            case FAVORITE_WITH_ID_STATUS:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE_FAVORITE;

            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE_TRAILER;

            case TRAILER_WITH_ID:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE_TRAILER;

            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE_REVIEW;

            case REVIEW_WITH_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE_REVIEW;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri insertionUri;

        long insertedId;

        switch (match) {
            case MOVIE:
                insertedId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertionUri = MovieContract.MovieEntry.buildMovieUri(insertedId);
                } else {
                    throw new SQLException("********** Failed to insert row into " + uri);
                }
                break;

            case TRAILER:
                insertedId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertionUri = MovieContract.TrailerEntry.buildTrailerUri(insertedId);
                } else {
                    throw new SQLException("********** Failed to insert row into " + uri);
                }
                break;

            case REVIEW:
                insertedId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertionUri = MovieContract.ReviewEntry.buildReviewUri(insertedId);
                } else {
                    throw new SQLException("********** Failed to insert row into " + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException("********* Unknown uri " + uri);
        }

        if (getContext() != null) {
            ContentResolver resolver = getContext().getContentResolver();
            if (resolver != null) {
                resolver.notifyChange(uri, null);
            }
        }

        return insertionUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //delete all rows and return the number of records deleted
        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            ContentResolver resolver = getContext().getContentResolver();
            if (resolver != null) {
                resolver.notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Log.i(LOG_TAG, "********** uri : "+ uri.toString());
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case FAVORITE_WITH_ID_STATUS:
                String movieId = getMovieIdFromFavoriteUri(uri);
                String favoriteFlag = getFavoriteStatusFromFavoriteUri(uri);
                int favStatus = favoriteFlag.equalsIgnoreCase("true") ? 1 : 0;

                selectionArgs = new String[]{movieId};
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favStatus);

                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs);
                Log.i(LOG_TAG, "************ Setting as favorite : " + movieId + " status : " + favoriteFlag + " updated : " + rowsUpdated);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null) {
            ContentResolver resolver = getContext().getContentResolver();
            if (resolver != null) {
                resolver.notifyChange(uri, null);
            }
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int matcher = sUriMatcher.match(uri);

        if (values == null || values.length == 0) {
            return 0;
        }

        switch (matcher) {
            case MOVIE: {
                db.beginTransaction();
                int count = 0;

                for (ContentValues item : values) {
                    long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, item);
                    if (_id != -1) {
                        count++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();

                if (getContext() != null) {
                    ContentResolver resolver = getContext().getContentResolver();
                    if (resolver != null) {
                        resolver.notifyChange(uri, null);
                    }
                }
                Log.i(LOG_TAG, "*********** bulk inserted : " + count);
                return count;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#/*", FAVORITE_WITH_ID_STATUS);

        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_ID);

        return uriMatcher;
    }

    private static String getMovieIdFromFavoriteUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    private static String getFavoriteStatusFromFavoriteUri(Uri uri) {
        return uri.getPathSegments().get(2);
    }

}
