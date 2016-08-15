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

public class TrailerAdapter extends CursorAdapter {

    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_trailer_layout, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read trailer from cursor
        TextView trailerName = (TextView) view.findViewById(R.id.trailer_name);
        String trailerString = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME));
        Log.i(LOG_TAG, "***** trialer name : " + trailerString);
        trailerName.setText(trailerString);
    }

}
