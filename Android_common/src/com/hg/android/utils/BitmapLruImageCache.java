package com.hg.android.utils;


import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Basic LRU Memory cache.
 * 
 * @author Trey Robinson
 * 
 */
public class BitmapLruImageCache extends LruCache<String, SafeBitmap> {

	private final String	TAG	= this.getClass().getSimpleName();

	public BitmapLruImageCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, SafeBitmap value) {
		return value.get().getRowBytes() * value.get().getHeight();
	}

	public void safePut(String url, SafeBitmap bitmap) {
		Log.v(TAG, "Added item to Mem Cache");
		bitmap.retain();
		put(url, bitmap);
	}

	@Override
	protected void entryRemoved(boolean evicted, String key, SafeBitmap oldValue, SafeBitmap newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
		if (oldValue != null) {
			oldValue.release();
		}
	}

}
