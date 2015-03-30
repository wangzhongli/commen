package com.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.text.TextUtils;

public final class MLog {
	private static final boolean	DEBUG			= true;
	private static final boolean	EXPORT_TO_FILE	= false;
	private static final String		TAG				= "MLog";

	public static void d(String tag, String msg) {
		if (DEBUG) {
			if (TextUtils.isEmpty(tag)) {
				android.util.Log.d(TAG, msg);
			} else {
				android.util.Log.d(tag, msg);
			}
		}

		if (EXPORT_TO_FILE) {
			wirteLogToFile(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			if (TextUtils.isEmpty(tag)) {
				android.util.Log.v(TAG, msg);
			} else {
				android.util.Log.v(tag, msg);
			}
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			if (TextUtils.isEmpty(tag)) {
				android.util.Log.i(TAG, msg);
			} else {
				android.util.Log.i(tag, msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			if (TextUtils.isEmpty(tag)) {
				android.util.Log.e(TAG, msg);
			} else {
				android.util.Log.e(tag, msg);
			}
		}
		if (EXPORT_TO_FILE) {
			wirteLogToFile(tag, msg);
		}
	}

	private static void wirteLogToFile(String tag, String msg) {
		File fileSDcard = Environment.getExternalStorageDirectory();
		File file = new File(fileSDcard.getAbsolutePath() + "/MLog");

		try {
			if (!file.exists()) {
				file.mkdirs();
			}

			File newFile = new File(fileSDcard.getAbsolutePath() + "/MLog/log.txt");
			if (!newFile.exists()) {
				newFile.createNewFile();
			}

			FileOutputStream fout = null;
			fout = new FileOutputStream(newFile, true);

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

			byte[] bytes = new String(dateFormat.format(new Date()) + ": " + tag + " : " + msg + "\r\n").getBytes();
			fout.write(bytes);
			fout.close();
		}
		catch (IOException e) {

		}
	}

	public static String GetStactTrace(Exception e) {
		if (null == e)
			return null;
		String ret = e.toString();
		StackTraceElement[] stack = e.getStackTrace();
		for (int i = 0; stack != null && i < stack.length; ++i) {
			ret += "\n" + stack[i].toString();
		}
		return ret;
	}

	public static void PrintStackTrace(Exception e) {
		if (null == e)
			return;
		e("", "Eeception: " + e.toString());
		StackTraceElement[] stack = e.getStackTrace();
		for (int i = 0; stack != null && i < stack.length; ++i) {
			e("", stack[i].toString());
		}
	}
}
