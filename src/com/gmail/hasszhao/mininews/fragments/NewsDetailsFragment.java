package com.gmail.hasszhao.mininews.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.gmail.hasszhao.mininews.MainActivity;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.LoadDetailsContent;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.views.WrapImageTextView;


public final class NewsDetailsFragment extends BasicFragment implements ISharable, ImageListener, OnClickListener {

	private static final int LAYOUT = R.layout.fragment_news_details;
	public static final String TAG = "TAG.NewsDetailsFragment";


	public static NewsDetailsFragment newInstance(Context _context) {
		return (NewsDetailsFragment) NewsDetailsFragment.instantiate(_context, NewsDetailsFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		setSidebarEnable(false);
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


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setSidebarEnable(true);
	}


	private void setSidebarEnable(boolean _enable) {
		MainActivity act = (MainActivity) getActivity();
		if (act != null) {
			act.setSidebarEnable(_enable);
		}
	}


	private void loadDetails(View v) {
		Fragment fragment = getTargetFragment();
		if (fragment instanceof INewsListItemProvider) {
			INewsListItemProvider p = (INewsListItemProvider) fragment;
			INewsListItem item = p.getNewsListItem();
			((TextView) v.findViewById(R.id.tv_details_topline)).setText(Html.fromHtml(item.getTopline()));
			loadHeadline(item);
			TextView details = (TextView) v.findViewById(R.id.tv_details_full_content);
			Button fallback = (Button) v.findViewById(R.id.btn_visit_article_site);
			fallback.setOnClickListener(this);
			new LoadDetailsContent(details, fallback, item) {

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
			}.execute();
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


	@Override
	public void onErrorResponse(VolleyError _error) {
	}


	@Override
	public void onResponse(ImageContainer _response, boolean _isImmediate) {
		showHeadline(_response);
	}


	private void loadHeadline(INewsListItem item) {
		int maxW = (int) getResources().getDimension(R.dimen.thumb_width);
		int maxH = (int) getResources().getDimension(R.dimen.thumb_height);
		TaskHelper.getImageLoader().get(item.getThumbUrl(), this, maxW, maxH);
	}


	private void showHeadline(ImageContainer _response) {
		Fragment fragment = getTargetFragment();
		if (fragment instanceof INewsListItemProvider) {
			INewsListItemProvider p = (INewsListItemProvider) fragment;
			INewsListItem item = p.getNewsListItem();
			View v = getView();
			if (v != null && _response != null && _response.getBitmap() != null) {
				ImageView iv = (ImageView) v.findViewById(R.id.iv_thumb);
				iv.setImageBitmap(_response.getBitmap());
			}
			WrapImageTextView tv = (WrapImageTextView) v.findViewById(R.id.tv_details_headline_with_image);
			tv.setVisibility(View.VISIBLE);
			tv.setTextSize((int) getResources().getDimension(R.dimen.font_size_details_headline));
			tv.setText(Html.fromHtml(item.getHeadline()));
			tv.invalidate();
		}
	}


	@Override
	public void onClick(View _v) {
		Fragment fragment = getTargetFragment();
		if (fragment instanceof INewsListItemProvider) {
			INewsListItemProvider p = (INewsListItemProvider) fragment;
			INewsListItem item = p.getNewsListItem();
			Activity act = getActivity();
			if (act != null) {
				Util.openUrl(act, item.getURL());
			}
		}
	}
}
