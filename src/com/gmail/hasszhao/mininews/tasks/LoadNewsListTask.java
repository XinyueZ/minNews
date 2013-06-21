package com.gmail.hasszhao.mininews.tasks;

import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DOStatus;


public final class LoadNewsListTask extends AbstractGsonRequest<DOStatus> {

	public LoadNewsListTask(Context _context, int _method, String _url, Class<DOStatus> _clazz,
			Listener<DOStatus> _listener, ErrorListener _errorListener) {
		super(_context, Method.GET, _url, _clazz, _listener, _errorListener);
	}


	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		headers.put(COOKIE_KEY, new DOCookie(mContext, 10, "en", "").toString());
		return headers;
	}

}
