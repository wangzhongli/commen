package com.hg.android.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ThreeParty.R;
import com.common.utils.FileUtils;
import com.common.utils.MLog;
import com.hg.android.widget.CropImageView;

/**
 * 裁剪图片
 * 
 * @author jiajia
 */
public class CropImageActivity extends Activity {
	private Button				okBtn;
	private Button				cancleBtn;
	private CropImageView		cropImageView;
	public static final int		PICTURE_CORP_RESULT	= 300;
	public static final String	LOCAL_FILE			= FileUtils.getSDPath() + File.separator + "e1858" + File.separator
															+ "head" + File.separator + "temp" + File.separator;
	private String				bitmap_name			= "e1858";
	private Bitmap				bitmap;
	public int					cropWidth			= 0;
	public int					cropHeight			= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_ablum);
		if (getIntent() != null) {
			bitmap_name = getIntent().getStringExtra("bitmap_name");
			cropWidth = getIntent().getIntExtra("cropWidth", 300);
			cropHeight = getIntent().getIntExtra("cropHeight", 300);
		}
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		okBtn = (Button) findViewById(R.id.ok_btn);
		cancleBtn = (Button) findViewById(R.id.cancel_btn);
		cropImageView = (CropImageView) findViewById(R.id.cropAblum);
		try {
			int degree = readPictureDegree(getRealPathFromURI((Uri) getIntent().getParcelableExtra("bitmap")));
			bitmap = getThumbnail((Uri) getIntent().getParcelableExtra("bitmap"), 760);
			if (degree > 0) {

				Matrix matrix = new Matrix();
				matrix.postRotate(degree);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		cropImageView.setDrawable(new BitmapDrawable(bitmap), cropWidth, cropHeight);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FileUtils.writeImage(cropImageView.getCropImage(), LOCAL_FILE + bitmap_name, 100);
				Intent mIntent = new Intent();
				mIntent.putExtra("cropImagePath", LOCAL_FILE + bitmap_name);
				setResult(PICTURE_CORP_RESULT, mIntent);
				finish();

			}
		});
		cancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (bitmap != null)
			bitmap.recycle();
		MLog.e("TAG", "newBitmap.recycle");
		super.onDestroy();
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		catch (Exception e) {
			return contentUri.getPath();
		}
	}

	public Bitmap getThumbnail(Uri uri, int size) throws FileNotFoundException, IOException {
		InputStream input = this.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;//optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;
		double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;//optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		input = this.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

}
