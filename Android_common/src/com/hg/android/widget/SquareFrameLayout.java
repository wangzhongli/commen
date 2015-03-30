package com.hg.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

public class SquareFrameLayout extends ScaleFrameLayout {

	public SquareFrameLayout(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	public SquareFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public float getHeightScale() {
		return 1;
	}
}
