package com.gmail.hasszhao.mininews;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.gmail.hasszhao.mininews.fragments.NewsListFragment;


public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showNewsListFragment();
	}


	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }
	private void showNewsListFragment() {
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		NewsListFragment fmg = NewsListFragment.newInstance(this);
		// if (fmg instanceof OnBackStackChangedListener) {
		// mFragmentManager.addOnBackStackChangedListener((OnBackStackChangedListener)
		// fmg);
		// }
		// String tag = String.valueOf(fmg.hashCode());
		trans.replace(R.id.container_news_list, fmg, NewsListFragment.TAG);
		// trans.addToBackStack(NewsListFragment.TAG);
		trans.commit();
	}
}
