package nz.co.stqry.library.android.sensor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

/**
 * Created by Marc Giovannoni on 5/11/14.
 */
public class NativeStepDetector implements IStepDetector, SensorEventListener {

    private static final String TAG = NativeStepDetector.class.getSimpleName();

    private final Context mContext;
    private StepDetectorListener mListener;

    public NativeStepDetector(Context context) {
        mContext = context;
    }

    public static boolean isSupported(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
           return false;
        else {
            PackageManager packageManager = context.getPackageManager();

            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))
                return false;
        }
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initialize() {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null)
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void registerListener(StepDetectorListener listener) {
        mListener = listener;
    }

    @Override
    public void unregisterListener(StepDetectorListener listener) {
        mListener = null;
    }

    @Override
    public void stop() {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        Sensor sensor = sensorEvent.sensor;
        int value = -1;

        if (values.length > 0)
            value = (int) values[0];

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (mListener != null) {
                if (value != -1)
                    mListener.onStep();
                else
                    mListener.onStopMoving();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
