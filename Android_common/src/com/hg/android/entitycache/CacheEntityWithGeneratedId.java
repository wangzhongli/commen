package com.hg.android.entitycache;

import com.j256.ormlite.field.DatabaseField;

@SuppressWarnings("serial")
public abstract class CacheEntityWithGeneratedId<BeanType> extends BaseCacheEntity<BeanType> {

	@DatabaseField(columnName = "ID", generatedId = true)
	public int	ID;
}
