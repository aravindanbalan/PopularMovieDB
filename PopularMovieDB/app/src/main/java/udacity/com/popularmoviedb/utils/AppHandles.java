package udacity.com.popularmoviedb.utils;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import udacity.com.popularmoviedb.PopularMovieApplication;

import static java.security.AccessController.getContext;

/**
 * Created by arbalan on 9/23/16.
 */

public class AppHandles {
    private static AppHandles sInstance;
    private static Context sApplicationContext;
    private ImageLoader mImageLoader;
    private OkHttpClient mOkHttpClient;

    public static AppHandles getInstance() {
        synchronized (AppHandles.class) {
            if (sInstance == null) {
                sInstance = new AppHandles();
            }
        }
        return sInstance;
    }

    private AppHandles() {
        sApplicationContext = PopularMovieApplication.getContext();
    }

    public static ImageLoader getImageLoader() {
        AppHandles appHandles = AppHandles.getInstance();
        synchronized (ImageLoader.class) {
            if (appHandles.mImageLoader == null) {
                appHandles.mImageLoader = new ImageLoader(sApplicationContext, getOkHttpClient());
            }
        }
        return appHandles.mImageLoader;
    }

    public static OkHttpClient getOkHttpClient() {
        AppHandles appHandles = AppHandles.getInstance();

        synchronized (ImageLoader.class) {
            if (null == appHandles.mOkHttpClient) {
                appHandles.mOkHttpClient = createOkHttpClient();
            }
        }
        return appHandles.mOkHttpClient;
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            TrustManager[] managers = new TrustManager[] {};
            sslContext.init(new KeyManager[0], managers, new SecureRandom());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
        okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        return okHttpClient;
    }
}
