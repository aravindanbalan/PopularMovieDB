package udacity.com.popularmoviedb;

import android.app.Application;
import android.content.Context;

import udacity.com.popularmoviedb.utils.AppHandles;

/**
 * Created by arbalan on 9/23/16.
 */

public class PopularMovieApplication extends Application{
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

    }

    public static Context getContext() {
        return sContext;
    }
}
