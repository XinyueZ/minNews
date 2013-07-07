package com.gmail.hasszhao.mininews.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.internal.widget.IcsProgressBar;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.fragments.basic.BasicDialogFragment;


public final class LoadingFragment extends BasicDialogFragment {

	private static final int LAYOUT = R.layout.fragment_loading;
	private static final String KEY_MAX = "LoadingFragment.max";
	private static final String KEY_PROGRESS = "LoadingFragment.progress";


	public static LoadingFragment newInstance(Context _context, int _max) {
		Bundle args = new Bundle();
		args.putInt(KEY_PROGRESS, _max);
		return (LoadingFragment) LoadingFragment.instantiate(_context, LoadingFragment.class.getName(), args);
	}


	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setCancelable(false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		View v = getView();
		if (v != null) {
			IcsProgressBar pb = (IcsProgressBar) v.findViewById(R.id.pb_loading);
			if (_savedInstanceState != null) {
				pb.setProgress(_savedInstanceState.getInt(KEY_PROGRESS));
			} else {
				pb.setProgress(0);
			}
			pb.setMax(getArguments().getInt(KEY_MAX));
		}
	}


	@Override
	public void onSaveInstanceState(Bundle _arg0) {
		super.onSaveInstanceState(_arg0);
		View v = getView();
		if (v != null) {
			_arg0.putInt(KEY_PROGRESS, ((IcsProgressBar) v.findViewById(R.id.pb_loading)).getProgress());
		}
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	public synchronized void setStep(int _step) {
		View v = getView();
		if (v != null) {
			IcsProgressBar pb = (IcsProgressBar) v.findViewById(R.id.pb_loading);
			pb.setProgress(_step);
			if (pb.getProgress() >= pb.getMax()) {
				dismiss();
			}
		}
	}
}
