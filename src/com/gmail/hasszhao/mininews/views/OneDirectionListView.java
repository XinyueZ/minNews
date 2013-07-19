package com.gmail.hasszhao.mininews.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class OneDirectionListView extends ListView {
    private GestureDetector mGestureDetector;
    View.OnTouchListener mGestureListener;
    private boolean mAllowTouch = true;

    public OneDirectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	mGestureDetector.onTouchEvent(ev);
		if (mAllowTouch)
			return super.onInterceptTouchEvent(ev);
		return false;
    }

    // Return false if we're scrolling in the x direction  
    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        	if(Math.abs(distanceY) > Math.abs(distanceX)) {
            	mAllowTouch = true;
                return true;
            }
            mAllowTouch = false;
            return false;
        }
    }
}