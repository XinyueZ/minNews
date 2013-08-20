package com.gmail.hasszhao.mininews.fragments.viewpagers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.adapters.DetailsPagersAdapter;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.fragments.details.NewsDetailsPageFragment;
import com.gmail.hasszhao.mininews.interfaces.IOpenInBrowser;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.viewpagerindicator.CirclePageIndicator;


public final class DetailsPagersFragment extends BasicFragment implements ISharable, IOpenInBrowser {

	public static final String TAG = "TAG.DetailsPagersFragment";
	public static final String KEY_SELECTED_INDEX = "selected.index";
	private static final int LAYOUT = R.layout.fragment_news_pages;
	private DetailsPagersAdapter mAdapter;


	public static DetailsPagersFragment newInstance(Context _context, Fragment _targetFragment, int _selectedIndex) {
		Bundle args = new Bundle();
		args.putInt(KEY_SELECTED_INDEX, _selectedIndex);
		DetailsPagersFragment fragment = (DetailsPagersFragment) DetailsPagersFragment.instantiate(_context,
				DetailsPagersFragment.class.getName(), args);
		fragment.setTargetFragment(_targetFragment, 0);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			int currentIndex = getArguments().getInt(KEY_SELECTED_INDEX);
			ViewPager vp = (ViewPager) _view.findViewById(R.id.vp_news_pages);
			vp.setAdapter(mAdapter = new DetailsPagersAdapter(getChildFragmentManager(), getActivity(),
					getTargetFragment()));
			vp.setCurrentItem(currentIndex);
			CirclePageIndicator indicator = (CirclePageIndicator) _view.findViewById(R.id.vp_indicator);
			indicator.setViewPager(vp, currentIndex);
		}
	}


	private NewsDetailsPageFragment getCurrentNewsDetailsPageFragment() {
		View view = getView();
		if (mAdapter != null && view != null) {
			ViewPager vp = (ViewPager) view.findViewById(R.id.vp_news_pages);
			int currentIndex = vp.getCurrentItem();
			return (NewsDetailsPageFragment) mAdapter.instantiateItem(vp, currentIndex);
		}
		return null;
	}


	@Override
	public String getSharedSubject() {
		NewsDetailsPageFragment fragment = getCurrentNewsDetailsPageFragment();
		if (fragment != null) {
			return fragment.getSharedSubject();
		}
		return null;
	}


	@Override
	public String getSharedText() {
		NewsDetailsPageFragment fragment = getCurrentNewsDetailsPageFragment();
		if (fragment != null) {
			return fragment.getSharedText();
		}
		return null;
	}


	@Override
	public void openInBrowser() {
		NewsDetailsPageFragment fragment = getCurrentNewsDetailsPageFragment();
		if (fragment != null) {
			fragment.openInBrowser();
		}
	}
}
