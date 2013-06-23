package com.gmail.hasszhao.mininews.fragments;

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
import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.tasks.LoadNewsListTask;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.Prefs;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public final class NewsListFragment extends SherlockFragment implements OnDismissCallback, Listener<DOStatus>,
		ErrorListener, OnRefreshListener {

	public static final String TAG = "TAG.NewsList";
	private final static int LAYOUT = R.layout.fragment_news_list;
	private NewsListAdapter mAdapter;


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
	public void onResume() {
		refreshData();
		super.onResume();
	}


	@Override
	public void onDestroyView() {
		TaskHelper.getRequestQueue().cancelAll(LoadNewsListTask.TAG);
		mAdapter = null;
		super.onDestroyView();
	}


	public void refreshData() {
		((MainActivity) getActivity()).refreshing();
		loadData();
	}


	private void loadData() {
		Activity act = getActivity();
		if (act != null) {
			new LoadNewsListTask(act.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class, this, this)
					.execute();
		}
	}


	@Override
	public void onErrorResponse(VolleyError _error) {
	}


	@Override
	public void onResponse(DOStatus _response) {
		if (_response != null) {
			try {
				switch (_response.getCode()) {
					case API.API_OK:
						Log.w("news", "Ask: API_OK");
						initList(TaskHelper.getGson().fromJson(
								new String(Base64.decode(_response.getData(), Base64.DEFAULT)), ListNews.class));
						break;
					case API.API_ACTION_FAILED:
						Log.e("news", "Ask: API_ACTION_FAILED");
						// Util.showLongToast(_cxt, R.string.action_failed);
						break;
					case API.API_SERVER_DOWN:
						Log.e("news", "Ask: API_SERVER_DOWN");
						// Util.showLongToast(_cxt, R.string.server_down);
						break;
				}
				((MainActivity) getActivity()).refreshComplete();
			} catch (Exception _e) {
				_e.printStackTrace();
			}
		}
	}


	private void initList(ListNews _listNews) {
		List<DONews> newsList = _listNews.getPulledNewss();
		if (newsList != null && newsList.size() > 0) {
			View v = getView();
			if (v != null) {
				ListView listView = (ListView) v.findViewById(R.id.activity_googlecards_listview);
				mAdapter = new NewsListAdapter(getActivity(), newsList);
				supportCardAnim(listView);
				if (Prefs.getInstance().isSupportPullToLoad()) {
					((MainActivity) getActivity()).setRefreshableView(listView, this);
				}
			}
		}
	}


	private void supportCardAnim(ListView listView) {
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mAdapter, this));
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listView.setAdapter(swingBottomInAnimationAdapter);
	}


	static class TempNewsItem implements INewsListItem {

		private final String topline;
		private final String headline;
		private final String date;


		public TempNewsItem(String _topline, String _headline, String _date) {
			super();
			topline = _topline;
			headline = _headline;
			date = _date;
		}


		@Override
		public String getTopline() {
			return topline;
		}


		@Override
		public String getHeadline() {
			return headline;
		}


		@Override
		public String getDate() {
			return date;
		}
	}


	@Override
	public void onDismiss(AbsListView _listView, int[] _reverseSortedPositions) {
		if (mAdapter != null) {
			for (int position : _reverseSortedPositions) {
				mAdapter.remove(position);
			}
			mAdapter.notifyDataSetChanged();
		}
	}


	@Override
	public void onRefreshStarted(View _view) {
		if (_view != null) {
			loadData();
		}
	}
}
