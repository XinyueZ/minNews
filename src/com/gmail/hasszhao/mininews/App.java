package com.gmail.hasszhao.mininews;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Application;

import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.db.AppDatabase;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class App extends Application {

	private final Map<String, ListNews> mNewsLists = new HashMap<String, ListNews>();
	private AppDatabase mAppDB;


	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		TaskHelper.init(getApplicationContext());
		Prefs.createInstance(getApplicationContext());
		mAppDB = new AppDatabase(this);
	}


	public void addListNews(String _langauge, ListNews _newsList) {
		mNewsLists.put(_langauge, _newsList);
	}


	public ListNews getListNews(String _langauge) {
		return mNewsLists.get(_langauge);
	}


	public boolean hasSomePayloaded() {
		Set<String> keys = mNewsLists.keySet();
		if (keys == null || keys.size() == 0) {
			return false;
		} else {
			for (String key : keys) {
				if (mNewsLists.get(key) != null) {
					return true;
				}
			}
			return false;
		}
	}


	public synchronized AppDatabase getAppDB() {
		return mAppDB;
	}
}
