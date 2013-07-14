package com.gmail.hasszhao.mininews.dataset;

import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class DOCookie {

	private final String mDevice;
	private final long mIndex;
	private final String mLang;
	private final String mQuery;
	private final long mDate;


	public DOCookie(long _index, String _lang, String _query) {
		super();
		mDevice = Prefs.getInstance().getDeviceId();
		mIndex = _index;
		mLang = _lang;
		mQuery = _query;
		mDate = System.currentTimeMillis();
	}


	@Override
	public String toString() {
		StringBuilder cookieBuilder = new StringBuilder();
		cookieBuilder.append("Device=").append(mDevice).append(";").append("Index=").append(mIndex).append(";")
				.append("Lang=").append(mLang).append(";").append("Query=").append(mQuery).append(";").append("Date=")
				.append(mDate);
		return cookieBuilder.toString();
	}
}
