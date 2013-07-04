package com.gmail.hasszhao.mininews.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.utils.URLImageParser;


public final class NewsDetailsFragment extends BasicFragment implements ISharable {

	private static final int LAYOUT = R.layout.fragment_news_details;
	public static final String TAG = "TAG.NewsDetailsFragment";


	public static NewsDetailsFragment newInstance(Context _context) {
		return (NewsDetailsFragment) NewsDetailsFragment.instantiate(_context, NewsDetailsFragment.class.getName());
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
			loadDetails(v);
		}
	}


	private void loadDetails(View v) {
		Fragment fragment = getTargetFragment();
		if (fragment instanceof INewsListItemProvider) {
			INewsListItemProvider p = (INewsListItemProvider) fragment;
			INewsListItem item = p.getNewsListItem();
			((TextView) v.findViewById(R.id.tv_details_topline)).setText(Html.fromHtml(item.getTopline()));
			TextView details = (TextView) v.findViewById(R.id.tv_details_full_content);
			URLImageParser parser = new URLImageParser(details);
			Spanned htmlSpan = Html.fromHtml(item.getFullContent(), parser, null);
			details.setText(htmlSpan);
		}
	}


	@Override
	public String getSubject() {
		return getString(R.string.app_name);
	}


	@Override
	public String getText() {
		Fragment fragment = getTargetFragment();
		if (fragment instanceof INewsListItemProvider) {
			INewsListItemProvider p = (INewsListItemProvider) fragment;
			INewsListItem item = p.getNewsListItem();
			return new StringBuilder().append(item.getTopline()).append('\n').append("----").append('\n')
					.append(item.getURL()).toString();
		}
		return null;
	}
}
