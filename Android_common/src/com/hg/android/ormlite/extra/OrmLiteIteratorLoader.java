package com.hg.android.ormlite.extra;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class OrmLiteIteratorLoader<T> extends AsyncTaskLoader<CloseableIterator<T>> {

	final ForceLoadContentObserver				mObserver;

	CloseableIterator<T>						mIterator;

	OrmLiteNotificationCenter					mNotificationCenter;

	Class<T>									mEntityClass;

	QuerySource<T>								mQuerySource;
	Class<? extends OrmLiteSqliteOpenHelper>	mOpenHelperClass;
	OrmLiteSqliteOpenHelper						mOpenHelper;

	public OrmLiteIteratorLoader(Context context, Class<? extends OrmLiteSqliteOpenHelper> openHelperClass,
			QuerySource<T> querySource, Class<T> entityClass) {
		super(context);
		mOpenHelperClass = openHelperClass;
		mEntityClass = entityClass;
		mQuerySource = querySource;
		mObserver = new ForceLoadContentObserver();
		mNotificationCenter = OrmLiteNotificationCenter.sharedInstance();
		mNotificationCenter.registerContentObserver(mEntityClass, mObserver);
	}

	/* Runs on a worker thread */
	@Override
	public CloseableIterator<T> loadInBackground() {

		try {
			if (mOpenHelper == null) {
				mOpenHelper = OpenHelperManager.getHelper(getContext(), mOpenHelperClass);
			}
			Dao<T, ?> postDao = mOpenHelper.getDao(mEntityClass);
			QueryBuilder<T, ?> queryBuilder = postDao.queryBuilder();
			mQuerySource.query(queryBuilder);
			CloseableIterator<T> iterator = queryBuilder.iterator();
			queryBuilder.reset();
			queryBuilder = null;
			return iterator;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(CloseableIterator<T> iterator) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (iterator != null) {
				iterator.closeQuietly();
			}
			return;
		}
		CloseableIterator<T> oldIterator = mIterator;
		mIterator = iterator;

		if (isStarted()) {
			super.deliverResult(iterator);
		}

		if ((oldIterator != null) && (oldIterator != iterator)) {
			oldIterator.closeQuietly();
		}
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is
	 * ready the callbacks will be called on the UI thread. If a previous load
	 * has been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (mIterator != null) {
			deliverResult(mIterator);
		}
		if (takeContentChanged() || (mIterator == null)) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(CloseableIterator<T> iterator) {
		if (iterator != null) {
			iterator.closeQuietly();
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		mNotificationCenter.unregisterContentObserver(mEntityClass, mObserver);
		mNotificationCenter = null;

		if (mOpenHelper != null) {
			OpenHelperManager.releaseHelper();
			mOpenHelper = null;
		}

		mQuerySource = null;
		mOpenHelperClass = null;

		// Ensure the loader is stopped
		onStopLoading();

		if (mIterator != null) {
			mIterator.closeQuietly();
		}
		mIterator = null;
		mEntityClass = null;
	}

	public synchronized void reQuery() {
		onContentChanged();
	}

	public interface QuerySource<Type> {

		public void query(QueryBuilder<Type, ?> queryBuilder) throws Exception;
	}

}
