package com.gmail.hasszhao.mininews.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.activities.MainActivity;
import com.gmail.hasszhao.mininews.dataset.DOReadNewsDetails;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.fragments.basic.BasicFragment;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItemProvider;
import com.gmail.hasszhao.mininews.interfaces.ISharable;
import com.gmail.hasszhao.mininews.tasks.TaskBookmark;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.tasks.TaskReadDetails;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gmail.hasszhao.mininews.views.WrapImageTextView;


public final class NewsDetailsFragment extends BasicFragment implements ISharable, ImageListener, OnClickListener {

    public static final String TAG = "TAG.NewsDetailsFragment";
    private static final int LAYOUT = R.layout.fragment_news_details;
    private boolean mRestoredPosition = false;

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

    private void loadDetails(final View _view) {
        Activity act = getActivity();
        if (act instanceof MainActivity) {
            Fragment fragment = getTargetFragment();
            if (fragment instanceof INewsListItemProvider) {
                INewsListItemProvider p = (INewsListItemProvider) fragment;
                final INewsListItem item = p.getNewsListItem();
                ((TextView) _view.findViewById(R.id.tv_details_topline)).setText(Html.fromHtml(item.getTopline()));
                loadHeadline(item);
                loadDetails((MainActivity) act, _view, item);
                refreshBookmarkButton(_view, item);
                _view.findViewById(R.id.iv_open_photo).setOnClickListener(this);
            }
        }
    }

    private void loadDetails(final MainActivity _activity, final View _view, final INewsListItem _item) {
        new TaskReadDetails(((App) _activity.getApplication()).getAppDB()) {
            @Override
            protected void onPreExecute() {
                ((MainActivity) _activity).showLoadingFragment(1);
            }

            @Override
            protected void onPostExecute(final DOReadNewsDetails _result) {
                mRestoredPosition = false;
                ((TextView) _view.findViewById(R.id.tv_details_full_content)).setText(_result.getContent());
                _view.findViewById(R.id.btn_visit_article_site).setOnClickListener(NewsDetailsFragment.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    _view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            restoreLastReadingPosition(_activity, _result, _view);
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
                ((MainActivity) _activity).setLoadingFragmentStep(1);
            }

        }.execute(_item);
    }

    private void restoreLastReadingPosition(Activity _act, DOReadNewsDetails _result, View _view) {
        if (!mRestoredPosition) {
            if (0 < _result.getLastPosition()) {
                Util.showShortToast(_act.getApplicationContext(), R.string.msg_move_to_the_last_position);
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
    public void onStop() {
        saveReadContentPosition();
        super.onStop();
    }

    private void saveReadContentPosition() {
        Fragment fragment = getTargetFragment();
        if (fragment instanceof INewsListItemProvider) {
            INewsListItemProvider p = (INewsListItemProvider) fragment;
            INewsListItem item = p.getNewsListItem();
            Activity act = getActivity();
            View v = getView();
            if (act instanceof MainActivity && v != null) {
                AppDB db = ((App) act.getApplication()).getAppDB();
                TextView details = (TextView) v.findViewById(R.id.tv_details_full_content);
                ScrollView scrollView = (ScrollView) v.findViewById(R.id.sv_root);
                if (!TextUtils.isEmpty(details.getText())) {
                    db.refreshNewsDetails(item.getURL(), details.getText().toString(), scrollView.getScrollY());
                }
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
            case R.id.iv_open_photo:
                Util.showShortToast(getActivity(),"test");
                break;
        }
    }

    private void bookmarkHandling(final View _v, final INewsListItem _item, Activity _act) {
        if (_act != null) {
            AppDB db = ((App) _act.getApplication()).getAppDB();
            if (_item.isBookmarked()) {
                // Delete
                new TaskBookmark(db, _item, TaskBookmark.BookmarkTaskType.DELETE) {

                    @Override
                    protected void onSuccess() {
                        _v.setSelected(false);
                    }
                }.execute();
            } else {
                // Add
                new TaskBookmark(db, _item, TaskBookmark.BookmarkTaskType.INSERT) {

                    @Override
                    protected void onSuccess() {
                        _v.setSelected(true);
                    }
                }.execute();
            }
        }
    }

    private void openArticleSite(Fragment _fragment, INewsListItem _item) {
        if (_fragment instanceof INewsListItemProvider) {
            Activity act = getActivity();
            if (act != null) {
                Util.openUrl(act, _item.getURL());
            }
        }
    }

    @Override
    public void onFragmentResume() {
        View v = getView();
        if (v != null) {
            Fragment fragment = getTargetFragment();
            INewsListItemProvider p = (INewsListItemProvider) fragment;
            final INewsListItem item = p.getNewsListItem();
            refreshBookmarkButton(v, item);
        }
    }
}
