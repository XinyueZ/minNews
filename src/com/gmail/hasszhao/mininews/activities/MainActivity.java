package com.gmail.hasszhao.mininews.activities;

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
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.fragments.NewsDetailsFragment;
import com.gmail.hasszhao.mininews.fragments.dialog.LoadingFragment;
import com.gmail.hasszhao.mininews.fragments.list.BookmarkListFragment;
import com.gmail.hasszhao.mininews.fragments.viewpagers.NewsPagersFragment;
import com.gmail.hasszhao.mininews.fragments.viewpagers.SearchedNewsPagersFragment;
import com.gmail.hasszhao.mininews.interfaces.IRefreshable;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.interfaces.OnFragmentBackStackChangedListener;
import com.gmail.hasszhao.mininews.utils.ShareUtil;
import com.gmail.hasszhao.mininews.utils.TabFactory;
import com.gmail.hasszhao.mininews.utils.TabFactory.OnTabClosedListener;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;


public final class MainActivity extends BasicActivity implements OnCheckedChangeListener, ISharable,
        OnBackStackChangedListener, OnEditorActionListener, DrawerLayout.DrawerListener, OnTabChangeListener,
        OnTabClosedListener, View.OnClickListener {

    public static final String ACTION = "com.gmail.hasszhao.mininews.MainActivity";
    private static final int FRAGMENT_ID = R.id.container_news;
    private static final String DLG_TAG = "dlg";
    private static final int LAYOUT = R.layout.activity_main;
    private final ArrayList<TabHost.TabSpec> mTabSpecList = new ArrayList<TabHost.TabSpec>();
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private ActionBarDrawerToggle mDrawerToggle;
    private TabHost mTabHost;
    private String TAG_HOME;

    public static void showDialogFragment(FragmentActivity _activty, DialogFragment _dlgFrg, String _tagName) {
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

    @Override
    protected void onDestroy() {
        mPullToRefreshAttacher = null;
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration _newConfig) {
        super.onConfigurationChanged(_newConfig);
        mDrawerToggle.onConfigurationChanged(_newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG_HOME = getString(R.string.title_home_tab);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initActionbar();
        mPullToRefreshAttacher = new PullToRefreshAttacher(this);
        // showNewsListFragment();
        showNewsPagersFragment();
        initSidebar();
        initSwitches();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        initTabs();
    }

    private void initTabs() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(this);
        addTab(TAG_HOME, false);
    }

    private void addTab(String _tabText, boolean _canClose) {
        TabFactory tf = new TabFactory(this);
        tf.setOnTabClosedListener(this);
        TabHost.TabSpec spec = mTabHost.newTabSpec(_tabText);
        spec.setIndicator(tf.createTabView(_tabText, _canClose));
        spec.setContent(tf);
        mTabHost.addTab(spec);
        mTabHost.setCurrentTabByTag(_tabText);
        mTabSpecList.add(spec);
    }

    @Override
    public void onTabClosed(String _tabText) {
        // mTabHost.
        // Util.showShortToast(getApplicationContext(), _tabText);
        removeTab(_tabText);
        removeFragmentragment(_tabText);
    }

    private void removeTab(String _tabText) {
        for (TabHost.TabSpec spec : mTabSpecList) {
            if (TextUtils.equals(_tabText, spec.getTag())) {
                mTabSpecList.remove(spec);
                break;
            }
        }
        mTabHost.setCurrentTab(0);
        mTabHost.clearAllTabs();
        int nTabIndex = 0;
        for (TabHost.TabSpec spec : mTabSpecList) {
            mTabHost.addTab(spec);
            mTabHost.setCurrentTab(nTabIndex++);
        }
        mTabHost.setCurrentTab(nTabIndex - 1);
    }

    @Override
    public void onTabChanged(String _tabId) {
        // Learn here to change the "shown" page.
        // http://wptrafficanalyzer.in/blog/creating-navigation-tabs-using-tabhost-and-fragments-in-android/
        // But I use "show"/"hide" instead of "attach" and "detach" .
        // Util.showShortToast(getApplicationContext(), _tabId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment found = null;
        for (TabSpec spec : mTabSpecList) {
            if (!TextUtils.equals(_tabId, spec.getTag())) {
                found = getSupportFragmentManager().findFragmentByTag(spec.getTag());
                if (found != null) {
                    ft.hide(found);
                }
            } else {
                found = getSupportFragmentManager().findFragmentByTag(spec.getTag());
                if (found != null) {
                    ft.show(found);
                }
            }
        }
        ft.commit();
        if (!TextUtils.equals(_tabId, getString(R.string.title_home_tab))) {
            ActionBar ab = getSupportActionBar();
            View customView = ab.getCustomView();
            ((EditText) customView.findViewById(R.id.tv_input_search_key)).setText(_tabId);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment top = getTopFragment();
        if (top != null) {
            removeTab(top.getTag());
        }
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
        invalidateOptionsMenu();
        ifOnBackFromSearchedNewsListFragment();
        if (getTopFragment() == null) {
            setSidebarEnable(true);
        } else {
            setSidebarEnable(false);
        }
        // Solve that problem that "added" fragments can not handle onResume
        // that "replaced" can.
        // http://stackoverflow.com/questions/6503189/fragments-onresume-from-back-stack
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            Fragment currFrag = manager.findFragmentById(FRAGMENT_ID);
            if (currFrag instanceof OnFragmentBackStackChangedListener) {
                ((OnFragmentBackStackChangedListener) currFrag).onFragmentResume();
            }
        }
        Fragment top = getTopFragment();
        if (top == null) {
            // Bottom-viewpager
            mTabHost.setCurrentTabByTag(TAG_HOME);
        } else {
            // Other-viewpagers, i.e "searched"
            mTabHost.setCurrentTabByTag(top.getTag());
        }
    }

    private void ifOnBackFromSearchedNewsListFragment() {
        Fragment f = getTopFragment();
        if (f == null) {
            ((EditText) getSupportActionBar().getCustomView().findViewById(R.id.tv_input_search_key)).setText("");
        }
    }

    public void setSidebarEnable(boolean _enable) {
        if (!_enable) {
            ((DrawerLayout) findViewById(R.id.sidebar)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            ((DrawerLayout) findViewById(R.id.sidebar)).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        mDrawerToggle.setDrawerIndicatorEnabled(_enable);
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

    private void initActionbar() {
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        View customView = View.inflate(getApplicationContext(), R.layout.input_search_key, null);
        ab.setCustomView(customView);
        customView.findViewById(R.id.btn_remove).setOnClickListener(this);
        ((EditText) customView.findViewById(R.id.tv_input_search_key)).setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View _v) {
        ((EditText) getSupportActionBar().getCustomView().findViewById(R.id.tv_input_search_key)).setText("");
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
        addTab(_key, true);
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
            // The bottom site(viewpager) is now exposed to user.
            Fragment lastFragment = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
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
        Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
        if (f instanceof NewsPagersFragment) {
            ((NewsPagersFragment) f).updatePages();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem _item) {
        switch (_item.getItemId()) {
            case android.R.id.home:
                goUp();
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_open_in_browser:
                openNewsDetailsInBrowser();
                return true;
            case R.id.action_bookmark_list:
                openBookmarkList();
                return true;
        }
        return false;
    }

    private void goUp() {
        getSupportFragmentManager().popBackStack();
    }

    private void openNewsDetailsInBrowser() {
        if (getTopFragment() instanceof NewsDetailsFragment) {
            NewsDetailsFragment f = (NewsDetailsFragment) getTopFragment();
            f.openInBrowser();
        }
    }

    private void openBookmarkList() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(BookmarkListFragment.TAG);
        if (f == null) {
            addOpenNextPage(BookmarkListFragment.newInstance(getApplicationContext()), BookmarkListFragment.TAG);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu _menu) {
        if (getTopFragment() instanceof NewsDetailsFragment) {
            _menu.findItem(R.id.action_refresh).setVisible(false);
            _menu.findItem(R.id.action_open_in_browser).setVisible(true);
            if (getSupportFragmentManager().findFragmentByTag(BookmarkListFragment.TAG) != null) {
                _menu.findItem(R.id.action_bookmark_list).setVisible(false);
            }
        } else {
            _menu.findItem(R.id.action_refresh).setVisible(true);
            _menu.findItem(R.id.action_open_in_browser).setVisible(false);
        }
        return super.onPrepareOptionsMenu(_menu);
    }

    /**
     * Use ViewPager to show all news.
     */
    private void showNewsPagersFragment() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        NewsPagersFragment fmg = NewsPagersFragment.newInstance(this);
        trans.replace(FRAGMENT_ID, fmg, TAG_HOME);
        trans.commit();
    }

    private void showSearchedNewsListFragment(String _key) {
        SearchedNewsPagersFragment fmg = SearchedNewsPagersFragment.newInstance(this, _key);
        addOpenNextPage(fmg, _key);
        setSidebarEnable(false);
    }

    public void addOpenNextPage(Fragment _f, String _tag) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left,
                R.anim.slide_out_to_right);
        trans.add(FRAGMENT_ID, _f, _tag).addToBackStack(_tag).commit();
    }

    public void closePage(String _tag) {
        getSupportFragmentManager().popBackStack(_tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        // trans.remove(_f);
        trans.commit();
    }

    public synchronized void showLoadingFragment(int _max) {
        showDialogFragment(this, LoadingFragment.newInstance(getApplicationContext(), _max), DLG_TAG);
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
}