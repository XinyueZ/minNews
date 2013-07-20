package com.gmail.hasszhao.mininews.fragments.list;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.activities.MainActivity;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsShareListener;
import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.fragments.NewsDetailsFragment;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.fragments.dialog.AskOpenDetailsMethodFragment;
import com.gmail.hasszhao.mininews.fragments.dialog.AskOpenDetailsMethodFragment.OpenContentMethod;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.TaskBookmark;
import com.gmail.hasszhao.mininews.tasks.TaskBookmark.BookmarkTaskType;
import com.gmail.hasszhao.mininews.utils.ShareUtil;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;


public final class BookmarkListFragment extends BasicFragment implements // OnNewsClickedListener,
		OnNewsShareListener, OnDismissCallback, INewsListItemProvider, OnItemClickListener {

	private static final int LAYOUT = R.layout.fragment_news_list;
	public static final String TAG = "TAG.BookmarkList";
	private NewsListAdapter mAdapter;
	private ListNews mListNews;
	private INewsListItem mSelectedNewsItem;


	public static BookmarkListFragment newInstance(Context _context) {
		return (BookmarkListFragment) BookmarkListFragment.instantiate(_context, BookmarkListFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		return _inflater.inflate(LAYOUT, _container, false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			loadBookmarkList(_view);
		}
	}


	private void loadBookmarkList(View _view) {
		Activity act = getActivity();
		if (act != null) {
			List<DONews> list = ((App) act.getApplication()).getAppDB().getAllBookmarkedNewsItems();
			if (list.size() == 0) {
				showWarningWhenListEmpty();
			} else {
				setListNews(new ListNews(list, list.size()));
				initList(_view);
			}
		}
	}


	@Override
	public void onFragmentResume() {
		View view = getView();
		if (view != null) {
			loadBookmarkList(view);
		}
	}


	@Override
	protected void setListNews(ListNews _listNews) {
		mListNews = _listNews;
	}


	@Override
	protected ListNews getListNews() {
		return mListNews;
	}


	private void initList(View _view) {
		if (getListNews() != null && getListNews().getPulledNewss().size() > 0) {
			if (_view != null) {
				ListView listView = (ListView) _view.findViewById(R.id.activity_googlecards_listview);
				listView.setOnItemClickListener(this);
				if (mAdapter == null) {
					mAdapter = new NewsListAdapter(getActivity().getApplicationContext(), getListNews());
					// mAdapter.setOnNewsClickedListener(this);
					mAdapter.setOnNewsShareListener(this);
					mAdapter.setShowBookmarkButton(false);
					supportCardAnim(listView);
				} else {
					mAdapter.refresh(getActivity(), getListNews());
				}
			}
		}
	}


	private void supportCardAnim(ListView _listView) {
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mAdapter, this));
		swingBottomInAnimationAdapter.setAbsListView(_listView);
		_listView.setAdapter(swingBottomInAnimationAdapter);
	}


	@Override
	public void onDestroyView() {
		mListNews = null;
		mAdapter = null;
		mSelectedNewsItem = null;
		super.onDestroyView();
	}


	@Override
	public void onNewsShare(final INewsListItem _newsItem) {
		new ShareUtil().openShare(this, new ISharable() {

			@Override
			public String getText() {
				return makeShareText(_newsItem);
			}


			@Override
			public String getSubject() {
				return getString(R.string.app_name);
			}
		});
	}


	private String makeShareText(INewsListItem _newsItem) {
		return new StringBuilder().append(_newsItem.getTopline()).append('\n').append("----").append('\n')
				.append(_newsItem.getURL()).toString();
	}


	// @Override
	// public void onNewsClicked(INewsListItem _newsItem) {
	//
	// }
	@Override
	public void onDismiss(AbsListView _listView, int[] _reverseSortedPositions) {
		for (final int position : _reverseSortedPositions) {
			new TaskBookmark(((App) getActivity().getApplication()).getAppDB(), getListNews().getPulledNewss().get(
					position), BookmarkTaskType.DELETE) {

				@Override
				protected void onSuccess() {
					mAdapter.remove(position);
					mAdapter.notifyDataSetChanged();
					if (mAdapter.getCount() == 0) {
						showWarningWhenListEmpty();
					}
				}
			}.execute();
			Log.d("mini", "Ask: position:" + position);
		}
	}


	@Override
	public INewsListItem getNewsListItem() {
		return mSelectedNewsItem;
	}


	@Override
	public void openDetails(OpenContentMethod _method) {
		switch (_method) {
			case IN_APP:
				openDetailsInApp();
				break;
			case IN_BROWSER:
				openDetailsInBrowser();
				break;
		}
	}


	public void openDetailsInApp() {
		Activity act = getActivity();
		if (act instanceof MainActivity) {
			Fragment f = NewsDetailsFragment.newInstance(act, this);
			((MainActivity) act).addOpenNextPage(f, NewsDetailsFragment.TAG);
		}
	}


	public void openDetailsInBrowser() {
		Activity act = getActivity();
		if (act instanceof MainActivity && mSelectedNewsItem != null) {
			Util.openUrl(act, mSelectedNewsItem.getURL());
			// act.overridePendingTransition(R.anim.hyperspace_fast_in,
			// R.anim.hyperspace_fast_out);
		}
	}


	@Override
	public void onItemClick(AdapterView<?> _arg0, View _arg1, int _arg2, long _arg3) {
		// http://cyrilmottier.com/2011/11/23/listview-tips-tricks-4-add-several-clickable-areas/
		FragmentActivity act = getActivity();
		if (act != null) {
			mSelectedNewsItem = getListNews().getPulledNewss().get(_arg2);
			if (!Prefs.getInstance().getDontAskForOpeningDetailsMethod()) {
				MainActivity.showDialogFragment(act, AskOpenDetailsMethodFragment.newInstance(this), null);
			} else {
				openDetails(OpenContentMethod.fromValue(Prefs.getInstance().getOpenDetailsMethod()));
			}
		}
	}


	private void showWarningWhenListEmpty() {
		View view = getView();
		if (view != null) {
			view.findViewById(R.id.tv_empty_bookmark_list).setVisibility(View.VISIBLE);
		}
	}
}
