package com.gmail.hasszhao.mininews.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;


public final class SearchedNewsListFragment extends NewsListFragment {

	public static final String TAG = "TAG.Searched.NewsList";
	private static final String KEY_SEARCH_KEY = "Searched.key";
	private static final String KEY_NEW_SEARCH = "Searched.new";
	private static final String KEY_NEW_SEARCH_KEY = "Searched.new.key";
	private boolean mNewSearch;
	private String mNewSearchKey;


	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		if (_savedInstanceState != null) {
			mNewSearch = _savedInstanceState.getBoolean(KEY_NEW_SEARCH);
			mNewSearchKey = _savedInstanceState.getString(KEY_NEW_SEARCH_KEY);
		}
	}


	public static SearchedNewsListFragment newInstance(Context _context, String _key) {
		Bundle args = new Bundle();
		args.putString(KEY_SEARCH_KEY, TextUtils.isEmpty(_key) ? "" : _key);
		return (SearchedNewsListFragment) SearchedNewsListFragment.instantiate(_context,
				SearchedNewsListFragment.class.getName(), args);
	}


	@Override
	public void onSaveInstanceState(Bundle _outState) {
		super.onSaveInstanceState(_outState);
		_outState.putBoolean(KEY_NEW_SEARCH, mNewSearch);
		_outState.putString(KEY_NEW_SEARCH_KEY, mNewSearchKey);
	}


	@Override
	protected String getQuery() {
		return mNewSearch ? mNewSearchKey : getArguments().getString(KEY_SEARCH_KEY);
	}


	public void searchWitNewKey(String _key) {
		mNewSearch = true;
		mNewSearchKey = _key;
		refresh();
	}


	@Override
	protected boolean canPullToLoad() {
		return false;
	}
}
