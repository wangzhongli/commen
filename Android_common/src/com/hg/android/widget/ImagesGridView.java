package com.hg.android.widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ThreeParty.R;
import com.hg.android.app.WebPictureBrowser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImagesGridView extends MyGridView {

	List<String>			imageUrls			= new ArrayList<String>();
	MyAdapter				adapter;

	View.OnClickListener	onItemClickListener;
	float					itemHeightScale		= 1;
	DisplayImageOptions		displayImageOptions	= new DisplayImageOptions.Builder().cacheInMemory(true)
														.cacheOnDisk(true).considerExifParams(true)
														.bitmapConfig(Bitmap.Config.RGB_565).build();

	public ImagesGridView(Context context) {
		super(context);
		initSubviews(context);
	}

	public ImagesGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSubviews(context);
	}

	@SuppressLint("NewApi")
	public ImagesGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initSubviews(context);
	}

	protected void initSubviews(Context context) {
		//setStretchMode(STRETCH_COLUMN_WIDTH);
		onItemClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int index = (Integer) arg0.getTag();
				if (index >= imagesCount()) {
					onAddPicture();
				} else {
					clickOnPicture(index);
				}
			}
		};
		adapter = new MyAdapter();
		setAdapter(adapter);
	}

	protected void clickOnPicture(int index) {
		Intent intent = new Intent(getContext(), WebPictureBrowser.class);
		intent.putExtra(WebPictureBrowser.IntentKey_Index, index);
		intent.putExtra(WebPictureBrowser.IntentKey_Urls, (Serializable) imageUrls);
		getContext().startActivity(intent);
	}

	public void setItemHeightScale(float scale) {
		this.itemHeightScale = scale;
	}

	public float getItemHeightScale() {
		return itemHeightScale;
	}

	public int imagesCount() {
		return imageUrls == null ? 0 : imageUrls.size();
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	void onAddPicture() {}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls.clear();
		if (imageUrls != null) {
			this.imageUrls.addAll(imageUrls);
		}
		adapter.notifyDataSetChanged();
	}

	public void removeImageAtIndex(int index) {
		if (index < this.imageUrls.size()) {
			this.imageUrls.remove(index);
			adapter.notifyDataSetChanged();
		}
	}

	public void addImage(String url) {
		this.imageUrls.add(url);
		adapter.notifyDataSetChanged();
	}

	protected String calcShowFile(String file) {
		return file;
	}

	protected int getAdapterCount() {
		return imagesCount();
	}

	public ScaleType getScaleType() {
		return ScaleType.CENTER_CROP;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return getAdapterCount();
		}

		@Override
		public String getItem(int arg0) {
			if (imageUrls != null && imageUrls.size() > arg0) {
				return imageUrls.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ScaleFrameLayout layout = (ScaleFrameLayout) arg1;
			if (layout == null) {
				arg1 = layout = new ScaleFrameLayout(getContext());
				ImageView imageView = new ImageView(getContext());
				imageView.setBackgroundResource(R.drawable.hg_sl_menuitem_bg);
				imageView.setOnClickListener(onItemClickListener);
				layout.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
			ImageView imageView = (ImageView) layout.getChildAt(0);
			imageView.setScaleType(getScaleType());
			imageView.setTag(arg0);
			layout.setHeightScale(getItemHeightScale());

			String file = getItem(arg0);
			if (!TextUtils.isEmpty(file)) {
				imageView.setImageDrawable(null);
				ImageLoader.getInstance().displayImage(calcShowFile(file), imageView, displayImageOptions);
			} else {
				ImageLoader.getInstance().displayImage(null, imageView);
				imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.hg_add_picture2));
			}
			return arg1;
		}

	}
}
