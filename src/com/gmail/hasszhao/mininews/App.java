package com.gmail.hasszhao.mininews;

import android.app.Application;

import com.gmail.hasszhao.mininews.tasks.TaskHelper;


public final class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		TaskHelper.init(this);
	}
}
