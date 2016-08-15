package udacity.com.popularmoviedb.utils;

/**
 * Created by arbalan on 9/23/16.
 */

import android.content.Context;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class ImageLoader {

    private final Picasso mPicasso;
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    protected LruCache mCache;

    public ImageLoader(Context context, OkHttpClient client) {
        mPicasso = initPicasso(context, client);
    }

    protected Picasso initPicasso(Context context, OkHttpClient okHttpClient) {
        Picasso.Builder builder = new Picasso.Builder(context);

        OkHttpDownloader loader = new OkHttpDownloader(okHttpClient);
        builder.downloader(loader);

        int maxSize = MAX_DISK_CACHE_SIZE;

        mCache = new LruCache(maxSize);
        builder.memoryCache(mCache);

        Picasso picasso = builder.build();
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("ImageLoader should be used for loading images instead of using Picasso directly", e);
        }
        return picasso;
    }

    public void loadImage(String url, ImageView imageView) {
        mPicasso.load(url).into(imageView);
    }

    public void loadImage(String url, ImageView imageView, int placeholderResId) {
        mPicasso.load(url).placeholder(placeholderResId).into(imageView);
    }

    public void loadImage(String url, ImageView imageView, int width, int height) {
        mPicasso.load(url).resize(width, height).into(imageView);
    }

    public void loadImage(String url, ImageView imageView, final Callback callback) {
        if (callback == null) {
            throw new IllegalStateException("ImageLoader callback should not be null");
        }
        mPicasso.load(url).into(imageView, callback);
    }
}
