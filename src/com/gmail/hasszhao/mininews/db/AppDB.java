package com.gmail.hasszhao.mininews.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.db.table.TblBookmarkedNewsItem;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.utils.BooleanUtil;


public final class AppDB {

	private final SQLiteDatabase mDB;
	private final Context mContext;

	public AppDB(Context _context) {
		DatabaseHelper dh = new DatabaseHelper(_context);
		mDB = dh.getReadableDatabase();
		mContext = _context;
	}


	public synchronized boolean insertNewsItem(INewsListItem _newsItem) {
		long rowId = -1;
		Cursor c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_BY_URL, new String[] { _newsItem.getURL() });
		if (c.getCount() < 1) {
			SQLiteStatement stm = mDB.compileStatement(TblBookmarkedNewsItem.STMT_INSERT);
			stm.bindString(1, _newsItem.getURL());
			stm.bindString(2, _newsItem.getTopline());
			stm.bindString(3, _newsItem.getFullContent());
			stm.bindString(4, _newsItem.getHeadline());
			stm.bindString(5, _newsItem.getThumbUrl());
			stm.bindLong(6, _newsItem.getTime());
			stm.bindLong(7, BooleanUtil.toLong(_newsItem.isHot()));
			rowId = stm.executeInsert();
			stm.clearBindings();
			stm.releaseReference();
		}
		c.close();
		return rowId != -1;
	}


	public synchronized boolean isNewsBookmarked(INewsListItem _newsItem) {
		Cursor c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_BY_URL, new String[] { _newsItem.getURL() });
		if (c.getCount() < 1) {
			return false;
		}
		c.close();
		return true;
	}


	public synchronized boolean deleteNewsItemByUrl(INewsListItem _newsItem) {
		int count = 0;
		if (!TextUtils.isEmpty(_newsItem.getURL())) {
			Cursor c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_BY_URL, new String[] { _newsItem.getURL() });
			if (c.getCount() > 0) {
				count = mDB.delete(TblBookmarkedNewsItem.TABLE_NAME, TblBookmarkedNewsItem.COL_URL + "=?",
						new String[] { _newsItem.getURL() });
			}
			c.close();
		}
		return count > 0;
	}


	public synchronized List<DONews> getAllBookmarkedNewsItems() {
		ArrayList<DONews> results = null;
		Cursor c = null;
		try {
			c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_ALL, null);
			int count = c.getCount();
			results = new ArrayList<DONews>(count);
			DONews news = null;
			while (c.moveToNext()) {
				news = new DONews(c.getString(c.getColumnIndex(TblBookmarkedNewsItem.COL_TITLE)), c.getString(c
						.getColumnIndex(TblBookmarkedNewsItem.COL_CONTENT)), c.getString(c
						.getColumnIndex(TblBookmarkedNewsItem.COL_PREVIEW)), c.getString(c
						.getColumnIndex(TblBookmarkedNewsItem.COL_URL)), c.getString(c
						.getColumnIndex(TblBookmarkedNewsItem.COL_IMAGE_URL)), c.getLong(c
						.getColumnIndex(TblBookmarkedNewsItem.COL_DATE)), BooleanUtil.fromLong(c.getLong(c
						.getColumnIndex(TblBookmarkedNewsItem.COL_IS_HOT))));
				results.add(news);
			}
		} catch (Exception _e) {
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return results;
	}


	public synchronized Context getContext() {
		return mContext;
	}
}
