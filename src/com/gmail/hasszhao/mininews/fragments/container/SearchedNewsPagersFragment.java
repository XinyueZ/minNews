package com.gmail.hasszhao.mininews.fragments.container;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.hasszhao.mininews.activities.MainActivity;


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
		setSidebarEnable(false);
		return super.onCreateView(_inflater, _container, _savedInstanceState);
	}


	private void setSidebarEnable(boolean _enable) {
		MainActivity act = (MainActivity) getActivity();
		if (act != null) {
			act.setSidebarEnable(_enable);
		}
	}
}
