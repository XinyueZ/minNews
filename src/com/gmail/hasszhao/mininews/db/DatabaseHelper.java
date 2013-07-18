package com.gmail.hasszhao.mininews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmail.hasszhao.mininews.db.table.TblBookmarkedNewsItem;


public final class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "mininews.db";
	private static final int DB_VERSION = 1;


	public DatabaseHelper(Context _context) {
		super(_context, DB_NAME, null, DB_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase _db) {
		_db.execSQL(TblBookmarkedNewsItem.SQL_CREATE);
	}


	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
		_db.execSQL("DROP TABLE IF EXISTS " + TblBookmarkedNewsItem.TABLE_NAME);
	}
}
