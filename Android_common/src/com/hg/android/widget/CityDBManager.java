package com.hg.android.widget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.ThreeParty.R;
import com.hg.android.utils.HGUtils;

/**
 * 省市区数据管理
 */
public class CityDBManager {
	private final int			BUFFER_SIZE	= 1024;
	public static final String	DB_NAME		= "city_new.db";
	private String				DBFile		= "";
	private SQLiteDatabase		database;
	private Context				context;

	private AtomicInteger		openCount;

	public static CityDBManager	sInstance;

	public CityDBManager(Context context) {
		this.context = context;
		DBFile = new File(context.getFilesDir(), DB_NAME).getAbsolutePath();
		openCount = new AtomicInteger();
	}

	public static CityDBManager sharedInstance(Context context) {
		if (sInstance == null) {
			sInstance = new CityDBManager(context.getApplicationContext());
		}
		return sInstance;
	}

	public SQLiteDatabase openDatabase() {
		openCount.getAndIncrement();
		if (database == null) {
			openDatabase(DBFile);
		}
		return database;
	}

	public void closeDatabase() {
		if (openCount.decrementAndGet() == 0) {
			if (database != null) {
				database.close();
			}
			database = null;
		}
	}

	public SQLiteDatabase getDatabase() {
		return this.database;
	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			File file = new File(dbfile);
			if (!file.exists()) {
				InputStream is = context.getResources().openRawResource(R.raw.city_new);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
					fos.flush();
				}
				fos.close();
				is.close();
			}

			if (database != null) {
				database.close();
			}
			database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return database;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return SQLiteDatabase.openOrCreateDatabase("", null);
	}

	private List<AreaEntity> rawQuery(String sql) {
		List<AreaEntity> areas = new ArrayList<AreaEntity>();
		try {
			Cursor cursor = getDatabase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				AreaEntity area = new AreaEntity();
				area.setName(cursor.getString(cursor.getColumnIndex("Name")));
				area.setCode(cursor.getString(cursor.getColumnIndex("Code")));
				areas.add(area);
			}
			cursor.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return areas;
	}

	public List<AreaEntity> allProvinces() {
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select * from Sys_Area u where u.Code=u.Parent";
		List<AreaEntity> areas = rawQuery(sql);
		if (opened) {
			closeDatabase();
		}
		return areas;
	}

	public List<AreaEntity> citiesForProvince(String provinceCode) {
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select * from Sys_Area u where u.Parent!=u.Code and u.Parent=" + provinceCode;
		List<AreaEntity> areas = rawQuery(sql);
		if (opened) {
			closeDatabase();
		}
		return areas;
	}

	public List<AreaEntity> districtsForCity(String cityCode) {
		return citiesForProvince(cityCode);
	}

	public List<String> districtsForCityName(String cityName, String provinceName) {
		List<String> districts = new ArrayList<String>();
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select * from Sys_Area u where u.Parent!=u.Code and u.Parent="
				+ "(select Code from Sys_Area u where  u.Name = '" + cityName
				+ "' and u.Parent ==(select Code from Sys_Area u where  u.Name='" + provinceName + "') )";
		try {
			Cursor cursor = getDatabase().rawQuery(sql, null);
			//cursor.moveToFirst();
			while (cursor.moveToNext()) {
				districts.add(cursor.getString(cursor.getColumnIndex("Name")));
			}
			cursor.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		if (opened) {
			closeDatabase();
		}
		return districts;
	}

	public AreaEntity areaForCode(String code) {
		if (TextUtils.isEmpty(code)) {
			return null;
		}
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select * from Sys_Area u where u.Code=" + code;
		List<AreaEntity> areas = rawQuery(sql);
		if (opened) {
			closeDatabase();
		}
		if (HGUtils.isListEmpty(areas)) {
			return null;
		}
		return areas.get(0);
	}

	public String codeForProvince(String provinceName) {
		String code = "";
		if (TextUtils.isEmpty(provinceName)) {
			return "0";
		}
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select * from Sys_Area u where  u.Code=u.Parent and u.Name='" + provinceName + "'";
		try {
			Cursor cursor = getDatabase().rawQuery(sql, null);
			cursor.moveToNext();
			code = cursor.getString(cursor.getColumnIndex("Code"));
			cursor.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (opened) {
			closeDatabase();
		}
		return code;
	}

	public String codeForDistrict(String provinceName, String cityName, String districtName) {
		String code = "";
		if (TextUtils.isEmpty(districtName)) {
			return "";
		}
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select Code from Sys_Area u where u.Parent!=u.Code and u.Name='" + districtName
				+ "' and u.parent = '" + codeForCity(provinceName, cityName) + "'";
		try {
			Cursor cursor = getDatabase().rawQuery(sql, null);
			cursor.moveToNext();
			code = cursor.getString(cursor.getColumnIndex("Code"));
			cursor.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (opened) {
			closeDatabase();
		}
		return code;
	}

	public String codeForCity(String provinceName, String cityName) {
		String code = "";
		if (TextUtils.isEmpty(cityName)) {
			return "410100";
		}
		boolean opened = false;
		if (getDatabase() == null) {
			openDatabase();
			opened = true;
		}
		String sql = "select Code from Sys_Area u where u.Parent!=u.Code and u.Name='" + cityName
				+ "' and u.parent = '" + codeForProvince(provinceName) + "'";
		try {
			Cursor cursor = getDatabase().rawQuery(sql, null);
			cursor.moveToNext();
			code = cursor.getString(cursor.getColumnIndex("Code"));
			cursor.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (opened) {
			closeDatabase();
		}
		return code;
	}

	/////
	@SuppressWarnings("serial")
	public static class AreaEntity implements Serializable {
		private String	Code;
		private String	Name;

		public String getCode() {
			return Code;
		}

		public void setCode(String Code) {
			this.Code = Code;
		}

		public String getName() {
			return Name;
		}

		public void setName(String Name) {
			this.Name = Name;
		}

		@Override
		public String toString() {
			return Name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((Code == null) ? 0 : Code.hashCode());
			result = prime * result + ((Name == null) ? 0 : Name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AreaEntity other = (AreaEntity) obj;
			if (Code == null) {
				if (other.Code != null)
					return false;
			} else if (!Code.equals(other.Code))
				return false;
			if (Name == null) {
				if (other.Name != null)
					return false;
			} else if (!Name.equals(other.Name))
				return false;
			return true;
		}

	}

}
