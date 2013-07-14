package com.gmail.hasszhao.mininews.fragments.basic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.gmail.hasszhao.mininews.R;


public abstract class BasicFragment extends SherlockFragment {

	protected static final int BOTTOM_IN_SEC = 1000;


	protected void openFragment(Fragment _f, String _tag) {
		FragmentTransaction trans = getChildFragmentManager().beginTransaction();
		trans.setCustomAnimations(R.anim.slide_top_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_top_out);
		trans.replace(R.id.container_error, _f, _tag).addToBackStack(_tag).commit();
	}


	protected void closeFragment(String _tag) {
		Fragment f = getChildFragmentManager().findFragmentByTag(_tag);
		if (f != null) {
			getChildFragmentManager().popBackStack(_tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			FragmentTransaction trans = getChildFragmentManager().beginTransaction();
			// trans.remove(_f);
			trans.commit();
		}
	}


	public static class ErrorFragment extends Fragment {

		private final static int LAYOUT = R.layout.fragment_error;
		private final static String KEY_TOPLINE = "error.topline";
		private final static String KEY_HEADLINE = "error.headline";
		public final static String TAG = "ErrorFragment";


		public static ErrorFragment newInstance(Context _context, String _topline, String _headline) {
			Bundle args = new Bundle();
			args.putString(KEY_TOPLINE, _topline);
			args.putString(KEY_HEADLINE, _headline);
			return (ErrorFragment) ErrorFragment.instantiate(_context, ErrorFragment.class.getName(), args);
		}


		@Override
		public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
			return _inflater.inflate(LAYOUT, _container, false);
		}


		@Override
		public void onViewCreated(View _view, Bundle _savedInstanceState) {
			super.onViewCreated(_view, _savedInstanceState);
			if (_view != null) {
				((TextView) _view.findViewById(R.id.tv_error_topline)).setText(getArguments().getString(KEY_TOPLINE));
				((TextView) _view.findViewById(R.id.tv_error_headline)).setText(getArguments().getString(KEY_HEADLINE));
			}
		}
	}
}
