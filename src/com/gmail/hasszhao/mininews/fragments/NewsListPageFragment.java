package com.gmail.hasszhao.mininews.fragments;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gmail.hasszhao.mininews.API;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.MainActivity;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsClickedListener;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsShareListener;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.fragments.AskOpenDetailsMethodFragment.OpenContentMethod;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.IRefreshable;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.LoadNewsListTask;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.ShareUtil;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public class NewsListPageFragment extends BasicFragment implements Listener<DOStatus>, ErrorListener,
		OnRefreshListener, OnNewsClickedListener, OnNewsShareListener, IRefreshable, INewsListItemProvider {

	private static final int LAYOUT = R.layout.fragment_news_list;
	public static final String TAG = "TAG.NewsList.Page";
	private static final String KEY_LANGUAGE = "NewsList.Page.language";
	private static final int MAX_FRQUENT = 5 * 1000;
	protected NewsListAdapter mAdapter;
	private long mLastLoadingTime = 0;
	private List<DONews> mNewsList;
	private INewsListItem mSelectedNewsItem;


	@Override
	public void onAttach(Activity _activity) {
		super.onAttach(_activity);
		Activity act = _activity;
		mNewsList = ((App) act.getApplication()).getListNews(getArguments().getString(KEY_LANGUAGE));
	}


	public static NewsListPageFragment newInstance(Context _context, String _language) {
		Bundle args = new Bundle();
		args.putString(KEY_LANGUAGE, _language);
		return (NewsListPageFragment) NewsListPageFragment.instantiate(_context, NewsListPageFragment.class.getName(),
				args);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (mNewsList == null) {
			refreshData();
		} else {
			initList();
		}
	}


	@Override
	public void onDestroyView() {
		TaskHelper.getRequestQueue().cancelAll(LoadNewsListTask.TAG);
		super.onDestroyView();
	}


	@Override
	public void onDestroy() {
		mAdapter = null;
		mNewsList = null;
		mSelectedNewsItem = null;
		super.onDestroy();
	}


	private void refreshData() {
		loadData();
	}


	@Override
	public void refresh() {
		refreshData();
	}


	private void loadData() {
		long now = System.currentTimeMillis();
		if (now - mLastLoadingTime > MAX_FRQUENT) {
			Activity act = getActivity();
			if (act != null) {
				mNewsList = new ArrayList<DONews>();
				new LoadNewsListTask(act.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class, this, this,
						new DOCookie(1, getArguments().getString(KEY_LANGUAGE),
								getQuery())).execute();
			}
			mLastLoadingTime = now;
		} else {
			Activity act = getActivity();
			if (act instanceof MainActivity) {
				((MainActivity) act).setLoadingFragmentStep(1);
			}
		}
	}


	protected String getQuery() {
		return "";
	}


	@Override
	public synchronized void onErrorResponse(VolleyError _error) {
		Log.e("news", "Ask: API_ErrorResponse");
		stepDone();
	}


	@Override
	public synchronized void onResponse(DOStatus _response) {
		if (_response != null) {
			try {
				switch (_response.getCode()) {
					case API.API_OK:
						Log.i("news", "Ask: API_OK");
						mNewsList.addAll(TaskHelper
								.getGson()
								.fromJson(new String(Base64.decode(_response.getData(), Base64.DEFAULT)),
										ListNews.class).getPulledNewss());
						initList();
						break;
					case API.API_ACTION_FAILED:
						Log.e("news", "Ask: API_ACTION_FAILED");
						break;
					case API.API_SERVER_DOWN:
						Log.e("news", "Ask: API_SERVER_DOWN");
						break;
				}
				Activity act = getActivity();
				if (act != null) {
					((MainActivity) act).refreshComplete();
				}
			} catch (Exception _e) {
				_e.printStackTrace();
			}
		}
	}


	private void initList() {
		if (mNewsList != null && mNewsList.size() > 0) {
			// Log.d("news", "Ask: news size:" + newsList.size());
			View v = getView();
			if (v != null) {
				ListView listView = (ListView) v.findViewById(R.id.activity_googlecards_listview);
				// if (mAdapter == null) {
				mAdapter = new NewsListAdapter(getActivity(), mNewsList);
				mAdapter.setOnNewsClickedListener(this);
				mAdapter.setOnNewsShareListener(this);
				listView.setOnScrollListener(mAdapter);
				supportCardAnim(listView);
				// } else {
				// mAdapter.refresh(getActivity(), mNewsList);
				// }
			}
		}
		stepDone();
	}


	private void stepDone() {
		FragmentActivity act = getActivity();
		if (act instanceof MainActivity) {
			((MainActivity) act).setLoadingFragmentStep(1);
		}
	}


	@Override
	public void setUserVisibleHint(boolean _isVisibleToUser) {
		View view = getView();
		if (_isVisibleToUser && canPullToLoad() && view != null) {
			ListView listView = (ListView) view.findViewById(R.id.activity_googlecards_listview);
			Activity act = getActivity();
			if (act != null) {
				((MainActivity) act).setRefreshableView(listView, this);
			}
		}
		super.setUserVisibleHint(_isVisibleToUser);
	}


	/**
	 * Should pull to load or not. Default in NewsList is true.
	 * */
	protected boolean canPullToLoad() {
		return true;
	}


	private void supportCardAnim(ListView listView) {
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwingBottomInAnimationAdapter(mAdapter, BOTTOM_IN_SEC));
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listView.setAdapter(swingBottomInAnimationAdapter);
	}


	@Override
	public void onRefreshStarted(View _view) {
		if (_view != null) {
			loadData();
		}
	}


	@Override
	public void onNewsClicked(INewsListItem _newsItem) {
		FragmentActivity act = getActivity();
		if (act != null) {
			mSelectedNewsItem = _newsItem;
			if (!Prefs.getInstance().getDontAskForOpeningDetailsMethod()) {
				MainActivity.showPopup(act, AskOpenDetailsMethodFragment.newInstance(this), null);
			} else {
				openDetails(OpenContentMethod.fromValue(Prefs.getInstance().getOpenDetailsMethod()));
			}
		}
	}


	@Override
	public void openDetails(OpenContentMethod method) {
		switch (method) {
			case IN_APP:
				openDetailsInApp();
				break;
			case IN_BROWSER:
				openDetailsInBrowser();
				break;
		}
	}


	private String makeShareText(INewsListItem _newsItem) {
		return new StringBuilder().append(_newsItem.getTopline()).append('\n').append("----").append('\n')
				.append(_newsItem.getURL()).toString();
	}


	@Override
	public void onNewsShare(final INewsListItem _newsItem) {
		new ShareUtil().openShare(this, new ISharable() {

			@Override
			public String getText() {
				return makeShareText(_newsItem);
			}


			@Override
			public String getSubject() {
				return getString(R.string.app_name);
			}
		});
	}


	public void openDetailsInApp() {
		Activity act = getActivity();
		if (act instanceof MainActivity) {
			Fragment f = NewsDetailsFragment.newInstance(act);
			f.setTargetFragment(this, 0);
			((MainActivity) act).openNextPage(f, NewsDetailsFragment.TAG);
		}
	}


	public void openDetailsInBrowser() {
		Activity act = getActivity();
		if (act instanceof MainActivity && mSelectedNewsItem != null) {
			Util.openUrl(act, mSelectedNewsItem.getURL());
			// act.overridePendingTransition(R.anim.hyperspace_fast_in,
			// R.anim.hyperspace_fast_out);
		}
	}


	@Override
	public INewsListItem getNewsListItem() {
		return mSelectedNewsItem;
	}
}
