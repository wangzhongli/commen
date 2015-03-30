package com.hg.android.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hg.android.widget.CityDBManager.AreaEntity;

public class CityPickDialog extends AlertDialog {

	enum State {
		Province, City, District
	}

	enum SelectMode {
		City, District, MultiDistrict
	}

	OnPickedListener		onPickedListener;
	OnMultiPickedListener	onMultiPickedListener;
	OnCityPickedListener	onCityPickedListener;

	ListView				listView;
	State					state;

	List<AreaEntity>		provinces;
	AreaEntity				selectedProvince, selectCity;
	CityDBManager			cityDBManager;

	private CityPickDialog(Context context) {
		super(context);
	}

	private CityPickDialog(Context context, SelectMode mode) {
		this(context);

		cityDBManager = CityDBManager.sharedInstance(context);
		cityDBManager.openDatabase();

		listView = new ListView(context);
		listView.setBackgroundColor(Color.WHITE);
		listView.setOnItemClickListener(createOnItemClickListener(mode));
		setView(listView);
		setState(State.Province);

		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				cityDBManager.closeDatabase();
			}
		});
	}

	public CityPickDialog(Context context, OnPickedListener onPickedListener) {
		this(context, SelectMode.District);
		this.onPickedListener = onPickedListener;
	}

	public CityPickDialog(Context context, OnMultiPickedListener onMultiPickedListener) {
		this(context, SelectMode.MultiDistrict);
		this.onMultiPickedListener = onMultiPickedListener;
	}

	public CityPickDialog(Context context, OnCityPickedListener onCityPickedListener) {
		this(context, SelectMode.City);
		this.onCityPickedListener = onCityPickedListener;
	}

	OnItemClickListener createOnItemClickListener(final SelectMode mode) {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (state) {
				case Province:
					selectedProvince = (AreaEntity) parent.getAdapter().getItem(position);
					setState(State.City);
					break;
				case City:
					selectCity = (AreaEntity) parent.getAdapter().getItem(position);
					switch (mode) {
					case City:
						dismiss();
						if (onCityPickedListener != null) {
							onCityPickedListener.onPicked(selectedProvince, selectCity, selectedProvince.getName()
									+ selectCity.getName());
						}
						break;
					case District:
						setState(State.District);
						break;
					case MultiDistrict:
						dismiss();
						List<AreaEntity> districts = cityDBManager.districtsForCity(selectCity.getCode());
						showMultiSelectDialog(selectedProvince, selectCity, districts, onMultiPickedListener,
								getContext());
						break;
					}
					break;
				case District:
					AreaEntity selectedDistrict = (AreaEntity) parent.getAdapter().getItem(position);
					if (onPickedListener != null) {
						String text = selectedProvince.getName() + selectCity.getName() + selectedDistrict.getName();
						onPickedListener.onPicked(selectedProvince, selectCity, selectedDistrict, text);
					}
					dismiss();
					break;
				}
			}
		};
	}

	void setState(State state) {
		this.state = state;
		if (provinces == null) {
			provinces = cityDBManager.allProvinces();
		}
		switch (state) {
		case Province:
			setTitle("选择省份");
			listView.setAdapter(new ArrayAdapter<AreaEntity>(getContext(), android.R.layout.simple_list_item_1,
					provinces));
			break;
		case City:
			setTitle(selectedProvince.getName());
			List<AreaEntity> cities = cityDBManager.citiesForProvince(selectedProvince.getCode());
			listView.setAdapter(new ArrayAdapter<AreaEntity>(getContext(), android.R.layout.simple_list_item_1, cities));
			break;
		case District:
			setTitle(selectCity.getName());
			List<AreaEntity> districts = cityDBManager.districtsForCity(selectCity.getCode());
			listView.setAdapter(new ArrayAdapter<AreaEntity>(getContext(), android.R.layout.simple_list_item_1,
					districts));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		switch (state) {
		case Province:
			dismiss();
			break;
		case City:
			setState(State.Province);
			break;
		case District:
			setState(State.City);
			break;
		}
	}

	public interface OnCityPickedListener {
		public void onPicked(AreaEntity province, AreaEntity city, String text);
	}

	public interface OnPickedListener {
		public void onPicked(AreaEntity province, AreaEntity city, AreaEntity district, String text);
	}

	public interface OnMultiPickedListener {
		public void onMultiPicked(AreaEntity province, AreaEntity city, List<AreaEntity> district);
	}

	static void showMultiSelectDialog(final AreaEntity province, final AreaEntity city,
			final List<AreaEntity> districts, final OnMultiPickedListener onMultiPickedListener, Context context) {

		final List<AreaEntity> selectedDistricts = new ArrayList<AreaEntity>();
		OnMultiChoiceClickListener listener = new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				AreaEntity district = districts.get(which);
				if (!isChecked) {
					selectedDistricts.remove(district);
				} else if (!selectedDistricts.contains(district)) {
					selectedDistricts.add(district);
				}
			}
		};
		CharSequence[] items = new CharSequence[districts.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = districts.get(i).getName();
		}
		new AlertDialog.Builder(context).setTitle(city.getName()).setMultiChoiceItems(items, null, listener)
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (onMultiPickedListener != null) {
							onMultiPickedListener.onMultiPicked(province, city, selectedDistricts);
						}
					}
				}).setNegativeButton("取消", null).show();

	}
}
