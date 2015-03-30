package com.hg.android.widget;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;

public class ImagesGridViewSpecialOne extends ImagesGridView {

	int	realNumColumns	= 0;

	public ImagesGridViewSpecialOne(Context context) {
		super(context);
	}

	public ImagesGridViewSpecialOne(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public ImagesGridViewSpecialOne(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void initSubviews(Context context) {
		super.initSubviews(context);
	}

	@Override
	public void setNumColumns(int numColumns) {
		if (numColumns == -1) {
			super.setNumColumns(1);
		} else {
			realNumColumns = numColumns;
			super.setNumColumns(numColumns);
		}
	}

	@Override
	public ScaleType getScaleType() {
		if (imagesCount() == 1) {
			return ScaleType.CENTER_INSIDE;
		} else {
			return super.getScaleType();
		}
	}

	@Override
	public void setImageUrls(List<String> imageUrls) {
		super.setImageUrls(imageUrls);
		if (imagesCount() == 1) {
			setNumColumns(-1);
		} else {
			setNumColumns(Math.max(3, realNumColumns));
		}
	}
}
