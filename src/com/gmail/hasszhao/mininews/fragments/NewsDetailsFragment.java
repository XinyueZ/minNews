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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.activities.MainActivity;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.IRefreshNewsListListener;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.TaskBookmark;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.tasks.TaskLoadDetailsContent;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.views.WrapImageTextView;
import com.gravity.goose.Article;


public final class NewsDetailsFragment extends BasicFragment implements ISharable, ImageListener, OnClickListener {

	private static final int LAYOUT = R.layout.fragment_news_details;
	public static final String TAG = "TAG.NewsDetailsFragment";


	public static NewsDetailsFragment newInstance(Context _context, Fragment _target) {
		NewsDetailsFragment f = (NewsDetailsFragment) NewsDetailsFragment.instantiate(_context,
				NewsDetailsFragment.class.getName());
		f.setTargetFragment(_target, 0);
		return f;
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
			loadHeadline(item);
			TextView details = (TextView) v.findViewById(R.id.tv_details_full_content);
			Button fallbackOpenArticleSite = (Button) v.findViewById(R.id.btn_visit_article_site);
			fallbackOpenArticleSite.setOnClickListener(this);
			new TaskLoadDetailsContent(details, fallbackOpenArticleSite, item) {

				@Override
				protected void onPreExecute() {
					Activity act = getActivity();
					if (act instanceof MainActivity) {
						((MainActivity) act).showLoadingFragment(1);
					}
				};


				@Override
				protected void onPostExecute(Article _result) {
					super.onPostExecute(_result);
					Activity act = getActivity();
					if (act instanceof MainActivity) {
						((MainActivity) act).setLoadingFragmentStep(1);
					}
				};
			}.execute();
			ImageButton bookmark = (ImageButton) v.findViewById(R.id.btn_bookmark);
			if (!(fragment instanceof IRefreshNewsListListener)) {
				bookmark.setVisibility(View.INVISIBLE);
			} else {
				bookmark.setOnClickListener(this);
				bookmark.setSelected(item.isBookmarked());
			}
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
	public void onClick(final View _v) {
		Fragment fragment = getTargetFragment();
		INewsListItemProvider p = (INewsListItemProvider) fragment;
		final INewsListItem item = p.getNewsListItem();
		Activity act = getActivity();
		switch (_v.getId()) {
			case R.id.btn_visit_article_site:
				openArticleSite(fragment, item);
				break;
			case R.id.btn_bookmark:
				bookmarkHandling(_v, item, act);
				break;
		}
	}


	private void bookmarkHandling(final View _v, final INewsListItem item, Activity act) {
		if (act != null) {
			AppDB db = ((App) act.getApplication()).getAppDB();
			if (item.isBookmarked()) {
				// Delete
				new TaskBookmark(db, item, TaskBookmark.BookmarkTaskType.DELETE) {

					@Override
					protected void onSuccess() {
						_v.setSelected(false);
						Fragment f = getTargetFragment();
						if (f instanceof IRefreshNewsListListener) {
							IRefreshNewsListListener l = (IRefreshNewsListListener) f;
							l.onBookmarkRemoved();
						}
					}
				}.execute();
			} else {
				// Add
				new TaskBookmark(db, item, TaskBookmark.BookmarkTaskType.INSERT) {

					@Override
					protected void onSuccess() {
						_v.setSelected(true);
						Fragment f = getTargetFragment();
						if (f instanceof IRefreshNewsListListener) {
							IRefreshNewsListListener l = (IRefreshNewsListListener) f;
							l.onBookmarked();
						}
					}
				}.execute();
			}
		}
	}


	private void openArticleSite(Fragment fragment, INewsListItem item) {
		if (fragment instanceof INewsListItemProvider) {
			Activity act = getActivity();
			if (act != null) {
				Util.openUrl(act, item.getURL());
			}
		}
	}
}
