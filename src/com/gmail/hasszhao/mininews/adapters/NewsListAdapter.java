package com.gmail.hasszhao.mininews.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;


public final class NewsListAdapter extends BaseAdapter {

	private static final int PLACE_HOLDER = R.drawable.ic_launcher;
	private static final int LAYOUT = R.layout.news_list_item;
	private Context mContext;
	private ListNews mNewsListItems;
	private AppDB mAppDB;


	public interface OnNewsClickedListener {

		void onNewsClicked(INewsListItem _newsItem);
	}

	public interface OnNewsShareListener {

		void onNewsShare(INewsListItem _newsItem);
	}

	public interface OnNewsBookmarkButtonClickedListener {

		void onNewsBookmarked(ImageButton _button, INewsListItem _newsItem);


		void onNewsBookmarkRemoved(ImageButton _button, INewsListItem _newsItem);
	}


	private OnNewsClickedListener mOnNewsClickedListener;
	private OnNewsShareListener mOnNewsShareListener;
	private OnNewsBookmarkButtonClickedListener mOnNewsBookmarkedListener;


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


	public static class ViewHolder {

		NetworkImageView thumb;
		TextView topline;
		TextView headline;
		TextView date;
		ImageButton newsShare;
		public ImageButton bookmark;
		ImageView banner1;
		ImageView banner2;


		private ViewHolder(NetworkImageView _thumb, TextView _topline, TextView _headline, TextView _date,
				ImageButton _newsShare, ImageButton _bookmark, ImageView _banner1, ImageView _banner2) {
			super();
			thumb = _thumb;
			topline = _topline;
			headline = _headline;
			date = _date;
			newsShare = _newsShare;
			bookmark = _bookmark;
			banner1 = _banner1;
			banner2 = _banner2;
		}
	}


	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		final ViewHolder h;
		if (_convertView == null) {
			_convertView = View.inflate(mContext, LAYOUT, null);
			_convertView.setTag(h = new ViewHolder((NetworkImageView) _convertView.findViewById(R.id.iv_thumb),
					(TextView) _convertView.findViewById(R.id.tv_topline), (TextView) _convertView
							.findViewById(R.id.tv_headline), (TextView) _convertView.findViewById(R.id.tv_date),
					(ImageButton) _convertView.findViewById(R.id.btn_news_be_shared), (ImageButton) _convertView
							.findViewById(R.id.btn_bookmark), (ImageView) _convertView
							.findViewById(R.id.iv_bunner_thumb_1), (ImageView) _convertView
							.findViewById(R.id.iv_bunner_thumb_2)));
		} else {
			h = (ViewHolder) _convertView.getTag();
		}
		final INewsListItem newsItem = mNewsListItems.getPulledNewss().get(_position);
		h.thumb.setDefaultImageResId(PLACE_HOLDER);
		h.thumb.setImageUrl(newsItem.getThumbUrl(), TaskHelper.getImageLoader());
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
		// Move to listview-listener because of blocking while scrolling the
		// list.
		// h.bookmarked.setSelected(mAppDB.isNewsBookmarked(newsItem));
		h.bookmark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View _v) {
				if (mOnNewsBookmarkedListener != null) {
					if (mAppDB.isNewsBookmarked(newsItem)) {
						mOnNewsBookmarkedListener.onNewsBookmarkRemoved(h.bookmark, newsItem);
					} else {
						mOnNewsBookmarkedListener.onNewsBookmarked(h.bookmark, newsItem);
					}
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
		if (newsItem.isHot() && newsItem.isNew()) {
			h.banner1.setVisibility(View.VISIBLE);
			h.banner1.setImageResource(R.drawable.ic_banner_hot_item);
			h.banner2.setVisibility(View.VISIBLE);
			h.banner2.setImageResource(R.drawable.ic_banner_new);
		} else {
			h.banner2.setVisibility(View.INVISIBLE);
			if (newsItem.isHot()) {
				h.banner1.setImageResource(R.drawable.ic_banner_hot_item);
			} else if (newsItem.isNew()) {
				h.banner1.setImageResource(R.drawable.ic_banner_new);
			} else {
				h.banner1.setVisibility(View.INVISIBLE);
			}
		}
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


	public synchronized void setOnNewsBookmarkedListener(OnNewsBookmarkButtonClickedListener _onNewsBookmarkedListener) {
		mOnNewsBookmarkedListener = _onNewsBookmarkedListener;
	}


	public synchronized void setAppDB(AppDB _appDB) {
		mAppDB = _appDB;
	}
}
