package com.gmail.hasszhao.mininews.dataset;

import java.util.Calendar;

import com.gmail.hasszhao.mininews.interfaces.INewsListItem;


public final class DONews implements INewsListItem {

	private final String Title;
	private final String Preview;
	private final String Url;
	private final long Date;


	public DONews(String _title, String _preview, String _url, long _date) {
		super();
		Title = _title;
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
		c.setTimeInMillis(System.currentTimeMillis());
		return c.getTime().toString();
	}
}
