package com.gmail.hasszhao.mininews.dataset;

import java.util.Calendar;

import com.gmail.hasszhao.mininews.interfaces.INewsListItem;


public final class DONews implements INewsListItem {

	private final String Title;
	private final String Content;
	private final String Preview;
	private final String Url;
	private final long Date;


	public DONews(String _title, String _content, String _preview, String _url, long _date) {
		super();
		Title = _title;
		Content = _content;
		Preview = _preview;
		Url = _url;
		Date = _date;
	}


	public String getTitle() {
		return Title;
	}


	public String getPreview() {
		return Preview;
	}


	public String getUrl() {
		return Url;
	}


	public String getContent() {
		return Content;
	}


	@Override
	public String getTopline() {
		return getTitle();
	}


	@Override
	public String getHeadline() {
		return getPreview();
	}


	@Override
	public String getDate() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Date);
		return c.getTime().toString();
	}


	@Override
	public String getURL() {
		return getUrl();
	}


	@Override
	public String getFullContent() {
		return getContent();
	}
}
