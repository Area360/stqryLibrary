package nz.co.stqry.library.volley.network;

import java.util.HashMap;
import java.util.Map;

public class HttpPostParams {

	protected Map<String, String> mParameters;
	
	public HttpPostParams() {
		mParameters = new HashMap<String, String>();
	}
	
	public void	addParameters(String fieldName, String fieldValue) {
		mParameters.put(fieldName, fieldValue);
	}

    public Map<String, String> getMap() {
        return mParameters;
    }
}
