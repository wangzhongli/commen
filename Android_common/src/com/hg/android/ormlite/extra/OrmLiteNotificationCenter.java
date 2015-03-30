package com.hg.android.ormlite.extra;

import java.util.HashMap;
import java.util.Map;

import android.database.ContentObservable;
import android.database.ContentObserver;

public class OrmLiteNotificationCenter {

	private static OrmLiteNotificationCenter		sInstance;

	private final Map<Class<?>, ContentObservable>	observables	= new HashMap<Class<?>, ContentObservable>();

	public static OrmLiteNotificationCenter sharedInstance() {
		if (sInstance == null) {
			sInstance = new OrmLiteNotificationCenter();
		}
		return sInstance;
	}

	public void registerContentObserver(Class<?> clazz, ContentObserver observer) {
		synchronized (observables) {
			ContentObservable observable = observables.get(clazz);
			if (observable == null) {
				observable = new ContentObservable();
				observables.put(clazz, observable);
			}
			observable.registerObserver(observer);
		}
	}

	public void unregisterContentObserver(Class<?> clazz, ContentObserver observer) {
		synchronized (observables) {
			ContentObservable observable = observables.get(clazz);
			if (observable != null) {
				observable.unregisterObserver(observer);
			}
		}
	}

	public void notifyChange(Class<?> clazz) {
		synchronized (observables) {
			ContentObservable observable = observables.get(clazz);
			if (observable != null) {
				observable.dispatchChange(false);
			}
		}
	}
}
