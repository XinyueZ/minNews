package com.gmail.hasszhao.mininews.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


/**
 * Store app and device information.
 * 
 * @author Chris.Xinyue Zhao<czhao@cellular.de>
 */
public final class Prefs extends BasicPrefs {

	/** The Constant VERSION. */
	private final static String VERSION = "DeviceData.version";
	/** The Constant DEVICE_ID. */
	private final static String DEVICE_ID = "DeviceData.deviceid";
	/** The Constant MODEL. */
	private final static String MODEL = "DeviceData.model";
	/** The Constant OS. */
	private final static String OS = "DeviceData.os";
	/** The Constant OS_VERSION. */
	private final static String OS_VERSION = "DeviceData.osversion";
	/** The Constant SIM_COUNTRY_ISO. */
	private final static String SIM_COUNTRY_ISO = "DeviceData.country";
	private final static String KEY_SUPPORT_PULL_TO_LOAD = "support.pull.to.load";
	private final static String KEY_NEWS_SIZE = "news.size";
	private final static String KEY_SUPPORT_ENGLISH = "language.english";
	private final static String KEY_SUPPORT_CHINESE = "language.chinese";
	private final static String KEY_SUPPORT_GERMAN = "language.german";
	/** The Instance. */
	private static Prefs sInstance;


	/**
	 * Singleton method.
	 * 
	 * @return single instance of DeviceData
	 */
	public static Prefs createInstance(Context _cxt) {
		if (sInstance == null) {
			synchronized (Prefs.class) {
				if (sInstance == null) {
					sInstance = new Prefs(_cxt);
				}
			}
		}
		return sInstance;
	}


	public static Prefs getInstance() {
		return sInstance;
	}


	private Prefs() {
		super(null);
	}


