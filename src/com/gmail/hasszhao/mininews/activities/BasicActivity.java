package com.gmail.hasszhao.mininews.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.gmail.hasszhao.mininews.R;


public abstract class BasicActivity extends SherlockFragmentActivity {

	protected void replaceOpenFragment(Fragment _f, String _tag) {
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.setCustomAnimations(R.anim.slide_top_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_top_out);
		trans.replace(R.id.container_error, _f, _tag).commit();
	}


	protected void closeFragment(String _tag) {
		Fragment f = getSupportFragmentManager().findFragmentByTag(_tag);
		if (f != null) {
			getSupportFragmentManager().popBackStack(_tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
			// trans.remove(_f);
			trans.commit();
		}
	}
}
