package com.hg.android.ormlite.extra;

import java.sql.SQLException;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;

public abstract class OrmLiteIteratorAdapter<T> extends BaseAdapter {
	protected AndroidDatabaseResults	mDBResults;
	protected CloseableIterator<T>		mIterator;
	protected Context					mContext;
	protected int						mCount	= 0;

	public OrmLiteIteratorAdapter(Context context, CloseableIterator<T> iterator) {
		mContext = context;
		changeIterator(iterator);
	}

	public OrmLiteIteratorAdapter(Context context) {
		this(context, null);
	}

	public CloseableIterator<T> getIterator() {
		return mIterator;
	}

	/**
	 * @see android.widget.ListAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if (isDataValid()) {
			return mCount;
		} else {
			return 0;
		}
	}

	private boolean isDataValid() {
		return (mIterator != null) && (mDBResults != null);
	}

	/**
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		if (isDataValid()) {
			try {
				if (mDBResults.moveAbsolute(position)) {
					return mIterator.current();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	@Deprecated
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (!isDataValid()) {
			throw new IllegalStateException("this should only be called when the iterator is valid");
		}
		T entity = getItem(position);
		if (entity == null) {
			throw new IllegalStateException("couldn't move iterator to position " + position);
		}
		View v;
		if (convertView == null) {
			v = newView(mContext, entity, parent);
		} else {
			v = convertView;
		}
		bindView(v, mContext, entity, position);
		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (isDataValid()) {
			T entity = getItem(position);
			if (entity == null) {
				throw new IllegalStateException("couldn't move iterator to position " + position);
			}
			View v;
			if (convertView == null) {
				v = newDropDownView(mContext, entity, parent);
			} else {
				v = convertView;
			}
			bindView(v, mContext, entity, position);
			return v;
		} else {
			return null;
		}
	}

	public abstract View newView(Context context, T entity, ViewGroup parent);

	public View newDropDownView(Context context, T entity, ViewGroup parent) {
		return newView(context, entity, parent);
	}

	public abstract void bindView(View view, Context context, T entity, int position);

	public void changeIterator(CloseableIterator<T> iterator) {
		CloseableIterator<T> old = swapIterator(iterator);
		if (old != null) {
			old.closeQuietly();
		}
	}

	public CloseableIterator<T> swapIterator(CloseableIterator<T> newIterator) {
		if (newIterator == mIterator) {
			return null;
		}
		CloseableIterator<T> oldIterator = mIterator;

		mCount = 0;
		mDBResults = null;
		mIterator = newIterator;
		if (mIterator != null) {
			mDBResults = (AndroidDatabaseResults) mIterator.getRawResults();
			try {
				/**
				 * 让mIterator的first属性为false,无语
				 */
				if (mIterator.hasNext()) {
					mIterator.current();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (mDBResults != null) {
				mCount = mDBResults.getCount();
			}
			notifyDataSetChanged();
		} else {
			notifyDataSetInvalidated();
		}
		return oldIterator;
	}
}
