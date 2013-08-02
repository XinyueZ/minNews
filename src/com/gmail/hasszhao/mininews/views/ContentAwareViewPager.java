package com.gmail.hasszhao.mininews.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A ViewPager that checks for other nested view pagers and prevents horizontal
 * swipe if touch event is within the bounds of the nested pager.
 */
public class ContentAwareViewPager extends ViewPager {
	private InterceptTouchListener mListener;

	public ContentAwareViewPager(Context _context) {
		super(_context);
	}

	public ContentAwareViewPager(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
	}

	public void setOnInterceptTouchListener(InterceptTouchListener _listener) {
		mListener = _listener;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent _event) {
		if (mListener != null)
			requestDisallowInterceptTouchEvent(mListener.shouldInterceptTouch(_event));

		return super.onInterceptTouchEvent(_event);
	}

	public static interface InterceptTouchListener {
		public boolean shouldInterceptTouch(MotionEvent _ev);
	}
}
