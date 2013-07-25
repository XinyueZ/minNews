package com.gmail.hasszhao.mininews;

import android.app.Application;

import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class App extends Application {

	private final Map<String, ListNews> mNewsLists = new HashMap<String, ListNews>();
	private AppDB mAppDB;


	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		TaskHelper.init(getApplicationContext());
		Prefs.createInstance(getApplicationContext());
		mAppDB = new AppDB(this);
	}


	public List<DONews> getBookmarkedNews() {
		List<DONews> ret = new ArrayList<DONews>();
		Set<String> keys = mNewsLists.keySet();
		if (keys != null) {
			ListNews ln = null;
			List<DONews> ldo = null;
			for (String key : keys) {
				ln = mNewsLists.get(key);
				if (ln != null) {
					ldo = ln.getPulledNewss();
					for (DONews n : ldo) {
						if (n.isBookmarked()) {
							ret.add(n);
						}
					}
				}
			}
		}
		return ret;
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


	public synchronized AppDB getAppDB() {
		return mAppDB;
	}
}
