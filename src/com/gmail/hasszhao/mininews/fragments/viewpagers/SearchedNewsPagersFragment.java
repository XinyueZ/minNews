package com.gmail.hasszhao.mininews.fragments.viewpagers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public final class SearchedNewsPagersFragment extends NewsPagersFragment {

	public static final String TAG = "TAG.SearchedNewsPagersFragment";


	public static SearchedNewsPagersFragment newInstance(Context _context, String _key) {
		Bundle args = new Bundle();
		args.putString(KEY_SEARCH_KEY, _key);
		return (SearchedNewsPagersFragment) SearchedNewsPagersFragment.instantiate(_context,
				SearchedNewsPagersFragment.class.getName(), args);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return super.onCreateView(_inflater, _container, _savedInstanceState);
	}
}
