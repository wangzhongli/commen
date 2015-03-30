package com.hg.android.utils;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.common.utils.ToastUtil;

/**
 * 日期: 2015年1月8日 下午7:08:55
 * 作者: 刘浩歌
 * 邮箱: okz@outlook.com
 * 作用:
 */
public class TakeCropHelper {
	private static final int		RequestCode_Major		= 0xfff0;
	private static final int		RequestCode_TakePhoto	= RequestCode_Major + 1;
	private static final int		RequestCode_PickPhoto	= RequestCode_Major + 2;
	public static final int			RequestCode_CorpPhoto	= RequestCode_Major + 3;
	public String					PhotoFile;
	private WeakReference<Activity>	activity;
	boolean							doCrop					= false;
	private OnFinishedListener		onFinishedListener;

	public TakeCropHelper(Activity activity, boolean doCrop, OnFinishedListener onFinishedListener) {
		this.doCrop = doCrop;
		this.onFinishedListener = onFinishedListener;
		this.activity = new WeakReference<Activity>(activity);
		PhotoFile = new File(activity.getCacheDir(), "TakeCropHelper.jpg").getAbsolutePath();
	}

	void initCorpInfo(Intent intent) {
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 480);
		intent.putExtra("return-data", true);
	}

	public void openAblum() {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			if (doCrop) {
				initCorpInfo(intent);
			}
			activity.get().startActivityForResult(intent, RequestCode_PickPhoto);
		}
		catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(activity.get(), "打开图库失败");
		}

	}

	public void openCamera() {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 设置裁剪
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(PhotoFile)));
			activity.get().startActivityForResult(intent, RequestCode_TakePhoto);
		}
		catch (Exception e) {
			ToastUtil.show(activity.get(), "打开相机失败");
			e.printStackTrace();
		}
	}

	public void openCorp() {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(Uri.fromFile(new File(PhotoFile)), "image/*");
			initCorpInfo(intent);
			activity.get().startActivityForResult(intent, RequestCode_CorpPhoto);
		}
		catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(activity.get(), "无法编辑图片");
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case RequestCode_TakePhoto:
			if (doCrop) {
				openCorp();
			} else {
				postFile(PhotoFile);
			}
			break;
		case RequestCode_PickPhoto:
		case RequestCode_CorpPhoto:
			if (doCrop) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					ImageUtils.saveBitmap(photo, PhotoFile);
					photo.recycle();
					postFile(PhotoFile);
				}
			} else {
				String picturePath = HGUtils.parseFilePath(activity.get(), data.getData());
				if (!TextUtils.isEmpty(picturePath)) {
					postFile(picturePath);
				} else {
					Toast.makeText(activity.get(), "图片读取失败", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}

	void postFile(String photoFile) {
		if (onFinishedListener != null) {
			onFinishedListener.onFinished(photoFile);
		}
	}

	public interface OnFinishedListener {
		public void onFinished(String file);
	}
}
