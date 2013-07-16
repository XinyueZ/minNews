package com.gmail.hasszhao.mininews.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;

import com.gmail.hasszhao.mininews.fragments.list.NewsListPageFragment;
import com.gmail.hasszhao.mininews.fragments.list.SearchedNewsListPageFragment;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class NewsListPagesAdapter extends FragmentStatePagerAdapter {

	private final List<String> mLanguages = new ArrayList<String>();
	private Context mContext;
	private String mSearchedKey;


	public NewsListPagesAdapter(FragmentManager _fm, Context _context, String _searchedKey) {
		super(_fm);
		setData(_fm, _context, _searchedKey);
	}


	public void setData(FragmentManager _fm, Context _context, String _searchedKey) {
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
		mSearchedKey = _searchedKey;
	}


	@Override
	public Fragment getItem(int _arg0) {
		if (TextUtils.isEmpty(mSearchedKey)) {
			return NewsListPageFragment.newInstance(mContext, mLanguages.get(_arg0));
		} else {
			return SearchedNewsListPageFragment.newInstance(mContext, mLanguages.get(_arg0), mSearchedKey);
		}
	}


	@Override
	public int getCount() {
		return mLanguages.size();
	}
}
