package com.gmail.hasszhao.mininews.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;


public final class NewsEndlessListAdapter extends EndlessAdapter {

	private final Context mContext;
	private final int maxCount;
	private final List<? extends INewsListItem> mNewsList;


	public interface ICallNext {

		void callNext(int _index);
	}


	private final ICallNext mCallNext;


	public NewsEndlessListAdapter(Context _context, NewsListAdapter _newsListAdapter, int _maxCount, ICallNext _callNext) {
		super(_newsListAdapter);
		mContext = _context;
		maxCount = _maxCount;
		mNewsList = _newsListAdapter.getNewsListItems();
		mCallNext = _callNext;
	}


	@Override
	protected View getPendingView(ViewGroup parent) {
		return View.inflate(mContext, R.layout.loading, null);
	}


	@Override
	protected boolean cacheInBackground() throws Exception {
		return mNewsList.size() < maxCount;
	}


	@Override
	protected void appendCachedData() {
		mCallNext.callNext(mNewsList.size());
	}
}
