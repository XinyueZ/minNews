package com.gmail.hasszhao.mininews.adapters;

import java.util.ArrayList;
import java.util.List;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.gmail.hasszhao.mininews.dataset.DONews;


public final class NewsEndlessListAdapter extends EndlessAdapter implements IItemsReadyListener {

	private final int maxCount;
	private List<DONews> mNewsList;


	public NewsEndlessListAdapter(NewsListAdapter _newsListAdapter, List<DONews> _newsList, int _maxCount) {
		super(_newsListAdapter);
		maxCount = _maxCount;
		mNewsList = _newsList;
	}


	@Override
	protected boolean cacheInBackground() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected void appendCachedData() {
		// TODO Auto-generated method stub
	}
}


interface IItemsReadyListener {

	public void onItemsReady(List<DONews> _data);
}
