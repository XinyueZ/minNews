package com.gmail.hasszhao.mininews.fragments.basic;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.gmail.hasszhao.mininews.R;


public class BasicDialogFragment extends DialogFragment {

	private final AnimationListener mInListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation _animation) {
		}


		@Override
		public void onAnimationRepeat(Animation _animation) {
		}


		@Override
		public void onAnimationEnd(Animation _animation) {
			View v = getView();
			if (v != null) {
				v.findViewById(R.id.rl_dialog_container).setVisibility(View.VISIBLE);
			}
		}
	};
	private final AnimationListener mOutListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation _animation) {
		}


		@Override
		public void onAnimationRepeat(Animation _animation) {
		}


		@Override
		public void onAnimationEnd(Animation _animation) {
			View v = getView();
			if (v != null) {
				v.findViewById(R.id.rl_dialog_container).setVisibility(View.GONE);
			}
			dismiss();
		}
	};


	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setCancelable(true);
		setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.Theme_Dialog_Translucent);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		View v = getView();
		if (v != null) {
			Animation in = AnimationUtils.loadAnimation(getActivity(), R.anim.hyperspace_fast_in);
			in.setAnimationListener(mInListener);
			v.findViewById(R.id.sv_root).startAnimation(in);
		}
	}


	public void close() {
		View v = getView();
		if (v != null) {
			Animation out = AnimationUtils.loadAnimation(getActivity(), R.anim.hyperspace_fast_out);
			out.setAnimationListener(mOutListener);
			v.findViewById(R.id.sv_root).startAnimation(out);
		}
	}
}
