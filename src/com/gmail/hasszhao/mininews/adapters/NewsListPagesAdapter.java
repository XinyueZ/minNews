package com.gmail.hasszhao.mininews.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gmail.hasszhao.mininews.fragments.NewsListPageFragment;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class NewsListPagesAdapter extends FragmentStatePagerAdapter {

	private final List<String> mLanguages = new ArrayList<String>();
	private Context mContext;


	public NewsListPagesAdapter(FragmentManager _fm, Context _context) {
		super(_fm);
		setData(_fm, _context);
	}


	public void setData(FragmentManager _fm, Context _context) {
		if (mLanguages.size() > 0) {
			mLanguages.clear();
		}
		if (Prefs.getInstance().isSupportEnglish()) {
			mLanguages.add("en");
		}
		if (Prefs.getInstance().isSupportGerman()) {
			mLanguages.add("de");
		}
		if (Prefs.getInstance().isSupportChinese()) {
			mLanguages.add("zh");
		}
		mContext = _context;
	}


	@Override
	public Fragment getItem(int _arg0) {
		return NewsListPageFragment.newInstance(mContext, mLanguages.get(_arg0));
	}


	@Override
	public int getCount() {
		return mLanguages.size();
	}
}
