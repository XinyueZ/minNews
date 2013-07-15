package com.gmail.hasszhao.mininews.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;


public final class NewsListAdapter extends BaseAdapter {

	private static final int PLACE_HOLDER = R.drawable.ic_launcher;
	private static final int LAYOUT = R.layout.news_list_item;
	private Context mContext;
	private ListNews mNewsListItems;


	public interface OnNewsClickedListener {

		void onNewsClicked(INewsListItem _newsItem);
	}

	public interface OnNewsShareListener {

		void onNewsShare(INewsListItem _newsItem);
	}


	private OnNewsClickedListener mOnNewsClickedListener;
	private OnNewsShareListener mOnNewsShareListener;


	public NewsListAdapter(Context _context, ListNews _newsListItems) {
		super();
		mContext = _context;
		mNewsListItems = _newsListItems;
	}


	public void refresh(Context _context, ListNews _newsListItems) {
		mContext = _context;
		mNewsListItems = _newsListItems;
		notifyDataSetChanged();
	}


	public void remove(int _i) {
		mNewsListItems.getPulledNewss().remove(_i);
	}


	@Override
	public int getCount() {
		return mNewsListItems.getPulledNewss().size();
	}


	@Override
	public Object getItem(int _position) {
		return mNewsListItems.getPulledNewss().get(_position);
	}


	@Override
	public long getItemId(int _position) {
		return _position;
	}


	static class ViewHolder {

		ImageView thumb;
		TextView topline;
		TextView headline;
		TextView date;
		ImageButton newsShare;


		public ViewHolder(ImageView _thumb, TextView _topline, TextView _headline, TextView _date,
				ImageButton _newsShare) {
			super();
			thumb = _thumb;
			topline = _topline;
			headline = _headline;
			date = _date;
			newsShare = _newsShare;
		}
	}


	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		ViewHolder h;
		if (_convertView == null) {
			_convertView = View.inflate(mContext, LAYOUT, null);
			_convertView.setTag(h = new ViewHolder((ImageView) _convertView.findViewById(R.id.iv_thumb),
					(TextView) _convertView.findViewById(R.id.tv_topline), (TextView) _convertView
							.findViewById(R.id.tv_headline), (TextView) _convertView.findViewById(R.id.tv_date),
					(ImageButton) _convertView.findViewById(R.id.btn_news_be_shared)));
		} else {
			h = (ViewHolder) _convertView.getTag();
		}
		final INewsListItem newsItem = mNewsListItems.getPulledNewss().get(_position);
		TaskHelper.getImageLoader().get(newsItem.getThumbUrl(),
				ImageLoader.getImageListener(h.thumb, PLACE_HOLDER, PLACE_HOLDER));
		// Log.w("mini", "Ask: " + newsItem.getThumbUrl());
		h.topline.setText(newsItem.getTopline());
		h.headline.setText(newsItem.getHeadline());
		h.date.setText(newsItem.getDate());
		h.newsShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View _v) {
				if (mOnNewsShareListener != null) {
					mOnNewsShareListener.onNewsShare(newsItem);
				}
			}
		});
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


	public void setOnNewsShareListener(OnNewsShareListener _onNewsShareListener) {
		mOnNewsShareListener = _onNewsShareListener;
	}


	public ListNews getNewsListItems() {
		return mNewsListItems;
	}
}
