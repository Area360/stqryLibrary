package nz.co.stqry.library.volley.managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Marc Giovannoni on 11/07/14.
 */
public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();

    private static RequestManager mInstance;
    private RequestQueue mRequestQueue;
    private DefaultRetryPolicy mDefaultRetryPolicy;
    private boolean mInitialized = false;

    public static RequestManager getInstance() {
        if (mInstance == null) {
            synchronized (RequestManager.class) {
                if (mInstance == null) {
                    mInstance = new RequestManager();
                }
            }
        }
        return mInstance;
    }

    public void initialize(Context context) throws Exception {
        if (mInitialized)
            throw new Exception("RequestManager manager already initialized!");
        mRequestQueue = Volley.newRequestQueue(context);
        mDefaultRetryPolicy = new DefaultRetryPolicy(5000, 0, 1.0f);
        mInitialized = true;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag, DefaultRetryPolicy defaultRetryPolicy) {

        req.setTag(tag);
        if (defaultRetryPolicy == null)
            req.setRetryPolicy(mDefaultRetryPolicy);
        Log.d(TAG, "Adding request to queue: " + req.getUrl());
        mRequestQueue.add(req);
    }

    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }
}
