package com.hg.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.ThreeParty.R;
import com.hg.android.utils.HGUtils;

/**
 * 日期: 2015-3-19 下午3:06:44
 * 作者: 刘浩歌
 * 邮箱: okz@outlook.com
 * 作用:
 */
public class RatingBar extends View {

	private Drawable		mNormalStar;
	private Drawable		mHighlightStar;
	private int				mPadding;
	private int				mStarSize;
	private int				mNumStars;
	private float			mRating;

	private boolean			mEnable;

	private GestureDetector	mGestureDetector;

	public RatingBar(Context context) {
		this(context, null);
	}

	public RatingBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);
		mNormalStar = a.getDrawable(R.styleable.RatingBar_normalStar);
		mHighlightStar = a.getDrawable(R.styleable.RatingBar_highlightStar);
		mNumStars = a.getInteger(R.styleable.RatingBar_numStars, 5);
		a.recycle();

		mGestureDetector = new GestureDetector(context, new MyOnGestureListener());
		mGestureDetector.setIsLongpressEnabled(true);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		setLongClickable(true);
	}

	public float getRating() {
		return mRating;
	}

	public void setRating(float rating) {
		this.mRating = rating;
		invalidate();
	}

	public boolean isEnable() {
		return mEnable;
	}

	public void setEnable(boolean enable) {
		this.mEnable = enable;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int intP = (int) mRating;
		for (int i = 0; i < mNumStars; i++) {
			int left = i * (mStarSize + mPadding);
			int top = 0;
			if (i < intP) {
				mHighlightStar.setBounds(left, top, left + mStarSize, top + mStarSize);
				mHighlightStar.draw(canvas);
			} else {
				mNormalStar.setBounds(left, top, left + mStarSize, top + mStarSize);
				mNormalStar.draw(canvas);
			}
		}

		float floatP = mRating - intP;
		canvas.save();
		int left = intP * (mStarSize + mPadding);
		int top = 0;
		int right = (int) (left + floatP * mStarSize);
		canvas.clipRect(left, top, right, top + mStarSize);
		mHighlightStar.setBounds(left, top, left + mStarSize, top + mStarSize);
		mHighlightStar.draw(canvas);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
			heightSize = HGUtils.dip2px(getContext(), 32);
		}
		mStarSize = heightSize;
		mPadding = (int) (heightSize * 0.2f);
		int widthSize = mPadding * (mNumStars - 1) + mNumStars * heightSize;
		setMeasuredDimension(widthSize, heightSize);
	}

	private class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			if (mEnable)
				setRating(e.getX() / getMeasuredWidth() * mNumStars);
			return mEnable;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (mEnable)
				setRating(e2.getX() / getMeasuredWidth() * mNumStars);
			return mEnable;
		}
	}
}
