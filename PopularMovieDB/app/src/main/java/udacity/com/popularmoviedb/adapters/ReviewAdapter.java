package udacity.com.popularmoviedb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.data.MovieContract;

/**
 * Created by arbalan on 9/25/16.
 */

public class ReviewAdapter extends CursorAdapter {

    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_review_layout, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read trailer from cursor
        TextView authorName = (TextView) view.findViewById(R.id.list_item_review_author);
        TextView contentView = (TextView) view.findViewById(R.id.list_item_review_content);
        String author = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR_NAME));
        String content = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT));
        Log.i(LOG_TAG, "***** author name : " + author);
        Log.i(LOG_TAG, "***** conetnt : " + content);
        authorName.setText(author);
        contentView.setText(content);
    }

}
