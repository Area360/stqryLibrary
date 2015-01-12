package nz.co.stqry.library.android.callback;

import nz.co.stqry.library.android.location.LocationManager;

public interface IChangedListener {

	/**
     * Called when the user's orientation changes.
     *
     * @param LocationManager the location manager that detected the change
     */
    void onOrientationChanged(LocationManager orientationManager);

    /**
     * Called when the accuracy of the compass changes.
     *
     * @param LocationManager the location manager that detected the change
     */
    void onAccuracyChanged(LocationManager orientationManager);
}
