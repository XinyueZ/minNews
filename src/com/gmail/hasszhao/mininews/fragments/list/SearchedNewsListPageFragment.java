package com.gmail.hasszhao.mininews.fragments.list;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.utils.Util;

import org.apache.commons.collections4.keyvalue.MultiKey;


public final class SearchedNewsListPageFragment extends NewsListPageFragment {

    public static final String TAG = "TAG.Searched.NewsList";
    private static final String KEY_SEARCH_KEY = "Searched.key";

    public static SearchedNewsListPageFragment newInstance(Context _context, String _language, String _key) {
        Bundle args = new Bundle();
        args.putString(KEY_LANGUAGE, _language);
        args.putString(KEY_SEARCH_KEY, TextUtils.isEmpty(_key) ? "" : _key);
        return (SearchedNewsListPageFragment) SearchedNewsListPageFragment.instantiate(_context,
                SearchedNewsListPageFragment.class.getName(), args);
    }

    @Override
    protected String getQuery() {
        String key = getArguments().getString(KEY_SEARCH_KEY);
        if (!TextUtils.isEmpty(key)) {
            return Util.encode(key).trim();
        }
        return null;
    }

    protected ListNews getListNews() {
        MultiKey key = new  MultiKey(getArguments().getString(KEY_LANGUAGE), getQuery());
        return ((App) getActivity().getApplication()).getSearchedListNews(key);
    }

    protected void setListNews(ListNews _listNews) {
        MultiKey key = new  MultiKey(getArguments().getString(KEY_LANGUAGE), getQuery());
        ((App) getActivity().getApplication()).addSearchedListNews(key, _listNews);
    }
}
