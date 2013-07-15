package com.gmail.hasszhao.mininews.fragments;

import android.content.Context;
import android.os.Bundle;


public final class SearchedNewsPagersFragment extends NewsPagersFragment {

	public static final String TAG = "TAG.SearchedNewsPagersFragment";


	public static SearchedNewsPagersFragment newInstance(Context _context, String _key) {
		Bundle args = new Bundle();
		args.putString(KEY_SEARCH_KEY, _key);
		return (SearchedNewsPagersFragment) SearchedNewsPagersFragment.instantiate(_context,
				SearchedNewsPagersFragment.class.getName(), args);
	}
}
