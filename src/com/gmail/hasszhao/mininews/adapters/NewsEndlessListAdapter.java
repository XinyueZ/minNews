package com.gmail.hasszhao.mininews.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;


public final class NewsEndlessListAdapter extends EndlessAdapter {

	private final Context mContext;
	private final ListNews mNewsList;


	public interface ICallNext {

		void callNext(int _index);
	}


	private final ICallNext mCallNext;


	public NewsEndlessListAdapter(Context _context, NewsListAdapter _newsListAdapter, ICallNext _callNext) {
		super(_newsListAdapter);
		mContext = _context;
		mNewsList = _newsListAdapter.getNewsListItems();
		mCallNext = _callNext;
	}


	@Override
	protected View getPendingView(ViewGroup parent) {
		return View.inflate(mContext, R.layout.loading, null);
	}


	@Override
	protected boolean cacheInBackground() throws Exception {
		List<DONews> list = mNewsList.getPulledNewss();
		int sz = list.size();
		mCallNext.callNext(sz + 1);
		Log.d("mini", "Ask: sz=" + sz);
		return sz < mNewsList.getCount();
	}


	@Override
	protected void appendCachedData() {
	}
}
