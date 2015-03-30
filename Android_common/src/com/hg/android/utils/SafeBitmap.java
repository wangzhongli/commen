package com.hg.android.utils;

import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;
import android.util.Log;

public class SafeBitmap {

	private static final String	TAG			= "SafeBitmap";
	private Bitmap				bitmap		= null;
	private AtomicInteger		retainCount	= new AtomicInteger();
	static int					sTotalCount	= 0;

	/**
	 * @param bitmap
	 */
	public SafeBitmap(Bitmap bitmap) {
		super();
		if (bitmap == null) {
			throw new IllegalArgumentException("bitmap must not be null!");
		}
		if (bitmap.isRecycled()) {
			throw new IllegalArgumentException("a recycled bitmap cant be set!");
		}
		this.bitmap = bitmap;
		retainCount.incrementAndGet();
		++sTotalCount;
		Log.i(TAG, " TotalCount" + sTotalCount + " new " + bitmap.hashCode());
	}

	public Bitmap get() {
		return bitmap;
	}

	public SafeBitmap retain() {
		if (retainCount.get() > 0) {
			retainCount.incrementAndGet();
			return this;
		}
		throw new RuntimeException("bitmap retain count == 0");
	}

	public void release() {
		if (retainCount.get() > 0) {
			int val = retainCount.decrementAndGet();
			if (val == 0) {
				--sTotalCount;
				Log.i(TAG, " TotalCount" + sTotalCount + " release " + bitmap.hashCode());
				bitmap.recycle();
			} else if (val < 0) {
				throw new RuntimeException("bitmap release too many times");
			}
		}
	}
}
