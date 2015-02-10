package nz.co.stqry.library.android.location;

import android.location.Location;

import nz.co.stqry.library.android.callback.ILocationChangedListener;

/**
 * Created by Marc on 2/9/2015.
 */
public class LocationChangedData {
    private final ILocationChangedListener mListener;
    private final double mDistance;
    private Location mLocation;

    public LocationChangedData(ILocationChangedListener listener, double distance, Location location) {
        mListener = listener;
        mDistance = distance;
        mLocation = location;
    }

    public Location getLastLocation() {
        return mLocation;
    }

    public void setLastLocation(Location location) {
        mLocation = location;
    }

    public double getDistance() {
        return mDistance;
    }

    public ILocationChangedListener getCallback() {
        return mListener;
    }
}
