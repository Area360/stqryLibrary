package nz.co.stqry.library.android.hardware;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;

public class DeviceManager {
    private static DeviceManager mInstance;

    private String mDeviceOs;
    private String mDeviceNetwork;
    private String mDeviceWireless;
    private String mUserSetLanguage;
    private int mWidth = -1;
    private int mHeight = -1;
    private boolean mBluetoothEnabled;

    public static DeviceManager getInstance() {
        if (mInstance == null) {
            synchronized (DeviceManager.class) {
                if (mInstance == null) {
                    mInstance = new DeviceManager();
                }
            }
        }
        return mInstance;
    }

    private DeviceManager() {
	}

    public void initialize(Context context) {
        mDeviceOs = Build.VERSION.RELEASE;
        mDeviceNetwork = deviceNetwork(context);
        mDeviceWireless = deviceWirelessNetwork(context);
        mUserSetLanguage = userSetLanguage();
        resolution(context);
        bluetooth();
    }

    private void bluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mBluetoothEnabled = false;
        } else {
            if (!mBluetoothAdapter.isEnabled())
                mBluetoothEnabled = true;
        }
    }

    private void resolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            Point size = new Point();
            display.getSize(size);

            mHeight = size.y;
            mWidth = size.x;
        }
    }

	private String deviceNetwork(Context c) {

		String deviceNetwork = null;

		if (c.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TELEPHONY)) {
			TelephonyManager telephonyManager = (TelephonyManager) c
					.getSystemService(Context.TELEPHONY_SERVICE);
			deviceNetwork = telephonyManager.getNetworkOperatorName();
		}
        if (deviceNetwork != null && deviceNetwork.equals(""))
            deviceNetwork = "";
		return deviceNetwork;
	}

	private String deviceWirelessNetwork(Context c) {

		String deviceWireless;

		WifiManager wifiMgr = (WifiManager) c
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		deviceWireless = wifiInfo.getSSID();

		return deviceWireless;
	}

	private String userSetLanguage() {

		String userSetLanguage = Locale.getDefault().getISO3Language();

		if (userSetLanguage == null) {
			userSetLanguage = null;
		} else if (userSetLanguage.length() >= 3) {
			userSetLanguage = userSetLanguage.substring(0, 2);
		}
		return userSetLanguage;
	}

    public int getScreenWidth() {
        return mWidth;
    }

    public int getScreenHeight() {
        return mHeight;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothEnabled;
    }

	public String getDeviceOs() {
		return mDeviceOs;
	}

	public String getDeviceNetwork() {
		return mDeviceNetwork;
	}

	public String getDeviceWireless() {
		return mDeviceWireless;
	}

	public String getUserSetLanguage() {
		return mUserSetLanguage;
	}

    public String getName() {
        return Build.MODEL;
    }

    public String getDeviceUid() {
        return Build.SERIAL;
    }
}
