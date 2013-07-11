package com.gmail.hasszhao.mininews.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.hasszhao.mininews.MainActivity;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.adapters.NewsListPagesAdapter;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.IRefreshable;
import com.viewpagerindicator.CirclePageIndicator;


public final class NewsPagersFragment extends BasicFragment implements IRefreshable {

	private static final int LAYOUT = R.layout.fragment_news_pages;
	public static final String TAG = "TAG.NewsPagersFragment";
	private NewsListPagesAdapter mAdapter;


	public static NewsPagersFragment newInstance(Context _context) {
		return (NewsPagersFragment) NewsPagersFragment.instantiate(_context, NewsPagersFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onResume() {
		MainActivity act = (MainActivity) getActivity();
		if (act != null) {
			act.setSidebarEnable(true);
		}
		super.onResume();
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		updatePages();
	}


	/**
	 * 
	 * Update pages. Maybe reloading data.
	 * */
	public void updatePages() {
		View view = getView();
		if (view != null) {
			ViewPager vp = (ViewPager) view.findViewById(R.id.vp_news_pages);
			if (mAdapter == null) {
				mAdapter = new NewsListPagesAdapter(getChildFragmentManager(), getActivity().getApplicationContext());
				showLoadingFragment();
				vp.setOffscreenPageLimit(mAdapter.getCount());
				vp.setAdapter(mAdapter);
			} else {
				showLoadingFragment();
				mAdapter.setData(getChildFragmentManager(), getActivity().getApplicationContext());
				// There's a workaround, but it seems doesn't work for my case
				// that after closing sub-fragment of details.
				//
				// https://code.google.com/p/android/issues/detail?id=19001
				// mAdapter.notifyDataSetChanged();
				//
				// I still use setAdapter to refresh data.
				vp.setAdapter(mAdapter);
			}
			CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.vp_indicator);
			indicator.setViewPager(vp);
		}
	}


	private void showLoadingFragment() {
		Activity act = getActivity();
		if (act != null) {
			((MainActivity) act).showLoadingFragment(mAdapter.getCount());
		}
	}


	/**
	 * 
	 * Reloading page's data
	 * 
	 * */
	@Override
	public void refresh() {
		// To get fragment at some positions.
		//
		// http://afeilulu.wordpress.com/2012/05/11/%E8%8E%B7%E5%8F%96viewpager%E4%B8%AD%E7%9A%84viewpager/
		//
		View view = getView();
		if (mAdapter != null && view != null) {
			showLoadingFragment();
			int count = mAdapter.getCount();
			NewsListPageFragment f;
			ViewPager vp = (ViewPager) view.findViewById(R.id.vp_news_pages);
			for (int i = 0; i < count; i++) {
				f = (NewsListPageFragment) mAdapter.instantiateItem(vp, i);
				f.refresh();
			}
		}
	}
}
