package com.gmail.hasszhao.mininews;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.hasszhao.mininews.fragments.NewsListFragment;
import com.gmail.hasszhao.mininews.utils.Prefs;


public class MainActivity extends SherlockFragmentActivity implements OnCheckedChangeListener {

	private static final int LAYOUT = R.layout.activity_main;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private ActionBarDrawerToggle mDrawerToggle;


	@Override
	protected void onDestroy() {
		mPullToRefreshAttacher = null;
		super.onDestroy();
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		initActionbar();
		mPullToRefreshAttacher = new PullToRefreshAttacher(this);
		showNewsListFragment();
		createSidebar();
		initPull2LoadSwitch();
	}


	private void initPull2LoadSwitch() {
		de.ankri.views.Switch sw = null;
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_pull_to_load)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportPullToLoad());
	}


	private void initActionbar() {
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
	}


	private static class DrawerListener implements DrawerLayout.DrawerListener {

		private final ActionBarDrawerToggle drawerToggle;


		public DrawerListener(ActionBarDrawerToggle _drawerToggle) {
			super();
			drawerToggle = _drawerToggle;
		}


		@Override
		public void onDrawerOpened(View drawerView) {
			drawerToggle.onDrawerOpened(drawerView);
		}


		@Override
		public void onDrawerClosed(View drawerView) {
			drawerToggle.onDrawerClosed(drawerView);
		}


		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
			drawerToggle.onDrawerSlide(drawerView, slideOffset);
		}


		@Override
		public void onDrawerStateChanged(int newState) {
			drawerToggle.onDrawerStateChanged(newState);
		}
	}


	private void createSidebar() {
		DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.sidebar);
		sidebar.setDrawerListener(new DrawerListener(mDrawerToggle = new ActionBarDrawerToggle(this, sidebar,
				R.drawable.ic_drawer, -1, -1)));
		sidebar.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	}


	public void refreshing() {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshing(true);
		}
	}


	public void refreshComplete() {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshComplete();
		}
	}


	public void setRefreshableView(View _view, OnRefreshListener _refreshListener) {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshableView(_view, _refreshListener);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		getSupportMenuInflater().inflate(R.menu.main, _menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem _item) {
		switch (_item.getItemId()) {
			case R.id.action_refresh:
				refreshNewsList();
				break;
		}
		return true;
	}


	@Override
	public void onCheckedChanged(CompoundButton _buttonView, boolean _isChecked) {
		switch (_buttonView.getId()) {
			case R.id.switch_pull_to_load:
				switchPullToLoadStatus(_isChecked);
				break;
			default:
				break;
		}
	}


	private void switchPullToLoadStatus(boolean _support) {
		Prefs.getInstance().setSupportPullToLoad(_support);
		refreshNewsList();
	}


	private void refreshNewsList() {
		Fragment f = getSupportFragmentManager().findFragmentByTag(NewsListFragment.TAG);
		if (f instanceof NewsListFragment) {
			((NewsListFragment) f).refreshData();
		}
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
