package com.hg.android.widget;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import com.ThreeParty.R;
import com.hg.android.app.ImagePagerAdapter;
import com.hg.android.app.ImagePagerAdapter.OnImageClickListener;
import com.viewpagerindicator.CirclePageIndicator;

public class ImagePagerLayout extends RelativeLayout {

	boolean						isInited	= false;

	private AutoScrollViewPager	viewPager;
	private CirclePageIndicator	indicator;

	private List<String>		imageUrls;

	public static Drawable		ImagePlaceholder;
	public static Drawable		ImageFail;

	OnImageClickListener		imageClickListener;

	private ImagePagerAdapter	adapter;

	public ImagePagerLayout(Context context) {
		super(context);
		initSubviews(context);
	}

	public ImagePagerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSubviews(context);
	}

	public ImagePagerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSubviews(context);
	}

	void initSubviews(Context context) {
		if (!isInited) {
			LayoutInflater.from(context).inflate(R.layout.hg_imagepagerlayout, this, true);
			isInited = true;
			viewPager = (AutoScrollViewPager) findViewById(R.id.viewPager);
			indicator = (CirclePageIndicator) findViewById(R.id.indicator);

			viewPager.setInterval(2000);
			viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
			if (imageUrls != null) {
				setImageUrls(imageUrls);
			}
		}
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
		if (viewPager == null) {
			return;
		}
		adapter = new ImagePagerAdapter(getContext(), imageUrls, ImagePlaceholder, ImageFail);
		adapter.setOnImageClickListener(imageClickListener);
		viewPager.setAdapter(adapter);
		indicator.setViewPager(viewPager);
		if (imageUrls != null && imageUrls.size() > 0) {
			viewPager.startAutoScroll();
		}
	}

	public void setOnImageClickListener(OnImageClickListener imageClickListener) {
		this.imageClickListener = imageClickListener;
		if (adapter != null) {
			adapter.setOnImageClickListener(imageClickListener);
		}
	}

}
