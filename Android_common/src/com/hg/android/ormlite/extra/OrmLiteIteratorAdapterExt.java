package com.hg.android.ormlite.extra;

import java.sql.SQLException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.hg.android.ormlite.extra.OrmLiteIteratorLoader.QuerySource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.QueryBuilder;

public abstract class OrmLiteIteratorAdapterExt<T> extends OrmLiteIteratorAdapter<T> {

	public static int	LoaderID	= 65432176;
	LoaderManager		loaderManager;

	public OrmLiteIteratorAdapterExt(Context context) {
		super(context);
	}

	public void load(LoaderManager loaderManager, final Class<? extends OrmLiteSqliteOpenHelper> openHelperClass,
			final Class<T> entityClass) {
		this.loaderManager = loaderManager;
		final QuerySource<T> querySource = new QuerySource<T>() {
			@Override
			public void query(QueryBuilder<T, ?> queryBuilder) throws Exception {
				buildQueryBuilder(queryBuilder);
			}
		};
		LoaderCallbacks<?> callbacks = new LoaderCallbacks<CloseableIterator<T>>() {
			@Override
			public Loader<CloseableIterator<T>> onCreateLoader(int id, Bundle args) {
				return new OrmLiteIteratorLoader<T>(mContext, openHelperClass, querySource, entityClass);
			}

			@Override
			public void onLoadFinished(Loader<CloseableIterator<T>> loader, CloseableIterator<T> data) {
				changeIterator(data);
			}

			@Override
			public void onLoaderReset(Loader<CloseableIterator<T>> loader) {
				changeIterator(null);
			}
		};
		loaderManager.initLoader(LoaderID, null, callbacks);
	}

	public void unload() {
		changeIterator(null);
		loaderManager.destroyLoader(LoaderID);
	}

	public abstract void buildQueryBuilder(QueryBuilder<T, ?> queryBuilder) throws SQLException;

}
