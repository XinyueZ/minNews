package com.gmail.hasszhao.mininews.dataset;

import android.text.format.DateUtils;

import com.gmail.hasszhao.mininews.interfaces.INewsListItem;


public final class DONews implements INewsListItem {

	private static final int ONE_HOUR = 60 * 60 * 1000;
	private final String Title;
	private final String Content;
	private final String Preview;
	private final String Url;
	private final String ImageUrl;
	private final long Date;
	private final boolean IsHot;
	// not from feed
	private boolean mBookmarked;


	public DONews(String _title, String _content, String _preview, String _url, String _imageUrl, long _date,
			boolean _isHot) {
		super();
		Title = _title;
		Content = _content;
		Preview = _preview;
		Url = _url;
		ImageUrl = _imageUrl;
		Date = _date;
		IsHot = _isHot;
	}


	@Override
	public String getTopline() {
		return Title;
	}


	@Override
	public String getHeadline() {
		return Preview;
	}


	@Override
	public String getDate() {
		return DateUtils.getRelativeTimeSpanString(Date).toString();
	}


	@Override
	public String getURL() {
		return Url;
	}


	@Override
	public String getFullContent() {
		return Content;
	}


	@Override
	public String getThumbUrl() {
		return ImageUrl;
	}


	@Override
	public boolean isHot() {
		return IsHot;
	}


	@Override
	public boolean isNew() {
		return System.currentTimeMillis() - Date <= ONE_HOUR;
	}


	@Override
	public long getTime() {
		return Date;
	}


	@Override
	public boolean isBookmarked() {
		return mBookmarked;
	}


	@Override
	public void setBookmark(boolean _bookmarked) {
		mBookmarked = _bookmarked;
	}
}
