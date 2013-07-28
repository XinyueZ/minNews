package com.gmail.hasszhao.mininews.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.gmail.hasszhao.mininews.dataset.DONews;
import com.gmail.hasszhao.mininews.db.table.TblBookmarkedNewsItem;
import com.gmail.hasszhao.mininews.db.table.TblNewsDetail;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.utils.BooleanUtil;

import java.util.ArrayList;
import java.util.List;


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
        Cursor c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_BY_URL, new String[]{_newsItem.getURL()});
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

    public synchronized long getLastNewsDetailsPosition(String _url) {
        long pos = 0;
        Cursor c = null;
        try {
            c = mDB.rawQuery(TblNewsDetail.STMT_SELECT_POS_BY_URL, new String[]{_url});
            int count = c.getCount();
            if (count > 0) {
                while (c.moveToNext()) {
                    pos = c.getLong(c.getColumnIndex(TblNewsDetail.COL_LAST_POS));
                }
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return pos;
    }

    public synchronized String getLastNewsDetails(String _url) {
        String str = null;
        Cursor c = null;
        try {
            c = mDB.rawQuery(TblNewsDetail.STMT_SELECT_DETAILS_BY_URL, new String[]{_url});
            int count = c.getCount();
            if (count > 0) {
                while (c.moveToNext()) {
                    str = c.getString(c.getColumnIndex(TblNewsDetail.COL_CONTENT));
                }
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return str;
    }

    public synchronized boolean refreshNewsDetails(String _url, String _details, int _position) {
        boolean ret;
        Cursor c = mDB.rawQuery(TblNewsDetail.STMT_SELECT_POS_BY_URL, new String[]{_url});
        ret = c.getCount() < 1 ? insertNewsDetails(_url, _details, _position) : updatetNewsDetails(_url, _details, _position);
        c.close();
        return ret;
    }

    public synchronized boolean findNewsDetails(String _url) {
        boolean ret;
        Cursor c = mDB.rawQuery(TblNewsDetail.STMT_SELECT_POS_BY_URL, new String[]{_url});
        ret = c.getCount() > 0;
        c.close();
        return ret;
    }

    private synchronized boolean insertNewsDetails(String _url, String _details, int _position) {
        long rowId = -1;
        SQLiteStatement stm = mDB.compileStatement(TblNewsDetail.STMT_INSERT);
        stm.bindString(1, _url);
        stm.bindString(2, _details);
        stm.bindLong(3, _position);
        rowId = stm.executeInsert();
        stm.clearBindings();
        stm.releaseReference();
        return rowId != -1;
    }

    private synchronized boolean updatetNewsDetails(String _url, String _details, int _position) {
        try {
            SQLiteStatement stm = mDB.compileStatement(TblNewsDetail.STMT_UPDATE);
            stm.bindString(1, _details);
            stm.bindLong(2, _position);
            stm.bindString(3, _url);
            stm.execute();
            stm.clearBindings();
            stm.releaseReference();
        } catch (Exception _e) {
            _e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized boolean isNewsBookmarked(INewsListItem _newsItem) {
        Cursor c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_BY_URL, new String[]{_newsItem.getURL()});
        if (c.getCount() < 1) {
            return false;
        }
        c.close();
        return true;
    }

    public synchronized boolean deleteNewsItemByUrl(INewsListItem _newsItem) {
        int count = 0;
        if (!TextUtils.isEmpty(_newsItem.getURL())) {
            Cursor c = mDB.rawQuery(TblBookmarkedNewsItem.STMT_SELECT_BY_URL, new String[]{_newsItem.getURL()});
            if (c.getCount() > 0) {
                count = mDB.delete(TblBookmarkedNewsItem.TABLE_NAME, TblBookmarkedNewsItem.COL_URL + "=?",
                        new String[]{_newsItem.getURL()});
            }
            c.close();
        }
        return count > 0;
    }

    /**
     * @deprecated Do not use all bookmarked news from DB directly.
     */
    @Deprecated
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
                // It's been sure.
                news.setBookmark(true);
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
