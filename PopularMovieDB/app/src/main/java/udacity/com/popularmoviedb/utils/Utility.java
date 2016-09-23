package udacity.com.popularmoviedb.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import udacity.com.popularmoviedb.R;

/**
 * Created by arbalan on 9/23/16.
 */

public class Utility {
    public static String getSortOrder(Context context){
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sharedPrefs.getString(
                context.getString(R.string.pref_sorting_key),
                context.getString(R.string.pref_sorting_default));
        return sortOrder;
    }
}
