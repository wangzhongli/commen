package com.hg.android.entitycache;

import java.io.Serializable;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;

@SuppressWarnings("serial")
public abstract class BaseCacheEntity<BeanType> implements Serializable {

	static Gson		gson	= new Gson();

	@DatabaseField(columnName = "tag")
	public String	tag;
	@DatabaseField(columnName = "sortIndex")
	public int		sortIndex;
	@DatabaseField(columnName = "json")
	public String	json;

	public BeanType	bean;

	public static Gson getGson() {
		return gson;
	}

	public static void setGson(Gson gson) {
		BaseCacheEntity.gson = gson;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public void setBean(BeanType bean) {
		this.bean = bean;
		setJson(gson.toJson(bean));
	}

	public BeanType getBean() {
		if (this.bean == null) {
			this.bean = gson.fromJson(json, getBeanClass());
		}
		return bean;
	}

	protected abstract Class<BeanType> getBeanClass();

}
