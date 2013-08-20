package com.gmail.hasszhao.mininews.fragments.details;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.activities.MainActivity;
import com.gmail.hasszhao.mininews.dataset.DOReadNewsDetails;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.IOpenInBrowser;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.TaskBookmark;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.tasks.TaskReadDetails;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.views.WrapImageTextView;

import java.util.List;


public final class NewsDetailsPageFragment extends BasicFragment implements IOpenInBrowser, ImageLoader.ImageListener,
		View.OnClickListener, ISharable {

	public static final String TAG = "TAG.NewsDetailsPageFragment";
	public static final String KEY_INDEX = "index";
	private static final int LAYOUT = R.layout.fragment_news_details;
	private boolean mRestoredPosition = false;


	public static NewsDetailsPageFragment newInstance(Context _context, Fragment _target, int _index) {
		Bundle args = new Bundle();
		args.putInt(KEY_INDEX, _index);
		NewsDetailsPageFragment f = (NewsDetailsPageFragment) NewsDetailsPageFragment.instantiate(_context,
				NewsDetailsPageFragment.class.getName(), args);
		f.setTargetFragment(_target, 0);
		return f;
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (getUserVisibleHint() && view != null) {
			loadDetails(view);
		}
	}


	private INewsListItem getNewsItem() {
		if (getTargetFragment() instanceof INewsListItemProvider) {
			INewsListItemProvider provider = (INewsListItemProvider) getTargetFragment();
			int index = getArguments().getInt(KEY_INDEX);
			List<? extends INewsListItem> list = provider.getList();
			if (list != null && list.size() > 0) {
				return list.get(index);
			} else {
				return null;
			}
		}
		return null;
	}


	private void loadDetails(final View _view) {
		final INewsListItem item = getNewsItem();
		Activity act = getActivity();
		if (act instanceof MainActivity && item != null) {
			((TextView) _view.findViewById(R.id.tv_details_topline)).setText(Html.fromHtml(item.getTopline()));
			loadHeadline(item);
			loadDetails((MainActivity) act, _view, item);
			refreshBookmarkButton(_view, item);
			_view.findViewById(R.id.iv_open_photo).setOnClickListener(this);
		}
	}


	private void showProgressIndicator() {
		View view = getView();
		if (view != null) {
			TextView details = (TextView) view.findViewById(R.id.tv_details_full_content);
			if (TextUtils.isEmpty(details.getText())) {
				view.findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
			}
		}
	}


	private void hiddenProgressIndicator() {
		View view = getView();
		if (view != null) {
			view.findViewById(R.id.pb_loading).setVisibility(View.GONE);
		}
	}


	private void loadDetails(final MainActivity _activity, final View _view, final INewsListItem _item) {
		new TaskReadDetails(((App) _activity.getApplication()).getAppDB()) {

			@Override
			protected void onPreExecute() {
				showProgressIndicator();
			}


			@Override
			protected void onPostExecute(final DOReadNewsDetails _result) {
				mRestoredPosition = false;
				((TextView) _view.findViewById(R.id.tv_details_full_content)).setText(_result.getContent());
				_view.findViewById(R.id.btn_visit_article_site).setOnClickListener(NewsDetailsPageFragment.this);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					_view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

						@Override
						public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
								int oldTop, int oldRight, int oldBottom) {
							restoreLastReadingPosition(_activity, _result, _view);
							_view.removeOnLayoutChangeListener(this);
						}
					});
				} else {
					_view.postDelayed(new Runnable() {

						@Override
						public void run() {
							restoreLastReadingPosition(_activity, _result, _view);
						}
					}, 1500);
				}
				// restoreLastReadingPosition(_activity, _result, _view);
				hiddenProgressIndicator();
			}
		}.execute(_item);
	}


	private void restoreLastReadingPosition(Activity _act, DOReadNewsDetails _result, View _view) {
		if (!mRestoredPosition) {
			if (0 < _result.getLastPosition()) {
				// Util.showShortToast(_act.getApplicationContext(),
				// R.string.msg_move_to_the_last_position);
				((ScrollView) _view.findViewById(R.id.sv_root)).scrollTo(0, (int) _result.getLastPosition());
				mRestoredPosition = true;
			}
		}
	}


	private void loadHeadline(INewsListItem _item) {
		int maxW = (int) getResources().getDimension(R.dimen.thumb_width);
		int maxH = (int) getResources().getDimension(R.dimen.thumb_height);
		TaskHelper.getImageLoader().get(_item.getThumbUrl(), this, maxW, maxH);
	}


	private void refreshBookmarkButton(View _v, final INewsListItem _item) {
		if (_item != null) {
			ImageButton bookmark = (ImageButton) _v.findViewById(R.id.btn_bookmark);
			bookmark.setOnClickListener(this);
			bookmark.setSelected(_item.isBookmarked());
		}
	}


	@Override
	public void onResponse(ImageLoader.ImageContainer _response, boolean _isImmediate) {
		showHeadline(_response);
	}


	private void showHeadline(ImageLoader.ImageContainer _response) {
		INewsListItem item = getNewsItem();
		View v = getView();
		if (v != null && _response != null && _response.getBitmap() != null) {
			ImageView iv = (ImageView) v.findViewById(R.id.iv_thumb);
			iv.setImageBitmap(_response.getBitmap());
		}
		if (item != null && v != null) {
			WrapImageTextView tv = (WrapImageTextView) v.findViewById(R.id.tv_details_headline_with_image);
			tv.setVisibility(View.VISIBLE);
			tv.setTextSize((int) getResources().getDimension(R.dimen.font_size_details_headline));
			tv.setText(Html.fromHtml(item.getHeadline()));
			tv.invalidate();
		}
	}


	private void saveReadContentPosition() {
		INewsListItem item = getNewsItem();
		Activity act = getActivity();
		View v = getView();
		if (act instanceof MainActivity && v != null && item != null) {
			AppDB db = ((App) act.getApplication()).getAppDB();
			TextView details = (TextView) v.findViewById(R.id.tv_details_full_content);
			ScrollView scrollView = (ScrollView) v.findViewById(R.id.sv_root);
			if (!TextUtils.isEmpty(details.getText())) {
				db.refreshNewsDetails(item.getURL(), details.getText().toString(), scrollView.getScrollY());
			}
		}
	}


	@Override
	public void onErrorResponse(VolleyError error) {
	}


	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
			case R.id.btn_visit_article_site:
				openInBrowser();
				break;
			case R.id.btn_bookmark:
				bookmarkHandling(_v);
				break;
			case R.id.iv_open_photo:
				Util.showShortToast(getActivity(), "test");
				break;
		}
	}


	private void bookmarkHandling(final View _v) {
		Activity act = getActivity();
		final INewsListItem item = getNewsItem();
		if (act != null && item != null) {
			AppDB db = ((App) act.getApplication()).getAppDB();
			if (item.isBookmarked()) {
				// Delete
				new TaskBookmark(db, item, TaskBookmark.BookmarkTaskType.DELETE) {

					@Override
					protected void onSuccess() {
						_v.setSelected(false);
					}
				}.execute();
			} else {
				// Add
				new TaskBookmark(db, item, TaskBookmark.BookmarkTaskType.INSERT) {

					@Override
					protected void onSuccess() {
						_v.setSelected(true);
					}
				}.execute();
			}
		}
	}


	public void openInBrowser() {
		Activity act = getActivity();
		if (act != null) {
			INewsListItem item = getNewsItem();
			if (item != null) {
				Util.openUrl(act, item.getURL());
			}
		}
	}


	@Override
	public void setUserVisibleHint(boolean _isVisibleToUser) {
		super.setUserVisibleHint(_isVisibleToUser);
		View view = getView();
		if (_isVisibleToUser && view != null) {
			loadDetails(view);
			INewsListItem item = getNewsItem();
			if (item != null) {
				refreshBookmarkButton(view, item);
			}
		} else {
			saveReadContentPosition();
		}
	}


	@Override
	public String getSharedSubject() {
		return getString(R.string.app_name);
	}


	@Override
	public String getSharedText() {
		INewsListItem item = getNewsItem();
		if (item != null) {
			return new StringBuilder().append(item.getTopline()).append('\n').append("----").append('\n')
					.append(item.getURL()).toString();
		}
		return null;
	}
}
