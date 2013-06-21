package com.gmail.hasszhao.mininews.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gmail.hasszhao.mininews.API;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter;
import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.tasks.LoadNewsListTask;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public final class NewsListFragment extends Fragment implements OnDismissCallback, Listener<DOStatus>, ErrorListener {

	public static final String TAG = "TAG.NewsList";
	private final static int LAYOUT = R.layout.fragment_news_list;


	public static NewsListFragment newInstance(Context _context) {
		return (NewsListFragment) NewsListFragment.instantiate(_context, NewsListFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
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
						initList(TaskHelper.getGson().fromJson(
								new String(Base64.decode(_response.getData(), Base64.DEFAULT)), ListNews.class));
						break;
					case API.API_ACTION_FAILED:
						// Util.showLongToast(_cxt, R.string.action_failed);
						break;
					case API.API_SERVER_DOWN:
						// Util.showLongToast(_cxt, R.string.server_down);
						break;
				}
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
				NewsListAdapter adapter = new NewsListAdapter(getActivity(), newsList);
				SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
						new SwipeDismissAdapter(adapter, this));
				swingBottomInAnimationAdapter.setAbsListView(listView);
				listView.setAdapter(swingBottomInAnimationAdapter);
			}
		}
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
		ListAdapter adp = _listView.getAdapter();
		if (adp instanceof NewsListAdapter) {
			NewsListAdapter newsListAdapter = (NewsListAdapter) adp;
			for (int position : _reverseSortedPositions) {
				newsListAdapter.remove(position);
			}
		}
	}
}
