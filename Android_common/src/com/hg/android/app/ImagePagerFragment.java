package com.hg.android.app;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import com.ThreeParty.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * ImagePagerFragment
 */
public class ImagePagerFragment extends Fragment {

	private Context				context;
	private AutoScrollViewPager	viewPager;
	private CirclePageIndicator	indicator;

	private List<String>		imageUrls;

	public static Drawable		ImagePlaceholder;
	public static Drawable		ImageFail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();

		View v = inflater.inflate(R.layout.hg_fragment_autoimagepager, container, false);
		viewPager = (AutoScrollViewPager) v.findViewById(R.id.viewPager);
		indicator = (CirclePageIndicator) v.findViewById(R.id.indicator);

		viewPager.setInterval(2000);
		viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
		if (imageUrls != null) {
			setImageUrls(imageUrls);
		}	
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
		if (viewPager == null) {
			return;
		}		
		viewPager.setAdapter(new ImagePagerAdapter(context, imageUrls, ImagePlaceholder, ImageFail));
		indicator.setViewPager(viewPager);
		if (imageUrls != null && imageUrls.size() > 0) {
			viewPager.startAutoScroll();
		}
	}
}
