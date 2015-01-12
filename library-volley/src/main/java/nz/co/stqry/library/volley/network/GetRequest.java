package nz.co.stqry.library.volley.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc Giovannoni on 23/07/14.
 */
public class GetRequest extends Request<String> {

    protected Response.Listener<String> mListener;
    protected HashMap<String, String> mHeader;

    public GetRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) throws UnsupportedEncodingException {
        super(Method.GET, url, errorListener);
        mListener = listener;
        mHeader = new HashMap<String, String>();
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
