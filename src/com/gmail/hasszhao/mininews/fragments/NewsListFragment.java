package com.gmail.hasszhao.mininews.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public final class NewsListFragment extends Fragment implements OnDismissCallback {

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
		initList();
	}


	private void initList() {
		View v = getView();
		if (v != null) {
			ListView listView = (ListView) v.findViewById(R.id.activity_googlecards_listview);
			NewsListAdapter adapter = new NewsListAdapter(getActivity(), getItems());
			SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
					new SwipeDismissAdapter(adapter, this));
			swingBottomInAnimationAdapter.setAbsListView(listView);
			listView.setAdapter(swingBottomInAnimationAdapter);
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


	private ArrayList<INewsListItem> getItems() {
		ArrayList<INewsListItem> items = new ArrayList<INewsListItem>();
		for (int i = 0; i < 100; i++) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			items.add(new TempNewsItem("Topline " + i, "Headline " + i, c.getTime().toLocaleString()));
		}
		return items;
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
