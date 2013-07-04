package com.gmail.hasszhao.mininews.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.fragments.basic.BasicDialogFragment;


public final class AskOpenDetailsMethodFragment extends BasicDialogFragment {

	private static final int LAYOUT = R.layout.fragment_ask_open_details_method;


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}
}
