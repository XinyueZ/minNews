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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.hasszhao.mininews.fragments.NewsListFragment;
import com.gmail.hasszhao.mininews.utils.Prefs;


public class MainActivity extends SherlockFragmentActivity implements OnCheckedChangeListener, OnSeekBarChangeListener {

	private static final int LAYOUT = R.layout.activity_main;
	private static final int MIN_NEWS_SIZE = 10;
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
		initNewsSizeSeekbar();
		initLangaugePreSelections();
	}


	private void initNewsSizeSeekbar() {
		SeekBar sb = (SeekBar) findViewById(R.id.sb_news_count);
		sb.setProgress(Prefs.getInstance().getNewsSize());
		sb.setOnSeekBarChangeListener(this);
		((TextView) findViewById(R.id.tv_news_size)).setText(String.format(getString(R.string.title_news_size),
				sb.getProgress()));
	}


	private void initLangaugePreSelections() {
		de.ankri.views.Switch sw = null;
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_english)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportEnglish());
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_chinese)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportChinese());
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_german)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportGerman());
	}


	@Override
	public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser) {
		if (_progress < MIN_NEWS_SIZE) {
			_seekBar.setProgress(MIN_NEWS_SIZE);
		}
		((TextView) findViewById(R.id.tv_news_size)).setText(String.format(getString(R.string.title_news_size),
				_seekBar.getProgress()));
		Prefs.getInstance().setNewsSize(_seekBar.getProgress());
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


	public void refreshingOn() {
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
			case R.id.switch_english:
				((de.ankri.views.Switch) findViewById(R.id.switch_english)).setChecked(_isChecked);
				break;
			case R.id.switch_chinese:
				((de.ankri.views.Switch) findViewById(R.id.switch_chinese)).setChecked(_isChecked);
				break;
			case R.id.switch_german:
				((de.ankri.views.Switch) findViewById(R.id.switch_german)).setChecked(_isChecked);
				break;
			default:
				break;
		}
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


	@Override
	public void onStartTrackingTouch(SeekBar _seekBar) {
	}


	@Override
	public void onStopTrackingTouch(SeekBar _seekBar) {
	}
}
