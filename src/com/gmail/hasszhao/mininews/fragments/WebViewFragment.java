package com.gmail.hasszhao.mininews.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gmail.hasszhao.mininews.MainActivity;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.ISharable;


public final class WebViewFragment extends Fragment implements ISharable {

	private static final int LAYOUT = R.layout.fragment_webview;
	public static final String KEY_URL = "url";
	private static final String KEY_TOPIC = "topic";
	public static final String TAG = "TAG.webview";


	public static WebViewFragment newInstance(Context _context, String _url, String _topic) {
		Bundle args = new Bundle();
		args.putString(KEY_URL, _url);
		args.putString(KEY_TOPIC, _topic);
		return (WebViewFragment) WebViewFragment.instantiate(_context, WebViewFragment.class.getName(), args);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(final View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			WebView webview = (WebView) _view.findViewById(R.id.wv_content);
			WebSettings settings = webview.getSettings();
			settings.setJavaScriptEnabled(true);
			settings.setDomStorageEnabled(true);
			settings.setBuiltInZoomControls(true);
			webview.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageStarted(WebView _webview, String _url, Bitmap _favicon) {
					super.onPageStarted(_webview, _url, _favicon);
					// showSpinner();
					_view.findViewById(R.id.pb_loading_url).setVisibility(View.VISIBLE);
				}


				@Override
				public void onPageFinished(WebView _webview, String _url) {
					super.onPageFinished(_webview, _url);
					// hiddenSpinner();
					_view.findViewById(R.id.pb_loading_url).setVisibility(View.GONE);
				}


				@Override
				public boolean shouldOverrideUrlLoading(WebView _view, String _url) {
					return false;
				}
			});
			if (_savedInstanceState == null) {
				load();
			} else {
				reload(_savedInstanceState);
			}
		}
	}


	@Override
	public void onSaveInstanceState(Bundle _outState) {
		super.onSaveInstanceState(_outState);
		_outState.putString(KEY_URL, getCurrentWebUrl());
	}


	private void load() {
		View v = getView();
		if (v != null) {
			String url = getArguments().getString(KEY_URL);
			if (!TextUtils.isEmpty(url)) {
				((WebView) v.findViewById(R.id.wv_content)).loadUrl(url);
			}
		}
	}


	private void reload(Bundle _savedInstanceState) {
		View v = getView();
		if (v != null) {
			((WebView) v.findViewById(R.id.wv_content)).loadUrl(_savedInstanceState.getString(KEY_URL));
		}
	}


	private String getCurrentWebUrl() {
		View v = getView();
		if (v != null) {
			return ((WebView) v.findViewById(R.id.wv_content)).getUrl();
		}
		return null;
	}


	public void backward(View _v) {
		View v = getView();
		if (v != null) {
			WebView wv = (WebView) v.findViewById(R.id.wv_content);
			if (wv.canGoBack()) {
				wv.goBack();
			} else {
				Activity act = getActivity();
				if (act instanceof MainActivity) {
					((MainActivity) act).closePage(WebViewFragment.TAG);
				}
			}
		}
	}


	public void forward(View _v) {
		View v = getView();
		if (v != null) {
			WebView wv = (WebView) v.findViewById(R.id.wv_content);
			if (wv.canGoForward()) {
				wv.goForward();
			}
		}
	}


	public void refresh(View _v) {
		View v = getView();
		if (v != null) {
			((WebView) v.findViewById(R.id.wv_content)).reload();
		}
	}


	@Override
	public String getSubject() {
		return getString(R.string.app_name);
	}


	@Override
	public String getText() {
		return getArguments().getString(KEY_TOPIC);
	}
}
