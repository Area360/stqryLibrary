package nz.co.stqry.library.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by Marc Giovannoni on 3/11/14.
 */
public class BluetoothUtils {

    public static BluetoothDevice getDeviceFromMacAddress(String macAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return bluetoothAdapter.getRemoteDevice(macAddress);
    }
}
