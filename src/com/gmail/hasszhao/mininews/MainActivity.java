package com.gmail.hasszhao.mininews;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.hasszhao.mininews.fragments.LoadingFragment;
import com.gmail.hasszhao.mininews.fragments.NewsDetailsFragment;
import com.gmail.hasszhao.mininews.fragments.NewsListFragment;
import com.gmail.hasszhao.mininews.fragments.NewsPagersFragment;
import com.gmail.hasszhao.mininews.fragments.SearchedNewsListFragment;
import com.gmail.hasszhao.mininews.interfaces.IRefreshable;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.utils.ShareUtil;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class MainActivity extends SherlockFragmentActivity implements OnCheckedChangeListener,
		OnSeekBarChangeListener, ISharable, OnBackStackChangedListener, OnEditorActionListener,
		DrawerLayout.DrawerListener {

	public static final String ACTION = "com.gmail.hasszhao.mininews.MainActivity";
	private static final String DLG_TAG = "dlg";
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
		// showNewsListFragment();
		showNewsPagersFragment();
		initSidebar();
		initNewsSizeSeekbar();
		initSwitches();
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}


	@Override
	public void onBackStackChanged() {
		invalidateOptionsMenu();
		ifOnBackFromSearchedNewsListFragment();
	}


	private void ifOnBackFromSearchedNewsListFragment() {
		Fragment f = getTopFragment();
		if (!(f instanceof SearchedNewsListFragment)) {
			((EditText) getSupportActionBar().getCustomView().findViewById(R.id.tv_input_search_key)).setText("");
		}
	}


	private void initNewsSizeSeekbar() {
		SeekBar sb = (SeekBar) findViewById(R.id.sb_news_count);
		sb.setProgress(Prefs.getInstance().getNewsSize());
		sb.setOnSeekBarChangeListener(this);
		((TextView) findViewById(R.id.tv_news_size)).setText(String.format(getString(R.string.title_news_size),
				sb.getProgress()));
	}


	private void initSwitches() {
		de.ankri.views.Switch sw = null;
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_english)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportEnglish());
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_chinese)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportChinese());
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_german)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().isSupportGerman());
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_open_content_type)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().getDontAskForOpeningDetailsMethod());
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
		View customView = View.inflate(getApplicationContext(), R.layout.input_search_key, null);
		ab.setCustomView(customView);
		((EditText) customView.findViewById(R.id.tv_input_search_key)).setOnEditorActionListener(this);
	}


	@Override
	public boolean onEditorAction(TextView _v, int _actionId, KeyEvent _event) {
		switch (_v.getId()) {
			case R.id.tv_input_search_key:
				if (_actionId == EditorInfo.IME_ACTION_GO) {
					startSearching(_v, _v.getText().toString());
					return true;
				}
				return false;
		}
		return false;
	}


	private void startSearching(TextView _v, String _key) {
		Util.hideKeyboard(this, _v);
		showSearchedNewsListFragment(_key);
	}


	@Override
	public void onDrawerOpened(View drawerView) {
		mDrawerToggle.onDrawerOpened(drawerView);
		de.ankri.views.Switch sw;
		(sw = (de.ankri.views.Switch) findViewById(R.id.switch_open_content_type)).setOnCheckedChangeListener(this);
		sw.setChecked(Prefs.getInstance().getDontAskForOpeningDetailsMethod());
	}


	@Override
	public void onDrawerClosed(View drawerView) {
		mDrawerToggle.onDrawerClosed(drawerView);
	}


	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
	}


	@Override
	public void onDrawerStateChanged(int newState) {
		mDrawerToggle.onDrawerStateChanged(newState);
	}


	private void initSidebar() {
		DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.sidebar);
		mDrawerToggle = new ActionBarDrawerToggle(this, sidebar, R.drawable.ic_drawer, -1, -1);
		sidebar.setDrawerListener(this);
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


	public boolean isRefreshing() {
		if (mPullToRefreshAttacher != null) {
			return mPullToRefreshAttacher.isRefreshing();
		}
		return false;
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


	private Fragment getTopFragment() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			return null;
		}
		String tag = getSupportFragmentManager().getBackStackEntryAt(
				getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		return getSupportFragmentManager().findFragmentByTag(tag);
	}


	private void refresh() {
		Fragment f = getTopFragment();
		if (f instanceof IRefreshable) {
			IRefreshable refreshable = (IRefreshable) f;
			refreshable.refresh();
		} else {
			Fragment lastFragment = getSupportFragmentManager().findFragmentByTag(NewsPagersFragment.TAG);
			if (lastFragment instanceof IRefreshable) {
				IRefreshable refreshable = (IRefreshable) lastFragment;
				refreshable.refresh();
			}
		}
	}


	private void share() {
		Fragment f = getTopFragment();
		if (f instanceof ISharable) {
			ISharable share = (ISharable) f;
			new ShareUtil().openShare(this, share);
		} else {
			new ShareUtil().openShare(this, this);
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton _buttonView, boolean _isChecked) {
		switch (_buttonView.getId()) {
			case R.id.switch_english:
				Prefs.getInstance().setSupportEnglish(_isChecked);
				updatePages();
				break;
			case R.id.switch_chinese:
				Prefs.getInstance().setSupportChinese(_isChecked);
				updatePages();
				break;
			case R.id.switch_german:
				Prefs.getInstance().setSupportGerman(_isChecked);
				updatePages();
				break;
			case R.id.switch_open_content_type:
				Prefs.getInstance().setDontAskForOpeningDetailsMethod(_isChecked);
				break;
			default:
				break;
		}
	}


	private void updatePages() {
		Fragment f = getTopFragment();
		if (f instanceof NewsPagersFragment) {
			((NewsPagersFragment) f).updatePages();
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem _item) {
		switch (_item.getItemId()) {
			case R.id.action_refresh:
				refresh();
				break;
			case R.id.action_share:
				share();
				break;
			case R.id.action_open_in_browser:
				openNewsDetailsInBrowser();
				break;
		}
		return true;
	}


	private void openNewsDetailsInBrowser() {
		if (getTopFragment() instanceof NewsDetailsFragment) {
			NewsDetailsFragment f = (NewsDetailsFragment) getTopFragment();
			f.openInBrowser();
		}
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu _menu) {
		if (getTopFragment() instanceof NewsDetailsFragment) {
			_menu.findItem(R.id.action_refresh).setVisible(false);
			_menu.findItem(R.id.action_open_in_browser).setVisible(true);
		} else {
			_menu.findItem(R.id.action_refresh).setVisible(true);
			_menu.findItem(R.id.action_open_in_browser).setVisible(false);
		}
		return super.onPrepareOptionsMenu(_menu);
	}


	/**
	 * Use ViewPager to show all news.
	 * 
	 * */
	private void showNewsPagersFragment() {
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		NewsPagersFragment fmg = NewsPagersFragment.newInstance(this);
		trans.replace(R.id.container_news, fmg, NewsPagersFragment.TAG);
		trans.commit();
	}


	/**
	 * Use single page to show all news.
	 * 
	 * @deprecated We use now ViewPager to show all news per langauge.
	 * */
	@SuppressWarnings("unused")
	@Deprecated
	private void showNewsListFragment() {
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		NewsListFragment fmg = NewsListFragment.newInstance(this);
		trans.replace(R.id.container_news, fmg, NewsListFragment.TAG);
		trans.commit();
	}


	private void showSearchedNewsListFragment(String _key) {
		Fragment f = getTopFragment();
		if (f instanceof SearchedNewsListFragment) {
			SearchedNewsListFragment snf = (SearchedNewsListFragment) f;
			snf.searchWitNewKey(_key);
		} else {
			SearchedNewsListFragment fmg = SearchedNewsListFragment.newInstance(this, _key);
			openNextPage(fmg, SearchedNewsListFragment.TAG);
		}
	}


	public void openNextPage(Fragment _f, String _tag) {
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left,
				R.anim.slide_out_to_right);
		trans.add(R.id.container_news, _f, _tag).addToBackStack(_tag).commit();
	}


	public void closePage(String _tag) {
		getSupportFragmentManager().popBackStack(_tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		// trans.remove(_f);
		trans.commit();
	}


	public static void showPopup(FragmentActivity _activty, DialogFragment _dlgFrg, String _tagName) {
		if (_dlgFrg != null) {
			DialogFragment dialogFragment = _dlgFrg;
			FragmentTransaction ft = _activty.getSupportFragmentManager().beginTransaction();
			// Ensure that there's only one dialog to the user.
			Fragment prev = _activty.getSupportFragmentManager().findFragmentByTag(DLG_TAG);
			if (prev != null) {
				ft.remove(prev);
			}
			try {
				if (TextUtils.isEmpty(_tagName)) {
					dialogFragment.show(ft, DLG_TAG);
				} else {
					dialogFragment.show(ft, _tagName);
				}
			} catch (Exception _e) {
			}
		}
	}


	public synchronized void showLoadingFragment(int _max) {
		showPopup(this, LoadingFragment.newInstance(getApplicationContext(), _max), DLG_TAG);
	}


	public synchronized void setLoadingFragmentStep(int _step) {
		Fragment runningDlg = getSupportFragmentManager().findFragmentByTag(DLG_TAG);
		if (runningDlg instanceof LoadingFragment) {
			((LoadingFragment) runningDlg).setStep(_step);
		}
	}


	@Override
	public String getSubject() {
		return "plackholder";
	}


	@Override
	public String getText() {
		return "plackholder";
	}


	@Override
	public void onStartTrackingTouch(SeekBar _seekBar) {
	}


	@Override
	public void onStopTrackingTouch(SeekBar _seekBar) {
	}
}
