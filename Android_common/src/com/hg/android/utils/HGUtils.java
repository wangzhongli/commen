package com.hg.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ThreeParty.R;

public class HGUtils {

	public static boolean isListEmpty(List<?> list) {
		return list == null || list.size() == 0;
	}

	public static void shareText(Context context, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
		intent.setType("text/plain"); // 分享发送的数据类型
		intent.putExtra(Intent.EXTRA_TEXT, text); // 分享的内容
		context.startActivity(Intent.createChooser(intent, "分享到"));// 目标应用选择对话框的标题
	}

	public static void shareImage(Context context, String file) {
		Uri uri = Uri.fromFile(new File(file));
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.setType("image/jpeg");
		context.startActivity(Intent.createChooser(shareIntent, "分享图片到"));// 目标应用选择对话框的标题
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void copy2Clipboard(Context context, String text) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setPrimaryClip(ClipData.newPlainText(null, text));
			Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "系统版本低,无法完成复制操作!!", Toast.LENGTH_SHORT).show();
		}
	}

	public static void hideKeyboard(Activity activity) {
		View focus = activity.getCurrentFocus();
		if (focus != null) {
			InputMethodManager inputManager = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String uniqueId(Context context) {
		try {
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			final String tmDevice, androidId;
			tmDevice = tm.getDeviceId();
			androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID);
			if (TextUtils.isEmpty(tmDevice) && TextUtils.isEmpty(androidId)) {
				return null;
			}
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (!TextUtils.isEmpty(tmDevice)) {
				md.update(tmDevice.getBytes());
			}
			if (!TextUtils.isEmpty(androidId)) {
				md.update(androidId.getBytes());
			}
			StringBuffer hexString = new StringBuffer();
			byte[] messageDigest = md.digest();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String uniqueId2(Context context) {
		String uid = uniqueId(context);
		if (TextUtils.isEmpty(uid)) {
			uid = UUID.randomUUID().toString();
		}
		return uid;
	}

	public static void openDefaultBrowser(Context context, String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);
		context.startActivity(intent);
	}

	public static String parseFilePath(Context context, Uri uri) {
		String picturePath = null;
		try {
			if (uri.getScheme().startsWith("file")) {
				picturePath = uri.toString().replace("file://", "");
				picturePath = URLDecoder.decode(picturePath, "utf-8");
			} else {
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return picturePath;
	}

	public static void copy(String srcfile, String destPath) {
		InputStream is = null;
		OutputStream os = null;
		try {
			int byteread = 0;
			is = new FileInputStream(srcfile);
			os = new FileOutputStream(destPath);
			byte[] buffer = new byte[2048];
			while ((byteread = is.read(buffer)) > 0) {
				os.write(buffer, 0, byteread);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void showConfirmDialog(Context context, CharSequence title, CharSequence message, final Runnable okRun) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton(context.getString(R.string.common_ok), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (okRun != null) {
							okRun.run();
						}
					}
				}).setNegativeButton(context.getString(R.string.common_cancel), null).show();
	}

	public static <Key> void removeFromMapThatKeyNotExistInList(Map<Key, ?> map, List<Key> list) {
		Object[] keys = map.keySet().toArray();
		for (Object key : keys) {
			if (!list.contains(key)) {
				map.remove(key);
			}
		}
	}

	public static String nil(String text) {
		return text == null ? "" : text;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}
}
