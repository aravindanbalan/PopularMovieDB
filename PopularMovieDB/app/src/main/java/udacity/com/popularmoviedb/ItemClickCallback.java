package udacity.com.popularmoviedb;

import android.net.Uri;

/**
 * Created by arbalan on 9/26/16.
 */

public interface ItemClickCallback {
    /**
     * DetailFragmentCallback for when an item has been selected.
     */
    void onItemSelected(Uri movieUri);
}
