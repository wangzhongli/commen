package com.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
	private static final String	FILE_NAME	= "share_date";

	public static void setSpParam(Context context, String key, Object object) {

		String type = object.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if ("String".equals(type)) {
			editor.putString(key, (String) object);
		} else if ("Integer".equals(type)) {
			editor.putInt(key, (Integer) object);
		} else if ("Boolean".equals(type)) {
			editor.putBoolean(key, (Boolean) object);
		} else if ("Float".equals(type)) {
			editor.putFloat(key, (Float) object);
		} else if ("Long".equals(type)) {
			editor.putLong(key, (Long) object);
		}

		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public static <Type> Type getSpParam(Context context, String key, Type defaultObject) {
		String type = defaultObject.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

		Object object = null;
		if ("String".equals(type)) {
			object = sp.getString(key, (String) defaultObject);
		} else if ("Integer".equals(type)) {
			object = sp.getInt(key, (Integer) defaultObject);
		} else if ("Boolean".equals(type)) {
			object = sp.getBoolean(key, (Boolean) defaultObject);
		} else if ("Float".equals(type)) {
			object = sp.getFloat(key, (Float) defaultObject);
		} else if ("Long".equals(type)) {
			object = sp.getLong(key, (Long) defaultObject);
		}
		return (Type) object;
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp;
	}
}
