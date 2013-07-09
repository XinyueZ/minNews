package com.gmail.hasszhao.mininews.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.MainActivity;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.LoadDetailsContent;
import com.gmail.hasszhao.mininews.utils.Util;


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
			((TextView) v.findViewById(R.id.tv_details_headline)).setText(Html.fromHtml(item.getHeadline()));
			TextView details = (TextView) v.findViewById(R.id.tv_details_full_content);
			new LoadDetailsContent(details) {

				@Override
				protected void onPreExecute() {
					Activity act = getActivity();
					if (act instanceof MainActivity) {
						((MainActivity) act).showLoadingFragment(1);
					}
				};


				@Override
				protected void onPostExecute(org.w3c.dom.Document _result) {
					super.onPostExecute(_result);
					Activity act = getActivity();
					if (act instanceof MainActivity) {
						((MainActivity) act).setLoadingFragmentStep(1);
					}
				};
			}.execute(item.getURL());
			// Document doc = Jsoup.parse(item.getFullContent().replace("<br>",
			// "\n\n").replace("<p>", "\n\n"));
			// details.setText(doc.text());
			// Elements media = doc.select("[src]");
			// for (Element src : media) {
			// if (src.tagName().equals("img")) {
			// Log.d("mini",
			// "Ask: "
			// + String.format(" * %s: <%s> %sx%s (%s)", src.tagName(),
			// src.attr("abs:src"),
			// src.attr("width"), src.attr("height"), Util.trim(src.attr("alt"),
			// 20)));
			// }
			// }
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


	public void openInBrowser() {
		Activity act = getActivity();
		if (act != null) {
			Fragment fragment = getTargetFragment();
			if (fragment instanceof INewsListItemProvider) {
				INewsListItemProvider p = (INewsListItemProvider) fragment;
				INewsListItem item = p.getNewsListItem();
				Util.openUrl(act, item.getURL());
			}
		}
	}
}
