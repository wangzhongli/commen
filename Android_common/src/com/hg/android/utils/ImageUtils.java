package com.hg.android.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtils {
	private static final String	TAG	= "ImageUtils";

	public static boolean scaleImage(String srcJPG, String destJPG, int minSideLength, int maxNumOfPixels) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcJPG, options);
		if (!thumbBitmap(srcJPG, destJPG, options, minSideLength, maxNumOfPixels)) {
			try {
				FileUtils.copy(srcJPG, destJPG);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static boolean saveBitmap(Bitmap bmp, String desFile) {
		try {
			File file = new File(desFile);
			file.delete();
			FileOutputStream out = new FileOutputStream(file);
			if (bmp.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
				out.flush();
				out.close();
				Log.i(TAG, "保存的照片路径 " + desFile);
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean thumbBitmap(String srcJPG, String destJPG, BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		int scale = ThumbnailUtils.computeSampleSize(options, minSideLength, maxNumOfPixels);
		if (scale > 1) {
			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(srcJPG, options);
			if (bitmap != null) {
				saveBitmap(bitmap, destJPG);
				options.outHeight = bitmap.getHeight();
				options.outWidth = bitmap.getWidth();
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
				bitmap = null;
				return true;
			}
		}
		return false;
	}
}
