package nz.co.stqry.library.android.sensor;

import android.content.Context;

/**
 * Created by Marc Giovannoni on 5/11/14.
 */
public class StepManager {

    private static final String TAG = StepManager.class.getSimpleName();

    private final Context mContext;
    private IStepDetector mStepDetector;

    public StepManager(Context context) {
        mContext = context;
    }

     public void start() {
        if (!NativeStepDetector.isSupported(mContext))
            mStepDetector = new SupportStepDetector(mContext);
         else
            mStepDetector = new NativeStepDetector(mContext);
        mStepDetector.initialize();
    }

    public void registerListener(StepDetectorListener listener) {
        mStepDetector.registerListener(listener);
    }

    public void unregisterListener(StepDetectorListener listener) {
        mStepDetector.unregisterListener(listener);
    }

    public void stop() {
        mStepDetector.stop();
    }
}
