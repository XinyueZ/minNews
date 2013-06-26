package com.gmail.hasszhao.mininews.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;


public final class NewsListAdapter extends BaseAdapter {

	private static final int LAYOUT = R.layout.news_list_item;
	private final Context mContext;
	private final List<? extends INewsListItem> mNewsListItems;


	public interface OnNewsClickedListener {

		void onNewsClicked(INewsListItem _newsItem);
	}


	private OnNewsClickedListener mOnNewsClickedListener;


	public NewsListAdapter(Context _context, List<? extends INewsListItem> _newsListItems) {
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
		final INewsListItem newsItem = mNewsListItems.get(_position);
		h.topline.setText(newsItem.getTopline());
		h.headline.setText(newsItem.getHeadline());
		h.date.setText(newsItem.getDate());
		_convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View _v) {
				if (mOnNewsClickedListener != null) {
					mOnNewsClickedListener.onNewsClicked(newsItem);
				}
			}
		});
		return _convertView;
	}


	public void setOnNewsClickedListener(OnNewsClickedListener _onNewsClickedListener) {
		mOnNewsClickedListener = _onNewsClickedListener;
	}
}
