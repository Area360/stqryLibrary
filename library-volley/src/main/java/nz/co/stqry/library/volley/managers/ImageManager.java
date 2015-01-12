package nz.co.stqry.library.volley.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import nz.co.stqry.library.volley.utils.BitmapCache;

/**
 * Created by Marc Giovannoni on 11/07/14.
 */
public class ImageManager {
    private static final String TAG = ImageManager.class.getSimpleName();

    private static ImageManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private BitmapCache mCache;
    private boolean mInitialized = false;

    public static ImageManager getInstance() {
        if (mInstance == null) {
            synchronized (ImageManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageManager();
                }
            }
        }
        return mInstance;
    }

    /*
    * @param percent Percent of memory class to use to size memory cache
    */
    public void initialize(Context context, FragmentManager fm, float percent) throws Exception {
        if (mInitialized)
            throw new Exception("Image manager already initialized!");
        mRequestQueue = Volley.newRequestQueue(context);
        mCache = BitmapCache.getInstance(fm, percent);
        mImageLoader = new ImageLoader(mRequestQueue, mCache);
        mInitialized = true;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void setDefaultHeader(Map<String, String> header) {
        mImageLoader.setDefaultHeader(header);
    }

    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void addImageRequest(String url, Response.Listener<Bitmap> imageListener, Response.ErrorListener errorListener) {
        ImageRequest getImageRequest = new ImageRequest(url, imageListener, 0, 0, null, errorListener, mImageLoader.getDefaultHeader());
        mRequestQueue.add(getImageRequest);
    }

    public void get(String url, ImageLoader.ImageListener listener) {
        mImageLoader.get(url, listener);
    }

    public void get(String url, ImageLoader.ImageListener listener, int maxWidth, int maxHeight) {
        mImageLoader.get(url, listener, maxWidth, maxHeight);
    }

    public Bitmap getBitmapFromCache(String url) {
        return mCache.getBitmap(url);
    }
}
