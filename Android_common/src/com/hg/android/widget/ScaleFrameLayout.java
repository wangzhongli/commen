package com.hg.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ThreeParty.R;

public class ScaleFrameLayout extends FrameLayout {

	float	heightScale	= 1;

	public ScaleFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context, attrs);
	}

	public ScaleFrameLayout(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	public ScaleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getAttrs(context, attrs);
	}

	private void getAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScaleFrameLayout);
		heightScale = ta.getFloat(R.styleable.ScaleFrameLayout_heightScale, heightScale);
		ta.recycle();
	}

	public void setHeightScale(float heightScale) {
		if (this.heightScale != heightScale) {
			this.heightScale = heightScale;
			requestLayout();
			invalidate();
		}
	}

	public float getHeightScale() {
		return heightScale;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		super.onMeasure(widthMeasureSpec,
				MeasureSpec.makeMeasureSpec((int) (width * getHeightScale()), MeasureSpec.EXACTLY));
	}
}
