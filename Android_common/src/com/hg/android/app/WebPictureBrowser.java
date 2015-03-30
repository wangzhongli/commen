package com.hg.android.app;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ThreeParty.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WebPictureBrowser extends ActionBarActivity {

	public static final String		IntentKey_Urls		= "ImtentKey_Urls";
	public static final String		IntentKey_Index		= "ImtentKey_Index";
	public static final String		IntentKey_Remark	= "IntentKey_Remark";
	public static final String		TAG					= "WebPictureBrowser";

	private List<String>			mImageUrls			= new ArrayList<String>();
	private int						mImageIndex			= -1;

	private ViewPager				mViewPager;
	private TextView				mTextView;

	private MyAdapter				adapter;

	private OnPageChangeListener	changeListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hg_activity_webpicturebrowser);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mTextView = (TextView) findViewById(R.id.textView);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().hide();

		Intent intent = getIntent();
		if (intent != null) {
			List<String> urls = (List<String>) intent.getSerializableExtra(IntentKey_Urls);
			
			if (urls != null) {
				for (String url : urls) {
					mImageUrls.add(url);
				}
			}
			if (mImageIndex == -1) {
				mImageIndex = intent.getIntExtra(IntentKey_Index, 0);
			}
			mTextView.setText(intent.getStringExtra(IntentKey_Remark));
		}
		mImageIndex = mImageIndex < 0 ? 0 : mImageIndex;

		mViewPager.setAdapter(adapter = new MyAdapter());

		changeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mImageIndex = position;
				setTitle((position + 1) + "/" + mViewPager.getAdapter().getCount());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageScrollStateChanged(int state) {}
		};

		mViewPager.setOnPageChangeListener(changeListener);
		mViewPager.setCurrentItem(mImageIndex);
		changeListener.onPageSelected(mImageIndex);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageUrls == null ? 0 : mImageUrls.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final View view = View.inflate(WebPictureBrowser.this, R.layout.hg_photo_view, null);
			PhotoView photoView = ((PhotoView) view.findViewById(R.id.photoView));
			photoView.setOnViewTapListener(new OnViewTapListener() {

				@Override
				public void onViewTap(View view, float x, float y) {
					if (getSupportActionBar().isShowing()) {
						getSupportActionBar().hide();
					} else {
						getSupportActionBar().show();
					}
				}
			});
			container.addView(view);
			view.setTag(Integer.valueOf(position));
			ImageLoader.getInstance().displayImage(mImageUrls.get(position), photoView);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
			if (photoView != null) {
				photoView.setImageBitmap(null);
				container.removeView(view);
				if (((Integer) view.getTag()).intValue() != position) {
					Log.e(TAG, "view.getTag() " + view.getTag() + " is not equal to position " + position);
				}
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
}
