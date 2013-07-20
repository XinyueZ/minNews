package com.gmail.hasszhao.mininews.fragments.basic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragment;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.interfaces.OnFragmentBackStackChangedListener;


public abstract class BasicFragment extends SherlockFragment implements OnFragmentBackStackChangedListener {

	protected static final String KEY_LANGUAGE = "NewsList.Page.language";
	protected static final int BOTTOM_IN_SEC = 1000;


	protected void replaceOpenFragment(Fragment _f, String _tag) {
		FragmentTransaction trans = getChildFragmentManager().beginTransaction();
		trans.setCustomAnimations(R.anim.slide_top_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_top_out);
		trans.replace(R.id.container_error, _f, _tag).commit();
		_f.setTargetFragment(this, 0);
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


	protected ListNews getListNews() {
		return ((App) getActivity().getApplication()).getListNews(getArguments().getString(KEY_LANGUAGE));
	}


	protected void setListNews(ListNews _listNews) {
		((App) getActivity().getApplication()).addListNews(getArguments().getString(KEY_LANGUAGE), _listNews);
	}


	@Override
	public void onFragmentResume() {
	}
}
