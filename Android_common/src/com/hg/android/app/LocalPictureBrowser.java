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
import android.view.ViewGroup.LayoutParams;

import com.ThreeParty.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LocalPictureBrowser extends ActionBarActivity {

	public static final String		BroadcastAction_Delete	= "LocalPictureBrowser.BroadcastAction_Delete";

	public static final String		ImtentKey_Urls			= "ImtentKey_Urls";
	public static final String		ImtentKey_Index			= "ImtentKey_Index";
	public static final String		ImtentKey_TAG			= "ImtentKey_TAG";
	public static final String		TAG						= "LocalPictureBrowser";

	private List<String>			mImageUrls				= new ArrayList<String>();
	private int						mImageIndex				= -1;
	private String					tag;

	private ViewPager				mViewPager;

	private MyAdapter				adapter;

	private OnPageChangeListener	changeListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new HackyViewPager(this);
		mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(mViewPager);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		//	getSupportActionBar().hide();

		Intent intent = getIntent();
		if (intent != null) {
			tag = intent.getStringExtra(ImtentKey_TAG);
			String[] urls = intent.getStringArrayExtra(ImtentKey_Urls);
			for (String url : urls) {
				if (!url.startsWith("file:")) {
					mImageUrls.add("file://" + url);
				} else {
					mImageUrls.add(url);
				}
			}
			if (mImageIndex == -1) {
				mImageIndex = intent.getIntExtra(ImtentKey_Index, 0);
			}
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
		getMenuInflater().inflate(R.menu.hg_localpicturebrowser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		} else if (item.getItemId() == R.id.hg_action_trash) {
			mImageUrls.remove(mImageIndex);
			mViewPager.setAdapter(adapter = new MyAdapter());
			Intent intent = new Intent(BroadcastAction_Delete);
			intent.putExtra(ImtentKey_Index, mImageIndex);
			if (tag != null) {
				intent.putExtra(ImtentKey_TAG, tag);
			}
			sendBroadcast(intent);
			if (mImageIndex >= mImageUrls.size()) {
				mImageIndex = mImageUrls.size() - 1;
			}
			if (mImageIndex < 0) {
				finish();
			}
			changeListener.onPageSelected(mImageIndex);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageUrls == null ? 0 : mImageUrls.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final View view = View.inflate(LocalPictureBrowser.this, R.layout.hg_photo_view, null);
			PhotoView photoView = ((PhotoView) view.findViewById(R.id.photoView));
			photoView.setOnViewTapListener(new OnViewTapListener() {

				@Override
				public void onViewTap(View view, float x, float y) {
//					if (getSupportActionBar().isShowing()) {
//						getSupportActionBar().hide();
//					} else {
//						getSupportActionBar().show();
//					}
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