	/**
	 * Created a DeviceData storage.
	 * 
	 */
	private Prefs(Context _cxt) {
		super(_cxt);
		try {
			TelephonyManager tm = (TelephonyManager) _cxt.getSystemService(Activity.TELEPHONY_SERVICE);
			PackageManager manager = _cxt.getPackageManager();
			PackageInfo info = manager.getPackageInfo(_cxt.getPackageName(), 0);
			setAppVersion(info.versionName);
			setDeviceId(DeviceId.getUDID(_cxt));
			// setDeviceId("123455654123");
			if (TextUtils.isEmpty(android.os.Build.MODEL)) {
				setDeviceModel("UNKNOWN");
			} else {
				setDeviceModel(android.os.Build.MODEL);
			}
			setOs("ANDROID");
			setOsVersion(android.os.Build.VERSION.RELEASE);
			setSimCountryIso(tm.getSimCountryIso());
		} catch (NameNotFoundException _e) {
			_e.printStackTrace();
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}


	/**
	 * Get app version.
	 * 
	 * @return the app version
	 */
	public String getAppVersion() {
		return getString(VERSION, "");
	}


	/**
	 * Save app version.
	 * 
	 * @param _value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setAppVersion(String _value) {
		return setString(VERSION, _value);
	}


	/**
	 * Get device id.
	 * 
	 * @return the device id
	 */
	public String getDeviceId() {
		return getString(DEVICE_ID, "");
	}


	/**
	 * Save device id.
	 * 
	 * @param _value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setDeviceId(String _value) {
		return setString(DEVICE_ID, _value);
	}


	/**
	 * Get device model.
	 * 
	 * @return the device model
	 */
	public String getDeviceModel() {
		return getString(MODEL, "");
	}


	/**
	 * Save device model.
	 * 
	 * @param _value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setDeviceModel(String _value) {
		return setString(MODEL, _value);
	}


	/**
	 * Get OS name.
	 * 
	 * @return the os
	 */
	public String getOs() {
		return getString(OS, "");
	}


	/**
	 * Save OS name.
	 * 
	 * @param _value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setOs(String _value) {
		return setString(OS, _value);
	}


	/**
	 * Get OS version.
	 * 
	 * @return the os version
	 */
	public String getOsVersion() {
		return getString(OS_VERSION, "");
	}


	/**
	 * Save current OS version.
	 * 
	 * @param _value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setOsVersion(String _value) {
		return setString(OS_VERSION, _value);
	}


	/**
	 * Get a country iso code.
	 * 
	 * @return the sim country iso
	 */
	public String getSimCountryIso() {
		return getString(SIM_COUNTRY_ISO, "");
	}


	/**
	 * Save country iso from sim-card.
	 * 
	 * @param _value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setSimCountryIso(String _value) {
		return setString(SIM_COUNTRY_ISO, _value);
	}


	public boolean isSupportPullToLoad() {
		return getBoolean(KEY_SUPPORT_PULL_TO_LOAD, true);
	}


	public void setSupportPullToLoad(boolean _support) {
		setBoolean(KEY_SUPPORT_PULL_TO_LOAD, _support);
	}


	public boolean isSupportEnglish() {
		return getBoolean(KEY_SUPPORT_ENGLISH, true);
	}


	public void setSupportEnglish(boolean _support) {
		setBoolean(KEY_SUPPORT_ENGLISH, _support);
	}


	public boolean isSupportChinese() {
		return getBoolean(KEY_SUPPORT_CHINESE, true);
	}


	public void setSupportChinese(boolean _support) {
		setBoolean(KEY_SUPPORT_CHINESE, _support);
	}


	public boolean isSupportGerman() {
		return getBoolean(KEY_SUPPORT_GERMAN, true);
	}


	public void setSupportGerman(boolean _support) {
		setBoolean(KEY_SUPPORT_GERMAN, _support);
	}


	public int getNewsSize() {
		return getInt(KEY_NEWS_SIZE, 10);
	}


	public void setNewsSize(int _size) {
		setInt(KEY_NEWS_SIZE, _size);
	}


	/**
	 * Fetch Device "UDID" like iOS.
	 */
	private static class DeviceId {

		/**
		 * requires permissions TELEPHONY_SERVICE on cell phones and
		 * WIFI_SERVICE on WiFi-only devices.
		 * 
		 * @param _cxt
		 *            the _cxt
		 * @return String
		 * @throws Exception
		 *             when there is no unique ID
		 */
		static public String getUDID(Context _cxt) throws Exception {
			String readableId;
			// Requires READ_PHONE_STATE
			TelephonyManager tm = (TelephonyManager) _cxt.getSystemService(Context.TELEPHONY_SERVICE);
			// gets the imei (GSM) or MEID/ESN (CDMA)
			String imei = tm.getDeviceId();
			if (null != imei && imei.length() > 0 && Long.parseLong(imei) > 0) { // 000000000000000
																					// for
																					// emulator
				readableId = imei;
			} else {
				// devices without SIM card (e.g. WiFi-only tablets)
				// requires ACCESS_WIFI_STATE
				WifiManager wm = (WifiManager) _cxt.getSystemService(Context.WIFI_SERVICE);
				// gets the MAC address
				String mac = wm.getConnectionInfo().getMacAddress();
				if (null != mac && mac.length() > 0) {
					readableId = mac;
				} else {
					// gets the android-assigned id
					// unfortunately, this is not unique on some devices:
					// http://groups.google.com/group/android-developers/browse_thread/thread/53898e508fab44f6/84e54feb28272384?pli=1
					// so it's only a fallback for emulators
					String androidId = Secure.getString(_cxt.getContentResolver(), Secure.ANDROID_ID);
					if (null != androidId && androidId.length() > 0) {
						readableId = androidId;
					} else {
						throw new Exception("cannot calculate a unique device ID");
					}
				}
			}
			return md5(readableId);
		}


		/**
		 * Md5.
		 * 
		 * @param _plaintext
		 *            the _plaintext
		 * @return the string
		 * @throws NoSuchAlgorithmException
		 *             the no such algorithm exception
		 */
		private static String md5(String _plaintext) throws NoSuchAlgorithmException {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(_plaintext.getBytes());
			byte[] hash = digest.digest();
			BigInteger bigInt = new BigInteger(1, hash);
			return String.format("%1$032X", bigInt).toLowerCase();
		}
	}
}
