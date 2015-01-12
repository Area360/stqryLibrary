package nz.co.stqry.library.volley.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc Giovannoni on 30/07/14.
 */
public class PostRequest extends Request<String> {

    private Response.Listener<String> mListener = null;
    private HashMap<String, String> mHeader;
    private HttpPostParams mParams;
    private Map<String, String> mResponseHeader;

    public PostRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, HttpPostParams params) {
        super(Method.POST, url, errorListener);
        mListener = listener;

        mHeader = new HashMap<String, String>();
        mParams = params;
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams.getMap();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);

        return Response.success(jsonString, getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        if (mListener != null)
            mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }
}
