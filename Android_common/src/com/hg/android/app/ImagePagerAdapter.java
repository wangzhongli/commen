package com.hg.android.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.three.widget.RecyclingPagerAdapter;

public class ImagePagerAdapter extends RecyclingPagerAdapter {
	private final Context				context;
	private final List<String>			list;
	private boolean						isInfiniteLoop;
	private final DisplayImageOptions	options;
	private OnImageClickListener		imageClickListener;

	public ImagePagerAdapter(Context context, List<String> list, Drawable placeholder, Drawable fail) {
		this.context = context;
		this.list = list == null ? new ArrayList<String>() : list;
		isInfiniteLoop = false;
		options = new DisplayImageOptions.Builder().showImageOnLoading(placeholder).showImageForEmptyUri(placeholder)
				.showImageOnFail(fail).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return isInfiniteLoop ? Integer.MAX_VALUE : list.size();
	}

	private int getPosition(int position) {
		return isInfiniteLoop ? position % (list.size()) : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ScaleType.FIT_XY);
			convertView = imageView;
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (imageClickListener != null) {
						imageClickListener.onClick((Integer) arg0.getTag());
					}
				}
			});
		}
		convertView.setTag(position);
		ImageLoader.getInstance().displayImage(list.get(getPosition(position)), (ImageView) convertView, options);
		return convertView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}

	public void setOnImageClickListener(OnImageClickListener imageClickListener) {
		this.imageClickListener = imageClickListener;
	}

	public interface OnImageClickListener {
		public void onClick(int position);
	}

}
