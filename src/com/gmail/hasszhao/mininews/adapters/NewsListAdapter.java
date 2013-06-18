package com.gmail.hasszhao.mininews.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;


public final class NewsListAdapter extends BaseAdapter {

	private static final int LAYOUT = R.layout.activity_main;
	private final Context mContext;
	private final List<INewsListItem> mNewsListItems;


	public NewsListAdapter(Context _context, List<INewsListItem> _newsListItems) {
		super();
		mContext = _context;
		mNewsListItems = _newsListItems;
	}


	@Override
	public int getCount() {
		return mNewsListItems.size();
	}


	@Override
	public Object getItem(int _position) {
		return mNewsListItems.get(_position);
	}


	@Override
	public long getItemId(int _position) {
		return _position;
	}


	static class ViewHolder {

		TextView topline;
		TextView headline;


		public ViewHolder(TextView _topline, TextView _headline) {
			super();
			topline = _topline;
			headline = _headline;
		}
	}


	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		ViewHolder h;
		if (_convertView == null) {
			_convertView = View.inflate(mContext, R.layout.news_list_item, null);
			_convertView.setTag(h = new ViewHolder((TextView) _convertView.findViewById(R.id.tv_topline),
					(TextView) _convertView.findViewById(R.id.tv_headline)));
		} else {
			h = (ViewHolder) _convertView.getTag();
		}
		INewsListItem newsItem = mNewsListItems.get(_position);
		h.topline.setText(newsItem.getTopline());
		h.headline.setText(newsItem.getHeadline());
		return _convertView;
	}
}
