package com.gmail.hasszhao.mininews.dataset;

import android.content.Context;

import com.gmail.hasszhao.mininews.utils.DeviceData;


public final class DOCookie {

	private final String mDevice;
	private final long mCount;
	private final String mLang;
	private final String mQuery;
	private final long mDate;


	public DOCookie(Context _context, long _count, String _lang, String _query) {
		super();
		mDevice = DeviceData.getInstance(_context).getDeviceId();
		mCount = _count;
		mLang = _lang;
		mQuery = _query;
		mDate = System.currentTimeMillis();
	}


	@Override
	public String toString() {
		StringBuilder cookieBuilder = new StringBuilder();
		cookieBuilder.append("Device=").append(mDevice).append(";").append("Count=").append(mCount).append(";")
				.append("Lang=").append(mLang).append(";").append("Query=").append(mQuery).append(";").append("Date=")
				.append(mDate);
		return cookieBuilder.toString();
	}
}
