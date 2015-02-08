package nz.co.stqry.library.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Marc Giovannoni on 1/09/14.
 */
public class BluetoothManager {

    private static final String TAG = BluetoothManager.class.getSimpleName();

    private static final String ERROR_ENABLING_BLUETOOTH = "Could not enable bluetooth.";
    private static final String ERROR_DISABLING_BLUETOOTH = "Could not disable bluetooth.";
    private static final long TIMEOUT_DELAY = 5000;
    private final Context mContext;
    private Timer mTimer;

    /*
     * Bluetooth broadcast
     */
    private BluetoothBroadcastReceiver mReceiver;

    /*
     * Bluetooth listener
     */
    private BluetoothStateListener mListener;
    private boolean mRegistered = false;
    private boolean mRegisteredForRestart = false;
    private BluetoothStateListener mRestartBluetoothListener = new BluetoothStateSimpleListener() {

        @Override
        public void bluetoothStateOn() {
            if (mRegisteredForRestart)
                unregisterReceiver();
        }

        @Override
        public void bluetoothStateOff() {
            enableBluetooth(true, mRestartBluetoothListener);
        }

        @Override
        public void bluetoothError() {
            Log.e(TAG, "Error occurred while turning Bluetooth on, please reboot your phone");
        }
    };

    public BluetoothManager(Context context) {
        mContext = context;
        mReceiver = new BluetoothBroadcastReceiver();
        mTimer = new Timer();
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, filter);
        mRegistered = true;
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public boolean enableBluetooth(boolean enable, BluetoothStateListener bluetoothListener) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        boolean success = true;

        mListener = bluetoothListener;
        if (enable && !isEnabled) {
            success = bluetoothAdapter.enable();

            if (!success)
                Log.e(TAG, ERROR_ENABLING_BLUETOOTH);
            else
                mTimer.schedule(new TimeOutTask(), TIMEOUT_DELAY);
        }
        else if (!enable && isEnabled) {
            success = bluetoothAdapter.disable();

            if (!success)
                Log.e(TAG, ERROR_DISABLING_BLUETOOTH);
        }
        return success;
    }

    public void unregisterReceiver() {
        if (mRegistered) {
            mContext.unregisterReceiver(mReceiver);
            mRegistered = false;
        }
    }

    public void restartBluetooth() {
        if (!mRegistered) {
            registerReceiver();
            mRegisteredForRestart = true;
        }
        enableBluetooth(false, mRestartBluetoothListener);
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            mTimer.cancel();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (mListener != null)
                            mListener.bluetoothStateOff();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (mListener != null)
                            mListener.bluetoothStateTurningOn();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (mListener != null)
                            mListener.bluetoothStateOn();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if (mListener != null)
                            mListener.bluetoothStateTurningOn();
                        break;
                    case BluetoothAdapter.ERROR:
                        if (mListener != null)
                            mListener.bluetoothError();
                        break;
                }
            }
        }
    }

    /**
     * Allow to implement a subset of the listener instead of implement the full listener
     */
    public static class BluetoothStateSimpleListener implements BluetoothStateListener {

        @Override
        public void bluetoothStateOn() {

        }

        @Override
        public void bluetoothStateTurningOn() {

        }

        @Override
        public void bluetoothStateOff() {

        }

        @Override
        public void bluetoothStateTurningOff() {

        }

        @Override
        public void bluetoothError() {

        }
    }

    public interface BluetoothStateListener {

        /**
         * Called bluetooth adapter went on
         */
        public void bluetoothStateOn();

        /**
         * Called bluetooth adapter is turning on
         */
        public void bluetoothStateTurningOn();

        /**
         * Called bluetooth adapter went off
         */
        public void bluetoothStateOff();

        /**
         * Called bluetooth adapter is turning off
         */
        public void bluetoothStateTurningOff();

        /**
         * Called whenever bluetooth is in error state
         */
        public void bluetoothError();
    }

    /**
     * TimeOutTask, trigger if no response from Bluetooth after TIMEOUT_DELAY
     */
    private class TimeOutTask extends TimerTask {
        @Override
        public void run() {
            if (mListener != null)
                mListener.bluetoothError();
        }
    }
}
