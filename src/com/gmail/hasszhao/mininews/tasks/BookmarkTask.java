package com.gmail.hasszhao.mininews.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.utils.Util;


public abstract class BookmarkTask extends AsyncTask<Void, String, String> {

	public enum BookmarkTaskType {
		INSERT, DELETE;
	}


	private final AppDB mDB;
	private final INewsListItem mNewsItem;
	private final BookmarkTaskType mType;


	public BookmarkTask(AppDB _db, INewsListItem _newsItem, BookmarkTaskType _type) {
		super();
		mDB = _db;
		mNewsItem = _newsItem;
		mType = _type;
	}


	@Override
	protected String doInBackground(Void... _params) {
		String _msg = null;
		switch (mType) {
			case INSERT:
				if (mDB.insertNewsItem(mNewsItem)) {
					_msg = mDB.getContext().getString(R.string.action_news_bookmarked);
				}
				break;
			case DELETE:
				if (mDB.deleteNewsItemByUrl(mNewsItem)) {
					_msg = mDB.getContext().getString(R.string.action_news_bookmark_removed);
				}
				break;
		}
		return _msg;
	}


	@Override
	protected void onPostExecute(String _result) {
		if (!TextUtils.isEmpty(_result)) {
			Util.showShortToast(mDB.getContext(), _result);
			onSuccess();
		}
	}


	protected abstract void onSuccess();
}
