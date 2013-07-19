package com.gmail.hasszhao.mininews.tasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DOStatus;


public final class TaskLoadNewsList extends AbstractGsonRequest<DOStatus> {

	public static final String TAG = TaskLoadNewsList.class.getName();
	private final DOCookie mCookie;


	public TaskLoadNewsList(Context _context, int _method, String _url, Class<DOStatus> _clazz,
			Listener<DOStatus> _listener,
			ErrorListener _errorListener, DOCookie _cookie) {
		super(_context, Method.GET, _url, _clazz, _listener, _errorListener);
		setTag(TAG);
		setShouldCache(true);
		mCookie = _cookie;
	}


	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}
		headers.put(COOKIE_KEY, mCookie.toString());
		return headers;
	}
}
