package com.gmail.hasszhao.mininews.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;


public final class NewsListAdapter extends BaseAdapter implements OnScrollListener {

	private static final int PLACE_HOLDER = R.drawable.ic_launcher;
	private static final int LAYOUT = R.layout.news_list_item;
	private Context mContext;
	private List<? extends INewsListItem> mNewsListItems;
	private boolean mBusy = false;


	public interface OnNewsClickedListener {

		void onNewsClicked(INewsListItem _newsItem);
	}

	public interface OnNewsShareListener {

		void onNewsShare(INewsListItem _newsItem);
	}


	private OnNewsClickedListener mOnNewsClickedListener;
	private OnNewsShareListener mOnNewsShareListener;


	public NewsListAdapter(Context _context, List<? extends INewsListItem> _newsListItems) {
		super();
		mContext = _context;
		mNewsListItems = _newsListItems;
	}


	public void refresh(Context _context, List<? extends INewsListItem> _newsListItems) {
		mContext = _context;
		mNewsListItems = _newsListItems;
		notifyDataSetChanged();
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


	@Override
	public View getView(final int _position, View _convertView, ViewGroup _parent) {
		if (_convertView == null) {
			_convertView = View.inflate(mContext, LAYOUT, null);
		}
		if (!mBusy) {
			showListItem(_convertView, _position);
			_convertView.setTag(null);
		} else {
			showListItem(_convertView);
			_convertView.setTag(this);
		}
		_convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View _v) {
				if (mOnNewsClickedListener != null) {
					mOnNewsClickedListener.onNewsClicked(mNewsListItems.get(_position));
				}
			}
		});
		return _convertView;
	}


	private void showListItem(View _convertView, int _position) {
		final INewsListItem newsItem = mNewsListItems.get(_position);
		ImageView thumb = (ImageView) _convertView.findViewById(R.id.iv_thumb);
		TaskHelper.getImageLoader().get(newsItem.getThumbUrl(),
				ImageLoader.getImageListener(thumb, PLACE_HOLDER, PLACE_HOLDER));
		TextView topline = (TextView) _convertView.findViewById(R.id.tv_topline);
		TextView headline = (TextView) _convertView.findViewById(R.id.tv_headline);
		TextView date = (TextView) _convertView.findViewById(R.id.tv_date);
		ImageButton newsShare = (ImageButton) _convertView.findViewById(R.id.btn_news_be_shared);
		ImageButton bookmark = (ImageButton) _convertView.findViewById(R.id.btn_bookmark);
		topline.setText(newsItem.getTopline());
		headline.setText(newsItem.getHeadline());
		date.setText(newsItem.getDate());
		newsShare.setVisibility(View.VISIBLE);
		newsShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View _v) {
				if (mOnNewsShareListener != null) {
					mOnNewsShareListener.onNewsShare(newsItem);
				}
			}
		});
		bookmark.setVisibility(View.VISIBLE);
	}


	private void showListItem(View _convertView) {
		ImageView thumb = (ImageView) _convertView.findViewById(R.id.iv_thumb);
		thumb.setImageResource(R.drawable.ic_launcher);
		TextView topline = (TextView) _convertView.findViewById(R.id.tv_topline);
		TextView headline = (TextView) _convertView.findViewById(R.id.tv_headline);
		TextView date = (TextView) _convertView.findViewById(R.id.tv_date);
		ImageButton newsShare = (ImageButton) _convertView.findViewById(R.id.btn_news_be_shared);
		ImageButton bookmark = (ImageButton) _convertView.findViewById(R.id.btn_bookmark);
		topline.setText(R.string.title_loading);
		headline.setText(R.string.title_loading);
		date.setText(R.string.title_loading);
		newsShare.setVisibility(View.GONE);
		bookmark.setVisibility(View.GONE);
	}


	public void setOnNewsClickedListener(OnNewsClickedListener _onNewsClickedListener) {
		mOnNewsClickedListener = _onNewsClickedListener;
	}


	public void setOnNewsShareListener(OnNewsShareListener _onNewsShareListener) {
		mOnNewsShareListener = _onNewsShareListener;
	}


	@Override
	public void onScroll(AbsListView _view, int _firstVisibleItem, int _visibleItemCount, int _totalItemCount) {
	}


	@Override
	public void onScrollStateChanged(AbsListView _view, int _scrollState) {
		switch (_scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				mBusy = false;
				int first = _view.getFirstVisiblePosition();
				int count = _view.getChildCount();
				for (int i = 0; i < count; i++) {
					View convertView = _view.getChildAt(i);
					if (convertView.getTag() != null) {
						int position = first + i;
						showListItem(convertView, position);
					}
				}
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				mBusy = true;
				// int count = _view.getChildCount();
				// for (int i = 0; i < count; i++) {
				// View convertView = _view.getChildAt(i);
				// if (convertView.getTag() != null) {
				// ImageButton newsShare = (ImageButton)
				// convertView.findViewById(R.id.btn_news_be_shared);
				// ImageButton bookmark = (ImageButton)
				// convertView.findViewById(R.id.btn_bookmark);
				// newsShare.setVisibility(View.GONE);
				// bookmark.setVisibility(View.GONE);
				// }
				// }
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				mBusy = true;
				break;
		}
	}
}
