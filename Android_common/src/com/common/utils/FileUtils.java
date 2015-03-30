package com.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtils {
	/**
	 * sd卡的根目录
	 */
	private static String		mSdRootPath						= Environment.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录
	 */
	private static String		mDataRootPath					= null;
	/**
	 * 保存Image的目录名
	 */
	private final static String	FOLDER_NAME						= "/AndroidImage";
	private static final String	TAG								= "FileUtil";
	public final static int		ONE_KB							= 1024;
	public final static int		ONE_MB							= ONE_KB * 1024;
	public final static int		CACHE_SIZE						= 5 * ONE_MB;
	public final static int		FREE_SD_SPACE_NEEDED_TO_CACHE	= CACHE_SIZE;
	public final static String	CACHDIR							= "data/ceapp/cache/imgcache";
	public final static String	WHOLESALE_CONV					= ".cach";

	@SuppressWarnings("deprecation")
	public static File UriToFile(Uri uri, Activity activity) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		return new File(img_path);
	}

	public static String readFromAssets(String fileName, Context context) {
		String Result = "";
		try {
			InputStream inStream = context.getAssets().open(fileName);
			int size = inStream.available();
			byte[] buffer = new byte[size];
			inStream.read(buffer);

			inStream.close();
			Result = new String(buffer).replaceAll("\r", "");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}

	public static String getExtension(String filename) {
		String result = "";
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > -1) && (i < (filename.length() - 1))) {
				result = filename.substring(i + 1);
			}
		}
		if (result.length() > 0) {
			result = "." + result;
		}
		return result;
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static int freeSpaceOnSdCard() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		@SuppressWarnings("deprecation")
		double sdFree = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize());
		return (int) sdFree;
	}

	public static void updateFileTime(String dir, String fileName) {

		File file = new File(dir, fileName);
		long newModifiedTime = System.currentTimeMillis();

		file.setLastModified(newModifiedTime);

	}

	public static void removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		long dirSize = 0;

		for (int i = 0; i < files.length; i++) {
			// if (files[i].getName().contains(Constant.WHOLESALE_CONV))
			{
				dirSize += files[i].length();
			}
		}

		int i = 0;
		while (dirSize > CACHE_SIZE || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSdCard()) {
			dirSize = dirSize - files[i].length();
			files[i].delete();
			i++;
		}
	}

	public static class FileLastModifSort implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	public static String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + WHOLESALE_CONV;
	}

	public static String getCachDirectory() {
		String dir = getSDPath() + "/" + CACHDIR;
		String substr = dir.substring(0, 4);
		if (substr.equals("/mnt")) {
			dir = dir.replace("/mnt", "");
		}
		if (!(new File(dir)).exists()) {
			(new File(dir)).mkdirs();
		}
		return dir;
	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();// 获取根目录
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}

	public File creatSDDir(String dir) {
		File dirFile = new File(getSDPath() + dir + File.separator);
		System.out.println(dirFile.mkdirs());
		return dirFile;
	}

