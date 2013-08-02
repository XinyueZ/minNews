package com.gmail.hasszhao.mininews.fragments.list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gmail.hasszhao.mininews.API;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.activities.MainActivity;
import com.gmail.hasszhao.mininews.adapters.NewsEndlessListAdapter;
import com.gmail.hasszhao.mininews.adapters.NewsEndlessListAdapter.ICallNext;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsBookmarkButtonClickedListener;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.OnNewsShareListener;
import com.gmail.hasszhao.mininews.adapters.NewsListAdapter.ViewHolder;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.fragments.ErrorFragment;
import com.gmail.hasszhao.mininews.fragments.ErrorFragment.ErrorType;
import com.gmail.hasszhao.mininews.fragments.ErrorFragment.IErrorResponsible;
import com.gmail.hasszhao.mininews.fragments.NewsDetailsFragment;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.fragments.dialog.AskOpenDetailsMethodFragment;
import com.gmail.hasszhao.mininews.fragments.dialog.AskOpenDetailsMethodFragment.OpenContentMethod;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.IRefreshable;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.TaskBookmark;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.tasks.TaskLoadNewsList;
import com.gmail.hasszhao.mininews.utils.ShareUtil;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;


public class NewsListPageFragment extends BasicFragment implements Listener<DOStatus>, ErrorListener,
        OnRefreshListener, OnNewsShareListener, IRefreshable, INewsListItemProvider, ICallNext, IErrorResponsible,
        OnNewsBookmarkButtonClickedListener, OnScrollListener, OnItemClickListener {

    public static final String TAG = "TAG.NewsListPageFragment";
    private static final int LAYOUT = R.layout.fragment_news_list;
    private NewsEndlessListAdapter mNewsEndlessListAdapter;
    private INewsListItem mSelectedNewsItem;

    public static NewsListPageFragment newInstance(Context _context, String _language) {
        Bundle args = new Bundle();
        args.putString(KEY_LANGUAGE, _language);
        return (NewsListPageFragment) NewsListPageFragment.instantiate(_context, NewsListPageFragment.class.getName(),
                args);
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
        super.onCreateView(_inflater, _container, _savedInstanceState);
        return _inflater.inflate(LAYOUT, _container, false);
    }

    @Override
    public void onViewCreated(View _view, Bundle _savedInstanceState) {
        super.onViewCreated(_view, _savedInstanceState);
        init();
    }

    private void init() {
        if (getListNews() != null && getListNews().getPulledNewss().size() > 0) {
            initList();
        } else {
            loadData();
        }
    }

    private void refreshBookmarkStatusMaybeFromChangingOnDetails() {
        View v = getView();
        if (v != null) {
            ListView listView = (ListView) v.findViewById(R.id.activity_googlecards_listview);
            onListIDLE(listView);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView _view, int _scrollState) {
        switch (_scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                onListIDLE(_view);
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            case OnScrollListener.SCROLL_STATE_FLING:
                break;
        }
    }

    private void onListIDLE(AbsListView _listView) {
        ListNews l = getListNews();
        if (l != null) {
            View convertView;
            ViewHolder vh;
            int first = _listView.getFirstVisiblePosition();
            int totalItemCount = _listView.getChildCount();
            for (int i = 0; i < totalItemCount; i++) {
                convertView = _listView.getChildAt(i);
                if (convertView != null && convertView.getTag() != null) {
                    vh = (ViewHolder) convertView.getTag();
                    int position = first + i;
                    List<? extends INewsListItem> items = l.getPulledNewss();
                    if (items != null) {
                        vh.bookmark.setSelected(items.get(position).isBookmarked());
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        TaskHelper.getRequestQueue().cancelAll(TaskLoadNewsList.TAG);
        mNewsEndlessListAdapter = null;
        mSelectedNewsItem = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refresh() {
        View v = getView();
        if (v != null && getListNews() != null) {
            getListNews().getPulledNewss().clear();
            // In order to let lazy-loading still work after "All-refreshing"
            // being finished.
            setListNews(null);
            mNewsEndlessListAdapter = null;
        }
        loadData();
    }

    private void loadData() {
        Activity act = getActivity();
        if (act != null) {
            callNext(1);
        }
    }

    protected String getQuery() {
        return "";
    }

    @Override
    public synchronized void onErrorResponse(VolleyError _error) {
        replaceOpenFragment(ErrorFragment.newInstance(getActivity(), ErrorType.DATA_LOADING_ERROR), ErrorFragment.TAG);
        stepDone();
    }

    @Override
    public synchronized void onResponse(DOStatus _response) {
        Activity act = getActivity();
        if (_response != null && act != null) {
            try {
                switch (_response.getCode()) {
                    case API.API_OK:
                        ListNews dataFromServer = TaskHelper.correctedByDB(_response,
                                ((App) act.getApplication()).getAppDB());
                        if (getListNews() == null) {
                            setListNews((ListNews) dataFromServer.clone());
                        } else {
                            getListNews().getPulledNewss().addAll(dataFromServer.getPulledNewss());
                        }
                        removeFragment(ErrorFragment.TAG);
                        initList();
                        break;
                    case API.API_ACTION_FAILED:
                    case API.API_SERVER_DOWN:
                        replaceOpenFragment(ErrorFragment.newInstance(getActivity(), ErrorType.SERVER_ERROR),
                                ErrorFragment.TAG);
                        break;
                }
                stepDone();
            } catch (Exception _e) {
                _e.printStackTrace();
            }
        }
    }

    private void initList() {
        Activity act = getActivity();
        if (act != null && getListNews() != null && getListNews().getPulledNewss().size() > 0) {
            // Log.d("news", "Ask: news size:" + newsList.size());
            View v = getView();
            if (v != null) {
                ListView listView = (ListView) v.findViewById(R.id.activity_googlecards_listview);
                listView.setOnItemClickListener(this);
                if (mNewsEndlessListAdapter == null) {
                    NewsListAdapter adapter = new NewsListAdapter(getActivity().getApplicationContext(), getListNews());
                    // adapter.setOnNewsClickedListener(this);
                    adapter.setOnNewsShareListener(this);
                    adapter.setOnNewsBookmarkedListener(this);
                    mNewsEndlessListAdapter = new NewsEndlessListAdapter(act.getApplicationContext(), adapter, this);
                    mNewsEndlessListAdapter.setRunInBackground(false);
                    listView.setOnScrollListener(this);
                    listView.setAdapter(mNewsEndlessListAdapter);
                } else {
                    // mAdapter.refresh(getActivity(), mNewsList);
                    mNewsEndlessListAdapter.onDataReady();
                }
            }
        }
        stepDone();
    }

    private void stepDone() {
        FragmentActivity act = getActivity();
        if (act instanceof MainActivity) {
            ((MainActivity) act).setLoadingFragmentStep(1);
            ((MainActivity) act).refreshComplete();
        }
    }

    @Override
    public void setUserVisibleHint(boolean _isVisibleToUser) {
        View view = getView();
        if (_isVisibleToUser && canPullToLoad() && view != null) {
            ListView listView = (ListView) view.findViewById(R.id.activity_googlecards_listview);
            Activity act = getActivity();
            if (act != null) {
                ((MainActivity) act).setRefreshableView(listView, this);
            }
        }
        super.setUserVisibleHint(_isVisibleToUser);
    }

    /**
     * Should pull to load or not. Default in NewsList is true.
     */
    protected boolean canPullToLoad() {
        return true;
    }

    @Override
    public void onRefreshStarted(View _view) {
        if (_view != null) {
            refresh();
        }
    }

    // @Override
    // public void onNewsClicked(INewsListItem _newsItem) {
    //
    // }
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

    private String makeShareText(INewsListItem _newsItem) {
        return new StringBuilder().append(_newsItem.getTopline()).append('\n').append("----").append('\n')
                .append(_newsItem.getURL()).toString();
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
    public INewsListItem getNewsListItem() {
        return mSelectedNewsItem;
    }

    @Override
    public void callNext(int _index) {
        Activity act = getActivity();
        if (act instanceof MainActivity) {
            new TaskLoadNewsList(act.getApplication(), Method.GET, API.GLAT, DOStatus.class, this, this, new DOCookie(
                    _index, getArguments().getString(KEY_LANGUAGE), getQuery())).execute();
        }
    }

    @Override
    public void retry() {
        callNext(getListNews() == null ? 1 : getListNews().getPulledNewss().size() + 1);
    }

    @Override
    public boolean isDataCached() {
        return getListNews() != null && getListNews().getPulledNewss().size() > 0;
    }

    @Override
    public void onNewsBookmarked(final ImageButton _button, INewsListItem _newsItem) {
        Activity act = getActivity();
        if (act != null) {
            App app = (App) act.getApplication();
            new TaskBookmark(app.getAppDB(), _newsItem, TaskBookmark.BookmarkTaskType.INSERT) {

                @Override
                protected void onSuccess() {
                    _button.setSelected(true);
                }
            }.execute();
        }
    }

    @Override
    public void onNewsBookmarkRemoved(final ImageButton _button, INewsListItem _newsItem) {
        Activity act = getActivity();
        if (act != null) {
            App app = (App) act.getApplication();
            new TaskBookmark(app.getAppDB(), _newsItem, TaskBookmark.BookmarkTaskType.DELETE) {

                @Override
                protected void onSuccess() {
                    _button.setSelected(false);
                }
            }.execute();
        }
    }

    @Override
    public void onScroll(AbsListView _view, int _firstVisibleItem, int _visibleItemCount, int _totalItemCount) {
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

    @Override
    public void onFragmentResume() {
        refreshBookmarkStatusMaybeFromChangingOnDetails();
    }
}
