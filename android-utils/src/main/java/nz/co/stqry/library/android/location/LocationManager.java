package nz.co.stqry.library.android.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedHashSet;
import java.util.Set;

import nz.co.stqry.library.android.callback.IChangedListener;


public class LocationManager implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	protected static final String TAG = "LocationManager";
	
	private static volatile LocationManager mInstance = null;

	private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
	private double mRetrievedLatitude = -1;
	private double mRetrievedLongitude = -1;
    
	/*
	 * Constants for location update parameters
	 */
	public static final int MILLISECONDS_PER_SECOND = 1000;

	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

	// Update interval in milliseconds
	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

	// A fast ceiling of update intervals, used when the app is visible
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private Dialog errorDialog;
	private boolean hasGooglePlayServices = true;
	private SensorManager mSensorManager;
	private final Set<IChangedListener> mListeners;
    private final float[] mRotationMatrix;
    private final float[] mRemapRorationMatrix;
    private final float[] mOrientation;

    private boolean mTracking = false;
    private float mHeading;
    private float mPitch;
    private GeomagneticField mGeomagneticField;
    private boolean mHasInterference;
	private double mAltitude;
	private long mTime;
    private Context mContext;
    private float mAccuracy;
    private float mSpeed;

    private SensorEventListener mSensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mHasInterference = (accuracy < SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                notifyAccuracyChanged();
                //Log.d(TAG, "has interference: " + mHasInterference);
            }
            if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                if (accuracy != SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
                    notifyAccuracyChanged();
                //Log.d(TAG, "has interference: " + mHasInterference);
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // Get the current heading from the sensor, then notify the listeners of the
                // change.
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRemapRorationMatrix);
                SensorManager.getOrientation(mRemapRorationMatrix, mOrientation);

                // Store the pitch (used to display a message indicating that the user's head
                // angle is too steep to produce reliable results.
                mPitch = (float) Math.toDegrees(mOrientation[1]);

                // Convert the heading (which is relative to magnetic north) to one that is
                // relative to true north, using the user's current location to compute this.
                float magneticHeading = (float) Math.toDegrees(mOrientation[0]);
                float a = computeTrueNorth(magneticHeading) - 90;
                float b = 360.0f;

                mHeading = (a % b + b) % b;

                notifyOrientationChanged();
            }
        }
    };
    private LocationEnabledCallBack mConnectionCallback;
    private boolean mInitialized = false;

    public LocationManager() {
		mRotationMatrix = new float[16];
		mRemapRorationMatrix = new float[16];
        mOrientation = new float[9];
        mListeners = new LinkedHashSet<>();
	}
	
	public static LocationManager getInstance() {
		if (mInstance == null) {
			synchronized (LocationManager.class) {
				if (mInstance == null) {
					mInstance = new LocationManager();
				}
			}
		}
		return mInstance;
	}

	public void initialize(Context context, LocationEnabledCallBack callback) throws Exception {

        if (mInitialized)
            return;

        mContext = context;
        mConnectionCallback = callback;

        if (!isLocationEnabled())
            buildAlertMessageNoGps(mContext);
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest.setFastestInterval(10000);

		/*
		 * Create a new location client, using the enclosing class to
		 * handle callbacks.
		 */
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        mInitialized = true;
	}

    public boolean isLocationEnabled() {
        android.location.LocationManager manager = (android.location.LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        mConnectionCallback.onConnectionFailure();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void startRotationSensor(Context context) {
        if (mTracking)
            return;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        initSensorManager();
    }

    public void stopRotationSensor() {
        mSensorManager.unregisterListener(mSensorListener);
    }

	private void initSensorManager() {
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);

        // The rotation vector sensor doesn't give us accuracy updates, so we observe the
        // magnetic field sensor solely for those.
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        mTracking = true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult((Activity)mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			// If no resolution is available, display a dialog to the user with the error.
			hasGooglePlayServices = false;
			showErrorDialog(connectionResult.getErrorCode());
		}
	}



    @Override
    public void onConnected(Bundle bundle) {
        hasGooglePlayServices = true;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mConnectionCallback.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location location) {
		if (location != null) {
			mRetrievedLatitude = location.getLatitude();
			mRetrievedLongitude = location.getLongitude();
            mAccuracy = location.getAccuracy();
			mAltitude = location.getAltitude();
			mTime = location.getTime();
            mSpeed = location.getSpeed();
            updateGeomagneticField();
		}
    }
	/**
     * Removes a listener from the list of those that will be notified when the user's location or
     * orientation changes.
     */
    public void removeOnChangedListener(IChangedListener listener) {
        mListeners.remove(listener);
    }


	public void stop(){
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
	}

	public double getLatitude() {
		return mRetrievedLatitude;
	}

	public double getLongitude() {
		return mRetrievedLongitude;
	}

    public double getAccuracy() {
        return mAccuracy;
    }

    public double getSpeed() {
        return mSpeed;
    }

	/**
	 * Show a dialog returned by Google Play services for the
	 * connection error code
	 *
	 * @param errorCode An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		if (errorDialog == null) {
			// Get the error dialog from Google Play services
			errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, (Activity)mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {

				errorDialog.setOnKeyListener(new OnKeyListener(){
					@Override
					public boolean onKey ( DialogInterface dialog , int keyCode , KeyEvent event ){
						if (keyCode == KeyEvent.KEYCODE_BACK){
							errorDialog.dismiss();
							errorDialog = null;
//                            mActivity.finish();
							return true;
						}
						return false;
					}
				});
				errorDialog.show();

				// Create a new DialogFragment in which to show the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();

				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				errorFragment.setCancelable(true);
				// Show the error dialog in the DialogFragment
				errorFragment.show(((Activity)mContext).getFragmentManager(), "Kea");
			}
		}
	}

    public String getGeoPosition() {
        return mRetrievedLatitude + ";" + mRetrievedLongitude + ";" + mAltitude + " epu=" + mAccuracy + " hdn=" + mHeading + " spd=" + mSpeed;
    }

    /**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 *
		 * @param dialog An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	public boolean hasGooglePlayServices(){
		return hasGooglePlayServices;
	}

	public boolean hasInterference() {
        return mHasInterference;
    }
	
	/**
     * Gets the user's current heading, in degrees. The result is guaranteed to be between 0 and
     * 360.
     *
     * @return the user's current heading, in degrees
     */
    public float getHeading() {
        return mHeading;
    }
	
	/**
     * Gets the user's current pitch (head tilt angle), in degrees. The result is guaranteed to be
     * between -90 and 90.
     *
     * @return the user's current pitch angle, in degrees
     */
    public float getPitch() {
        return mPitch;
    }
    
    /**
     * Updates the cached instance of the geomagnetic field after a location change.
     */
    private void updateGeomagneticField() {
        mGeomagneticField = new GeomagneticField((float) mRetrievedLatitude, (float) mRetrievedLongitude, (float)mAltitude, mTime);
    }

    /**
         * Use the magnetic field to compute true (geographic) north from the specified heading
         * relative to magnetic north.
         *
         * @param heading the heading (in degrees) relative to magnetic north
         * @return the heading (in degrees) relative to true north
         */
    private float computeTrueNorth(float heading) {
        if (mGeomagneticField != null) {
            return heading + mGeomagneticField.getDeclination();
        } else {
            return heading;
        }
    }
    
    /**
     * Notifies all listeners that the compass's accuracy has changed.
     */
    private void notifyAccuracyChanged() {
        for (IChangedListener listener : mListeners) {
            listener.onAccuracyChanged(this);
        }
    }
    
    /**
     * Notifies all listeners that the user's orientation has changed.
     */
    private void notifyOrientationChanged() {
        for (IChangedListener listener : mListeners) {
            listener.onOrientationChanged(this);
        }
    }
    
    /**
     * Adds a listener that will be notified when the user's location or orientation changes.
     */
    public void addOnChangedListener(IChangedListener listener) {
        mListeners.add(listener);
    }

    public interface LocationEnabledCallBack {
        void onConnectionFailure();
        void onConnected();
    }
}
