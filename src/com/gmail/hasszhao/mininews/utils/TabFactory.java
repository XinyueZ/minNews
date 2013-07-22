package com.gmail.hasszhao.mininews.utils;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;


public final class TabFactory implements TabHost.TabContentFactory {

	private static final int LAYOUT_TAB = R.layout.tab_pagers;
	private final Context mContext;


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


	public View createTabView(final String _tabText) {
		View view = View.inflate(mContext, LAYOUT_TAB, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_tab_text);
		tv.setText(_tabText);
		return view;
	}
}
