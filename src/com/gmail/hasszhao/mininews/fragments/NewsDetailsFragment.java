package com.gmail.hasszhao.mininews.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.ISharable;


public final class NewsDetailsFragment extends BasicFragment implements ISharable {

	private static final int LAYOUT = R.layout.fragment_news_details;
	private static final String KEY_URL = "url";
	private static final String KEY_TOPIC = "topic";
	public static final String KEY_FULL_CONTENT = "full.content";
	public static final String TAG = "TAG.NewsDetailsFragment";


	public static NewsDetailsFragment newInstance(Context _context, String _topic, String _fullContent, String _url) {
		Bundle args = new Bundle();
		args.putString(KEY_URL, _url);
		args.putString(KEY_TOPIC, _topic);
		args.putString(KEY_FULL_CONTENT, _fullContent);
		return (NewsDetailsFragment) NewsDetailsFragment.instantiate(_context, NewsDetailsFragment.class.getName(),
				args);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onActivityCreated(Bundle _savedInstanceState) {
		super.onActivityCreated(_savedInstanceState);
		View v = getView();
		if (v != null) {
			((TextView) v.findViewById(R.id.tv_details_topline)).setText(Html.fromHtml(getArguments().getString(
					KEY_TOPIC)));
			((TextView) v.findViewById(R.id.tv_details_full_content)).setText(Html.fromHtml(
					getArguments().getString(KEY_FULL_CONTENT), new ImageGetter() {

						@SuppressWarnings("finally")
						@Override
						public Drawable getDrawable(String _source) {
							Log.d("min", "Ask: image[" + _source + "]");
							Drawable drawable = null;
							URL url = null;
							try {
								if (!TextUtils.isEmpty(_source)) {
									url = new URL(_source);
									drawable = Drawable.createFromStream(url.openStream(), "");
									drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
											drawable.getIntrinsicHeight());
								}
							} catch (MalformedURLException _e) {
								_e.printStackTrace();
							} catch (IOException _e) {
								_e.printStackTrace();
							} catch (NullPointerException _e) {
								_e.printStackTrace();
							} finally {
								return drawable;
							}
						}
					}, null));
			// ((TextView)
			// v.findViewById(R.id.tv_details_topline)).setText(Util.linkifyHtml(
			// getArguments().getString(KEY_TOPIC), Linkify.ALL));
			// ((TextView)
			// v.findViewById(R.id.tv_details_full_content)).setText(Util.linkifyHtml(getArguments()
			// .getString(KEY_FULL_CONTENT), Linkify.ALL));
		}
	}


	@Override
	public String getSubject() {
		return getString(R.string.app_name);
	}


	@Override
	public String getText() {
		return new StringBuilder().append(getArguments().getString(KEY_TOPIC)).append('\n').append("----").append('\n')
				.append(getArguments().getString(KEY_URL)).toString();
	}
}
