package com.gmail.hasszhao.mininews.interfaces;

// Solve that problem that "added" fragments can not handle onResume
// that "replaced" can.
// http://stackoverflow.com/questions/6503189/fragments-onresume-from-back-stack
public interface OnFragmentBackStackChangedListener {

	void onFragmentResume();
}
