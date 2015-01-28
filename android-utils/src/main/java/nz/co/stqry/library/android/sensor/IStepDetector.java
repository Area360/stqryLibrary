package nz.co.stqry.library.android.sensor;

/**
 * Created by Marc Giovannoni on 5/11/14.
 */
public interface IStepDetector {
    public void initialize();
    public void registerListener(StepDetectorListener listener);
    public void unregisterListener(StepDetectorListener listener);
    public void stop();
}
