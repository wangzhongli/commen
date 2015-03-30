package com.hg.android.widget;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.hg.android.app.LocalPictureBrowser;
import com.hg.android.utils.HGUtils;

public class ImagesGridEditView extends ImagesGridView {

	static int				GlobleCount				= 0;

	private final int		RequestCode_Major		= 0xf000 + (GlobleCount++) % 0xff0;
	private final int		RequestCode_TakePhoto	= RequestCode_Major + 1;
	private final int		RequestCode_PickPhoto	= RequestCode_Major + 2;
	private final String	TAG						= "ImagesGridEditView" + RequestCode_Major;
	BroadcastReceiver		receiver;
	private Uri				takePickureUri;

	private int				maxCount				= 9;										//默认九张

	public ImagesGridEditView(Context context) {
		super(context);
	}

	public ImagesGridEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public ImagesGridEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (receiver == null) {
			receiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context arg0, Intent intent) {
					if (intent.getAction().equals(LocalPictureBrowser.BroadcastAction_Delete)) {
						if (TAG.equals(intent.getStringExtra(LocalPictureBrowser.ImtentKey_TAG))) {
							int index = intent.getIntExtra(LocalPictureBrowser.ImtentKey_Index, -1);
							removeImageAtIndex(index);
						}
					}
				}
			};
			IntentFilter filter = new IntentFilter(LocalPictureBrowser.BroadcastAction_Delete);
			getContext().registerReceiver(receiver, filter);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (receiver != null) {
			getContext().unregisterReceiver(receiver);
			receiver = null;
		}
	}

	@Override
	protected void clickOnPicture(int index) {
		Intent intent = new Intent(getContext(), LocalPictureBrowser.class);
		intent.putExtra(LocalPictureBrowser.ImtentKey_Urls, imageUrls.toArray(new String[0]));
		intent.putExtra(LocalPictureBrowser.ImtentKey_Index, index);
		intent.putExtra(LocalPictureBrowser.ImtentKey_TAG, TAG);
		getContext().startActivity(intent);
	}

	@Override
	protected void initSubviews(Context context) {
		super.initSubviews(context);
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	@Override
	protected String calcShowFile(String file) {
		//必须是本地地址
		if (!file.startsWith("file:")) {
			file = "file://" + file;
		}
		return file;
	}

	@Override
	protected int getAdapterCount() {
		int imageCount = imagesCount();
		if (imageCount >= getMaxCount()) {
			return imageCount;
		}
		return imageCount + 1;
	}

	@Override
	void onAddPicture() {
		if (imagesCount() >= maxCount) {
			Toast.makeText(getContext(), "最多只能添加" + maxCount + "张图片", Toast.LENGTH_SHORT).show();
			return;
		}
		CharSequence[] items = new CharSequence[] { "从相册选取", "拍照" };
		new AlertDialog.Builder(getContext()).setTitle("选取照片").setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int witch) {
				arg0.dismiss();
				if (witch == 0) {
					onActionPickPicture();
				} else {
					onActionTakePicture();
				}
			}
		}).show();
	}

	private void onActionPickPicture() {
		try {
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			((Activity) getContext()).startActivityForResult(i, RequestCode_PickPhoto);
		}
		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getContext(), "无法打开图库,请确认安装了图库浏览程序", Toast.LENGTH_LONG).show();
		}
	}

	private void onActionTakePicture() {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File out = new File(getContext().getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
			takePickureUri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, takePickureUri);
			((Activity) getContext()).startActivityForResult(intent, RequestCode_TakePhoto);
		}
		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getContext(), "无法打开相机", Toast.LENGTH_LONG).show();
		}
	}

	public boolean handleForRequestCode(int requestCode) {
		return (RequestCode_TakePhoto == requestCode || requestCode == RequestCode_PickPhoto);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == RequestCode_TakePhoto) && (resultCode == Activity.RESULT_OK)) {
			if (takePickureUri != null) {
				Log.i(TAG, "拍照的照片路径 " + takePickureUri.getPath());
				String picturePath = takePickureUri.getPath();
				addImage(picturePath);
			}
			takePickureUri = null;
		}
		if ((requestCode == RequestCode_PickPhoto) && (resultCode == Activity.RESULT_OK)) {
			if (data != null) {
				String picturePath = HGUtils.parseFilePath(getContext(), data.getData());
				if (!TextUtils.isEmpty(picturePath)) {
					addImage(picturePath);
				} else {
					Toast.makeText(getContext(), "无法读取照片", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

}