//	public static void saveBmpToSd(Bitmap bm, String filename) {
//
//		if (bm == null) {
//			Log.i(TAG, " 图片对象为null");
//			return;
//		}
//
//		if (Constant.FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSdCard()) {
//			Log.i(TAG, "SD卡空间不足！");
//			return;
//		}
//
//		File file = new File(getCachDirectory() + "/" + filename);
//
//		try {
//			if (file.exists()) {
//				file.delete();
//			}
//			{
//				file.createNewFile();
//				OutputStream outStream = new FileOutputStream(file);
//				bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//				outStream.flush();
//				outStream.close();
//				// Log.i(Constant.TAG_UTILS_FILE, "图片保存到SD卡成功！");
//			}
//		}
//		catch (Exception e) {
//			Log.e(TAG, e.getMessage());
//		}
//	}

	public static String readFileData(String fileName, Context context, String encoding) {
		String res = "";
		try {
			FileInputStream fin = context.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, encoding);
			fin.close();
		}
		catch (Exception e) {}
		return res;
	}

	public static void writeFileData(String fileName, String message, String encoding, Context context) {
		try {
			@SuppressWarnings("static-access")
			FileOutputStream fout = context.openFileOutput(fileName, context.MODE_APPEND);
			byte[] bytes = EncodingUtils.getBytes(message, encoding);
			fout.write(bytes);
			fout.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String	SDPATH; //用于存sd card的文件的路径

	public String getSDPATH() {
		return SDPATH;
	}

	public void setSDPATH(String sDPATH) {
		SDPATH = sDPATH;
	}

	/**
	 * 构造方法
	 * 获取SD卡路径
	 */
	public FileUtils() {
		//获得当前外部存储设备的目录
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		System.out.println("sd card's directory path:" + SDPATH);
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		Log.v("sdpath", "========" + SDPATH + fileName);

		try {
			file.createNewFile();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * 在SD卡上创建目录
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		System.out.println("storage device's state :" + Environment.getExternalStorageState());

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			dir.mkdirs();
		}
		return dir;
	}

	public static boolean makeDirs(String filePath) {
		String folderName = getFolderName(filePath);
		if (StringUtils.isEmpty(folderName)) {
			return false;
		}

		File folder = new File(folderName);
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	public static String getFolderName(String filePath) {

		if (StringUtils.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	public static StringBuffer readTxtFromSD(String path, long id) {
		StringBuffer sb = new StringBuffer();
		try {

			File file = new File(path + id + ".txt");
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				int c;
				while ((c = fis.read()) != -1) {
					sb.append((char) c);
				}
				fis.close();
			}

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * 将一个inputSteam里面的数据写入到SD卡中
	 * public File write2SDFromInput(MainApplication application,int length,String path,String fileName,InputStream
	 * inputStream){
	 * File file=null;
	 * OutputStream output=null;
	 * try {
	 * //创建SD卡上的目录
	 * File tempf=createSDDir(path);
	 * file=createSDFile(path+fileName);
	 * if(file.exists()){
	 * output=new FileOutputStream(file);
	 * byte buffer[]=new byte[1024];
	 * int len=-1;
	 * int downSize=0;
	 * while((len=(inputStream.read(buffer))) !=-1){
	 * output.write(buffer, 0, len);
	 * Log.v("len", "=="+len);
	 * downSize+=len;
	 * int progreeValue=(int) (((float) downSize /length) * 100);
	 * Log.v("progreeValue", "=="+progreeValue);
	 * if(progreeValue<0){
	 * application.setProgress(0);
	 * }else{
	 * application.setProgress((int) (((float) downSize /length) * 100));
	 * }
	 * Message message = application.getCurrentHandler().obtainMessage(MessageWhat.DOWN_UPDATE, "");
	 * application.getCurrentHandler().sendMessage(message);
	 * }
	 * if(downSize==length){
	 * Message message_over = application.getCurrentHandler().obtainMessage(MessageWhat.DOWN_OVER, "");
	 * application.getCurrentHandler().sendMessage(message_over);
	 * }else if(downSize<length){
	 * file.delete();
	 * Message message_over = application.getCurrentHandler().obtainMessage(MessageWhat.DOWNFIELD, "");
	 * application.getCurrentHandler().sendMessage(message_over);
	 * }
	 * output.flush();
	 * }
	 * } catch (FileNotFoundException e) {
	 * e.printStackTrace();
	 * } catch (IOException e) {
	 * e.printStackTrace();
	 * }finally{
	 * try {
	 * output.close();
	 * } catch (IOException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 * return file;
	 * }
	 */

	public FileUtils(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	/**
	 * 获取储存Image的目录
	 * 
	 * @return
	 */
	private String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME
				: mDataRootPath + FOLDER_NAME;
	}

	/**
	 * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * 
	 * @param fileName
	 * @param bitmap
	 * @throws IOException
	 */
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

//	/**
//	 * 从手机或者sd卡获取Bitmap
//	 * 
//	 * @param fileName
//	 * @return
//	 */
//	public Bitmap getBitmap(String fileName) {
//		BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inPreferredConfig = Bitmap.Config.RGB_565;//图像解码类型表示16位位图 565代表对应三原色占的位数
//		opt.inInputShareable = true;
//		opt.inSampleSize = ImageUtil.computeSampleSize(opt, -1, 128 * 128);
//		opt.inPurgeable = true;//设置图片可以被回收
//		return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName, opt);
////			return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
//	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).exists();
	}

	/**
	 * 获取文件的大小
	 * 
	 * @param fileName
	 * @return
	 */
	public long getFileSize(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).length();
	}

	/**
	 * 删除SD卡或者手机的缓存图片和目录
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}

		dirFile.delete();
	}

	public static void writeImage(Bitmap bitmap, String destPath, int quality) {
		try {
			FileUtils.deleteFile(destPath);
			if (FileUtils.createFile(destPath)) {
				FileOutputStream out = new FileOutputStream(destPath);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
					out.flush();
					out.close();
					out = null;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除一个文件
	 * 
	 * @param filePath
	 *            要删除的文件路径名
	 * @return true if this file was deleted, false otherwise
	 */
	public static boolean deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				return file.delete();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建一个文件，创建成功返回true
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean createFile(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				return file.createNewFile();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 删除一个文件夹
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}
}
