package com.gmail.hasszhao.mininews.fragments.basic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


	protected void removeFragment(String _tag) {
		Fragment f = getChildFragmentManager().findFragmentByTag(_tag);
		if (f != null) {
			getChildFragmentManager().beginTransaction().remove(f).commit();
		}
	}


	protected ListNews getListNews() {
		return ((App) getActivity().getApplication()).getListNews(getArguments().getString(KEY_LANGUAGE));
	}


	protected void setListNews(ListNews _listNews) {
		((App) getActivity().getApplication()).addListNews(getArguments().getString(KEY_LANGUAGE), _listNews);
	}


	// ------------------------------------------------------
	// Debug info
	// ------------------------------------------------------
	@Override
	public void onFragmentResume() {
		Log.i("mini", "Fragment::onFragmentResume->" + getClass().getSimpleName());
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("mini", "Fragment::onCreate->" + getClass().getSimpleName());
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("mini", "Fragment::onAttach->" + getClass().getSimpleName());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("mini", "Fragment::onCreateView->" + getClass().getSimpleName());
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("mini", "Fragment::onActivityCreated->" + getClass().getSimpleName());
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.i("mini", "Fragment::onViewCreated->" + getClass().getSimpleName());
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.i("mini", "Fragment::onResume->" + getClass().getSimpleName());
	}


	@Override
	public void onPause() {
		super.onPause();
		Log.i("mini", "Fragment::onPause->" + getClass().getSimpleName());
	}


	@Override
	public void onStop() {
		super.onStop();
		Log.i("mini", "Fragment::onStop->" + getClass().getSimpleName());
	}


	@Override
	public void onStart() {
		super.onStart();
		Log.i("mini", "Fragment::onStart->" + getClass().getSimpleName());
	}


	@Override
	public void onDetach() {
		super.onDetach();
		Log.i("mini", "Fragment::onDetach->" + getClass().getSimpleName());
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("mini", "Fragment::onDestroy->" + getClass().getSimpleName());
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i("mini", "Fragment::onDestroyView->" + getClass().getSimpleName());
	}
}
