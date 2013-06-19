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

	private static final int LAYOUT = R.layout.news_list_item;
	private final Context mContext;
	private final List<INewsListItem> mNewsListItems;


	public NewsListAdapter(Context _context, List<INewsListItem> _newsListItems) {
		super();
		mContext = _context;
		mNewsListItems = _newsListItems;
	}


	public void remove(int _i) {
		mNewsListItems.remove(_i);
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
		TextView date;


		public ViewHolder(TextView _topline, TextView _headline, TextView _date) {
			super();
			topline = _topline;
			headline = _headline;
			date = _date;
		}
	}


	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		ViewHolder h;
		if (_convertView == null) {
			_convertView = View.inflate(mContext, LAYOUT, null);
			_convertView.setTag(h = new ViewHolder((TextView) _convertView.findViewById(R.id.tv_topline),
					(TextView) _convertView.findViewById(R.id.tv_headline), (TextView) _convertView
							.findViewById(R.id.tv_date)));
		} else {
			h = (ViewHolder) _convertView.getTag();
		}
		INewsListItem newsItem = mNewsListItems.get(_position);
		h.topline.setText(newsItem.getTopline());
		h.headline.setText(newsItem.getHeadline());
		h.date.setText(newsItem.getDate());
		return _convertView;
	}
}
