package com.gmail.hasszhao.mininews.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.hasszhao.mininews.fragments.details.NewsDetailsPageFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;


public final class DetailsPagersAdapter extends FragmentPagerAdapter {

	private Context mContext;
	private Fragment mTargetFragment;
	private int mCount;


	public DetailsPagersAdapter(FragmentManager _fm, Context _context, Fragment _targetFragment) {
		super(_fm);
		mContext = _context;
		mTargetFragment = _targetFragment;
		if (mTargetFragment instanceof INewsListItemProvider) {
			mCount = ((INewsListItemProvider) mTargetFragment).getList().size();
		}
	}


	@Override
	public Fragment getItem(int _i) {
		return NewsDetailsPageFragment.newInstance(mContext, mTargetFragment, _i);
	}


	@Override
	public int getCount() {
		return mCount;
	}
}
