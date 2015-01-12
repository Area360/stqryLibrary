package nz.co.stqry.library.volley.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nz.co.stqry.library.volley.constants.HttpRequestConstants;

/**
 * Created by Marc Giovannoni on 23/07/14.
 */
public class HttpGetParams {
    protected String mBaseUrl;
    protected Map<String, String> mParameters;

    public HttpGetParams(String baseUrl) {
        mParameters = new HashMap<String, String>();
        mBaseUrl = baseUrl;
    }

    public void	addParameters(String fieldName, String fieldValue) {
        mParameters.put(fieldName, fieldValue);
    }

    public String toUrl() {
        String url = mBaseUrl;

        Iterator it = mParameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();

            url += pairs.getKey() + "=" + pairs.getValue() + HttpRequestConstants.PARAMS_SEPARATOR_GET;
        }
        return url;
    }
}
