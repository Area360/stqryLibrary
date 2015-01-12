package nz.co.stqry.library.volley.callbacks;

import com.android.volley.Response;

/**
 * Created by Marc Giovannoni on 18/09/14.
 */
public abstract class LoopResponseCallback<T> implements Response.Listener<T> {

    public abstract void onResponse(T response, int id);

    @Override
    public void onResponse(T response) {
    }
}
