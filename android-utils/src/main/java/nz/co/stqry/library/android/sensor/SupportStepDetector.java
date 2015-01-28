package nz.co.stqry.library.android.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Marc Giovannoni on 5/11/14.
 */
public class SupportStepDetector implements IStepDetector, SensorEventListener {

    private static final String TAG = SupportStepDetector.class.getSimpleName();

    private static final float ZERO_G_VALUE = 8192;
    private static final float THRESHOLD = .5f;

    private final Context mContext;

    private StepDetectorListener mListener;
    private int mSteps;
    private Timer mTimer;
    private float mPreviousY;
    private long mStartTime;

    public SupportStepDetector(Context context) {
        mContext = context;
    }

    public static boolean isSupported() {
        return true;
    }

    @Override
    public void initialize() {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null)
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

        mTimer = new Timer();
        mTimer.schedule(new StepTask(), 2000, 5000);
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
        mTimer.cancel();
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometerSensor != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float y = sensorEvent.values[1];

            if (Math.abs(y - mPreviousY) > THRESHOLD) {
                if (mSteps == 1)
                    mStartTime = System.currentTimeMillis();
                if (mSteps == 2 && System.currentTimeMillis() - mStartTime < 1000) {
                    mSteps = 0;
                    if (mListener != null)
                        mListener.onStep();
                    mTimer.cancel();
                    mTimer = new Timer();
                    mTimer.schedule(new StepTask(), 2000, 5000);
                }
                ++mSteps;
            }
            mPreviousY = y;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class StepTask extends TimerTask {
        @Override
        public void run() {
            mSteps = 0;
            if (mListener != null)
                mListener.onStopMoving();
        }
    }
}
