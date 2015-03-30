package com.hg.android.entitycache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.text.TextUtils;

import com.hg.android.ormlite.extra.OrmLiteNotificationCenter;
import com.hg.android.utils.HGUtils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

public class EntityCacheHelper {
	public static EntityCacheHelper						sInstance;
	private Context										context;
	private Class<? extends OrmLiteSqliteOpenHelper>	helperClass;

	public static void initInstance(Context appcontext, Class<? extends OrmLiteSqliteOpenHelper> helperClass) {
		sInstance = new EntityCacheHelper();
		sInstance.context = appcontext;
		sInstance.helperClass = helperClass;
	}

	public static EntityCacheHelper sharedInstance() {
		if (sInstance == null) {
			throw new RuntimeException("you must initInstance before you call sharedInstance");
		}
		return sInstance;
	}

	public <BeanType, EntityType extends CacheEntityWithSpecifiedId<BeanType>> void deleteEntityByID(String ID,
			Class<EntityType> klass) {
		OrmLiteSqliteOpenHelper openHelper = OpenHelperManager.getHelper(context, helperClass);
		try {
			Dao<EntityType, String> dao = openHelper.getDao(klass);
			dao.deleteById(ID);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		OpenHelperManager.releaseHelper();
		OrmLiteNotificationCenter.sharedInstance().notifyChange(klass);
	}

	public <BeanType, EntityType extends CacheEntityWithSpecifiedId<BeanType>> void deleteAllEntities(
			Class<EntityType> klass) {
		OrmLiteSqliteOpenHelper openHelper = OpenHelperManager.getHelper(context, helperClass);
		try {
			Dao<EntityType, String> dao = openHelper.getDao(klass);
			dao.deleteBuilder().delete();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		OpenHelperManager.releaseHelper();
		OrmLiteNotificationCenter.sharedInstance().notifyChange(klass);
	}

	public <BeanType, EntityType extends BaseCacheEntity<BeanType>> void saveCacheEntity(Class<EntityType> klass,
			BeanType bean) {
		saveCacheEntity(klass, bean, null);
	}

	public <BeanType, EntityType extends BaseCacheEntity<BeanType>> void saveCacheEntity(Class<EntityType> klass,
			BeanType bean, String tag) {
		List<BeanType> list = new ArrayList<BeanType>();
		list.add(bean);
		saveCacheEntities(klass, list, 1, tag);
	}

	public <BeanType, EntityType extends BaseCacheEntity<BeanType>> void saveCacheEntities(Class<EntityType> klass,
			List<BeanType> beans, int offset) {
		saveCacheEntities(klass, beans, offset, null);
	}

	public <BeanType, EntityType extends BaseCacheEntity<BeanType>> void saveCacheEntities(
			final Class<EntityType> klass, final List<BeanType> beans, final int offset, final String tag) {

		OrmLiteSqliteOpenHelper openHelper = OpenHelperManager.getHelper(context, helperClass);
		try {
			final Dao<EntityType, ?> dao = openHelper.getDao(klass);
			if (offset == 0) {
				DeleteBuilder<EntityType, ?> builder = dao.deleteBuilder();
				if (!TextUtils.isEmpty(tag)) {
					builder.where().eq("tag", tag);
				}
				builder.delete();
			}

			if (!HGUtils.isListEmpty(beans)) {
				Callable<Void> callable = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						int sortIndex = offset;
						for (BeanType bean : beans) {
							EntityType entity = klass.newInstance();
							entity.setBean(bean);
							entity.setTag(tag);
							entity.setSortIndex(sortIndex++);
							dao.createOrUpdate(entity);
						}
						return null;
					}
				};
				if (beans.size() > 1) {
					dao.callBatchTasks(callable);
				} else {
					callable.call();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		OpenHelperManager.releaseHelper();
		OrmLiteNotificationCenter.sharedInstance().notifyChange(klass);
	}
}
