package com.gmail.hasszhao.mininews.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;


public final class TabFactory implements TabHost.TabContentFactory {

    private static final int LAYOUT_TAB = R.layout.tab_pagers;
	private final Context mContext;


	public interface OnTabClosedListener {

		void onTabClosed(String _tabText);
	}


	OnTabClosedListener mOnTabClosedListener;


	public TabFactory(Context _context) {
		super();
		mContext = _context; 
	}


	@Override
	public View createTabContent(String tag) {
		View v = new View(mContext);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}


	public View createTabView(final String _tabText, boolean _canClose) {
		View view = View.inflate(mContext, LAYOUT_TAB, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_tab_text);
		view.findViewById(R.id.btn_close_tab).setVisibility(_canClose ? View.VISIBLE : View.INVISIBLE);
		view.findViewById(R.id.btn_close_tab).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View _v) {
				if (mOnTabClosedListener != null) {
					mOnTabClosedListener.onTabClosed(_tabText);
				}
			}
		});
		tv.setText(_tabText);
		return view;
	}


	public void setOnTabClosedListener(OnTabClosedListener _onTabClosedListener) {
		mOnTabClosedListener = _onTabClosedListener;
	}
}
