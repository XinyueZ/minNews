package com.gmail.hasszhao.mininews;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class App extends Application {

	private final Map<String, ListNews> mNewsLists = new HashMap<String, ListNews>();


	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		TaskHelper.init(getApplicationContext());
		Prefs.createInstance(getApplicationContext());
	}


	public void addListNews(String _langauge, ListNews _newsList) {
		mNewsLists.put(_langauge, _newsList);
	}


	public ListNews getListNews(String _langauge) {
		return mNewsLists.get(_langauge);
	}
}
