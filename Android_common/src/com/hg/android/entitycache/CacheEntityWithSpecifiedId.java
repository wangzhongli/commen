package com.hg.android.entitycache;

import com.j256.ormlite.field.DatabaseField;

@SuppressWarnings("serial")
public abstract class CacheEntityWithSpecifiedId<BeanType> extends BaseCacheEntity<BeanType> {
	@DatabaseField(id = true, columnName = "ID")
	public String	ID;

	@Override
	public void setBean(BeanType bean) {
		super.setBean(bean);
		setID(generateID(bean));
	}

	protected abstract String generateID(BeanType bean);

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
}
