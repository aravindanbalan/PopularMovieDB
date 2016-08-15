package udacity.com.popularmoviedb.utils;

/**
 * Created by arbalan on 9/23/16.
 */

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.widget.ImageView;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageLoader {

    private final Picasso mPicasso;
    private static final String PICASSO_CACHE = "picasso-cache";
    private static final int MEMORY_CACHE_SIZE = 25;
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    protected LruCache mCache;

    public ImageLoader(Context context, OkHttpClient client) {
        mPicasso = initPicasso(context, client);
    }

    protected Picasso initPicasso(Context context, OkHttpClient okHttpClient) {
        Picasso.Builder builder = new Picasso.Builder(context);

        setCache(context, okHttpClient);

        OkHttpDownloader loader = new OkHttpDownloader(okHttpClient);
        builder.downloader(loader);

        mCache = new LruCache(context);
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


    private static void setCache(Context context, OkHttpClient okHttpClient) {
        File cacheDir = createDefaultCacheDir(context);
        long maxSize = calculateDiskCacheSize(cacheDir);

        try {
            okHttpClient.setCache(new Cache(cacheDir, maxSize));
        } catch (Exception ignored) {
        }
    }

    private static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                available = statFs.getTotalBytes();
            } else {
                available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            }
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }
}
