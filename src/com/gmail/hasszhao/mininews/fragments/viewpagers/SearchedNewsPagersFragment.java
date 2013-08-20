package com.gmail.hasszhao.mininews.fragments.viewpagers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;


public final class SearchedNewsPagersFragment extends NewsPagersFragment {

	public static final String TAG = "TAG.SearchedNewsPagersFragment";


	public static SearchedNewsPagersFragment newInstance(Context _context, String _key) {
		Bundle args = new Bundle();
		args.putString(KEY_SEARCH_KEY, _key);
		return (SearchedNewsPagersFragment) SearchedNewsPagersFragment.instantiate(_context,
				SearchedNewsPagersFragment.class.getName(), args);
	}


	public static void newInstance(FragmentTabHost _tabHost, TabHost.TabSpec _spec, String _tabText) {
		Bundle args = new Bundle();
		args.putString(KEY_SEARCH_KEY, _tabText);
		_tabHost.addTab(_spec, SearchedNewsPagersFragment.class, args);
		_tabHost.setCurrentTabByTag(_tabText);
	}
}
