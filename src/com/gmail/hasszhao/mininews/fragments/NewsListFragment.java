package com.gmail.hasszhao.mininews.fragments;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gmail.hasszhao.mininews.API;
import com.gmail.hasszhao.mininews.MainActivity;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsClickedListener;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsShareListener;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.LoadNewsListTask;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.Prefs;
import com.gmail.hasszhao.mininews.utils.ShareUtil;
import com.gmail.hasszhao.mininews.utils.Util;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public final class NewsListFragment extends SherlockFragment implements OnDismissCallback, Listener<DOStatus>,
		ErrorListener, OnRefreshListener, OnNewsClickedListener, OnNewsShareListener {

	private static final int LAYOUT = R.layout.fragment_news_list;
	public static final String TAG = "TAG.NewsList";
	private static final int MAX_FRQUENT = 5 * 1000;
	private NewsListAdapter mAdapter;
	private long mLastLoadingTime = 0;
	private int mCallCount = 0;
	private List<DONews> mNewsList;


	public static NewsListFragment newInstance(Context _context) {
		return (NewsListFragment) NewsListFragment.instantiate(_context, NewsListFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onAttach(Activity _activity) {
		super.onAttach(_activity);
		TextView title = (TextView) View.inflate(_activity, R.layout.action_bar_title, null);
		((MainActivity) _activity).getSupportActionBar().setCustomView(title);
		title.setText(R.string.title_hot_news);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		refreshData();
	}


	@Override
	public void onDestroyView() {
		TaskHelper.getRequestQueue().cancelAll(LoadNewsListTask.TAG);
		mAdapter = null;
		mNewsList = null;
		super.onDestroyView();
	}


	public void refreshData() {
		((MainActivity) getActivity()).refreshingOn();
		loadData();
	}


	private void loadData() {
		long now = System.currentTimeMillis();
		if (now - mLastLoadingTime > MAX_FRQUENT) {
			Activity act = getActivity();
			if (act != null) {
				mNewsList = new ArrayList<DONews>();
				if (Prefs.getInstance().isSupportEnglish()) {
					mCallCount++;
				}
				if (Prefs.getInstance().isSupportChinese()) {
					mCallCount++;
				}
				if (Prefs.getInstance().isSupportGerman()) {
					mCallCount++;
				}
				if (Prefs.getInstance().isSupportEnglish()) {
					new LoadNewsListTask(act.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class, this, this,
							new DOCookie(Prefs.getInstance().getNewsSize(), "en", "")).execute();
				}
				if (Prefs.getInstance().isSupportChinese()) {
					new LoadNewsListTask(act.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class, this, this,
							new DOCookie(Prefs.getInstance().getNewsSize(), "zh", "")).execute();
				}
				if (Prefs.getInstance().isSupportGerman()) {
					new LoadNewsListTask(act.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class, this, this,
							new DOCookie(Prefs.getInstance().getNewsSize(), "de", "")).execute();
				}
			}
			mLastLoadingTime = now;
		} else {
			Activity act = getActivity();
			if (act != null) {
				((MainActivity) getActivity()).refreshComplete();
			}
			Util.showShortToast(getActivity(), R.string.msg_refresh);
		}
	}


	@Override
	public synchronized void onErrorResponse(VolleyError _error) {
		mCallCount--;
		if (mCallCount == 0) {
			((MainActivity) getActivity()).refreshComplete();
		}
	}


	@Override
	public synchronized void onResponse(DOStatus _response) {
		mCallCount--;
		if (_response != null) {
			try {
				switch (_response.getCode()) {
					case API.API_OK:
						Log.w("news", "Ask: API_OK");
						mNewsList.addAll(TaskHelper
								.getGson()
								.fromJson(new String(Base64.decode(_response.getData(), Base64.DEFAULT)),
										ListNews.class).getPulledNewss());
						if (mCallCount == 0) {
							initList();
						}
						break;
					case API.API_ACTION_FAILED:
						Log.e("news", "Ask: API_ACTION_FAILED");
						break;
					case API.API_SERVER_DOWN:
						Log.e("news", "Ask: API_SERVER_DOWN");
						break;
				}
				if (mCallCount == 0) {
					((MainActivity) getActivity()).refreshComplete();
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
				supportCardAnim(listView);
				// } else {
				// mAdapter.refresh(getActivity(), mNewsList);
				// }
				((MainActivity) getActivity()).setRefreshableView(listView, this);
			}
		}
	}


	private void supportCardAnim(ListView listView) {
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mAdapter, this));
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listView.setAdapter(swingBottomInAnimationAdapter);
	}


	@Override
	public void onDismiss(AbsListView _listView, int[] _reverseSortedPositions) {
		// if (mAdapter != null) {
		// for (int position : _reverseSortedPositions) {
		// mAdapter.remove(position);
		// }
		// mAdapter.notifyDataSetChanged();
		// }
	}


	@Override
	public void onRefreshStarted(View _view) {
		if (_view != null) {
			loadData();
		}
	}


	@Override
	public void onNewsClicked(INewsListItem _newsItem) {
		// Util.openUrl(getActivity(), _newsItem.getURL());
		Activity act = getActivity();
		if (act instanceof MainActivity) {
			((MainActivity) act).openNextPage(
					WebViewFragment.newInstance(act, _newsItem.getFullContent(), makeShareText(_newsItem)),
					WebViewFragment.TAG);
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
}
