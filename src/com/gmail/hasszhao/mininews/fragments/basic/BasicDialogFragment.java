package com.gmail.hasszhao.mininews.fragments.basic;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.gmail.hasszhao.mininews.R;


public class BasicDialogFragment extends DialogFragment {

	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setCancelable(true);
		setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.Theme_Dialog_Translucent);
	}
}
