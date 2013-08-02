package com.gmail.hasszhao.mininews.fragments.viewpagers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.activities.MainActivity;
import com.gmail.hasszhao.mininews.adapters.NewsListPagesAdapter;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.fragments.list.NewsListPageFragment;
import com.gmail.hasszhao.mininews.interfaces.IRefreshable;
import com.gmail.hasszhao.mininews.interfaces.OnFragmentBackStackChangedListener;
import com.gmail.hasszhao.mininews.utils.Util;
import com.viewpagerindicator.CirclePageIndicator;


public class NewsPagersFragment extends BasicFragment implements IRefreshable {

    public static final String TAG = "TAG.NewsPagersFragment";
    protected static final String KEY_SEARCH_KEY = "Searched.key";
    private static final int LAYOUT = R.layout.fragment_news_pages;
    private NewsListPagesAdapter mAdapter;

    public static NewsPagersFragment newInstance(Context _context) {
        Bundle args = new Bundle();
        args.putString(KEY_SEARCH_KEY, null);
        return (NewsPagersFragment) NewsPagersFragment.instantiate(_context, NewsPagersFragment.class.getName(), args);
    }

    public static void newInstance(FragmentTabHost _tabHost, TabHost.TabSpec _spec) {
        Bundle args = new Bundle();
        args.putString(KEY_SEARCH_KEY, null);
        _tabHost.addTab(_spec, NewsPagersFragment.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
        super.onCreateView(_inflater, _container, _savedInstanceState);
        return _inflater.inflate(LAYOUT, _container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updatePages();
    }



    /**
     * Update pages. Maybe reloading data.
     */
    public void updatePages() {
        View view = getView();
        if (view != null) {
            ViewPager vp = (ViewPager) view.findViewById(R.id.vp_news_pages);
            if (mAdapter == null) {
                mAdapter = new NewsListPagesAdapter(getChildFragmentManager(), getActivity().getApplicationContext(),
                        getArguments().getString(KEY_SEARCH_KEY));
                showLoadingFragment();
                vp.setOffscreenPageLimit(mAdapter.getCount());
                vp.setAdapter(mAdapter);
            } else {
                // showLoadingFragment();
                mAdapter.setData(getChildFragmentManager(), getActivity().getApplicationContext(), getArguments()
                        .getString(KEY_SEARCH_KEY));
                // There's a workaround, but it seems doesn't work for my case
                // that after closing sub-fragment of details.
                //
                // https://code.google.com/p/android/issues/detail?id=19001
                // mAdapter.notifyDataSetChanged();
                //
                // I still use setAdapter to refresh data.
                vp.setAdapter(mAdapter);
            }
            CirclePageIndicator indicator = (CirclePageIndicator) view.findViewById(R.id.vp_indicator);
            indicator.setViewPager(vp);
        }
    }

    private void showLoadingFragment() {
        Activity act = getActivity();
        if (act != null) {
            ((MainActivity) act).showLoadingFragment(mAdapter.getCount());
        }
    }

    /**
     * Reloading page's data
     */
    @Override
    public void refresh() {
        // To get fragment at some positions.
        //
        // http://afeilulu.wordpress.com/2012/05/11/%E8%8E%B7%E5%8F%96viewpager%E4%B8%AD%E7%9A%84viewpager/
        //
        View view = getView();
        if (mAdapter != null && view != null) {
            showLoadingFragment();
            int count = mAdapter.getCount();
            NewsListPageFragment f;
            ViewPager vp = (ViewPager) view.findViewById(R.id.vp_news_pages);
            for (int i = 0; i < count; i++) {
                f = (NewsListPageFragment) mAdapter.instantiateItem(vp, i);
                f.refresh();
            }
            Util.showLongToast(getActivity().getApplicationContext(), R.string.msg_refresh_all);
        }
    }

    @Override
    public void onFragmentResume() {
        View view = getView();
        if (mAdapter != null && view != null) {
            int count = mAdapter.getCount();
            NewsListPageFragment f;
            ViewPager vp = (ViewPager) view.findViewById(R.id.vp_news_pages);
            for (int i = 0; i < count; i++) {
                f = (NewsListPageFragment) mAdapter.instantiateItem(vp, i);
                if (f instanceof OnFragmentBackStackChangedListener) {
                    f.onFragmentResume();
                }
            }
        }
    }
}
