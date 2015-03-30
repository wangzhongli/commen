package com.common.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * SIMCard工具类，获取SIM卡相关信息
 * 
 */
public class SIMCardUtil {

	/**
	 * 通过系统接口获取手机号码，不一定能够获取 需要权限 <uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @param context
	 * @return
	 */
	public static String getNativePhoneNumber(Context context) {
		String phone = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			phone = tm.getLine1Number();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return phone;
	}

	/**
	 * 获取服务提供商信息 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @param context
	 * @return
	 */
	public static String getProvidersName(Context context) {
		String ProvidersName = null;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String IMSI = tm.getSubscriberId();
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			// System.out.println(IMSI);
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				ProvidersName = "中国移动";
			} else if (IMSI.startsWith("46001")) {
				ProvidersName = "中国联通";
			} else if (IMSI.startsWith("46003")) {
				ProvidersName = "中国电信";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ProvidersName;
	}

	/**
	 * 获取手机SIM卡IMSI号 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		String imsi = null;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return imsi;
	}

	/**
	 * 获取SIM序列号 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimSerialNumber(Context context) {
		String simSerialNum = null;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			simSerialNum = tm.getSimSerialNumber();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return simSerialNum;
	}

	/**
	 * 获取设备id（IMEI） 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String deviceId = null;
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = tm.getDeviceId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return deviceId;
	}

}
