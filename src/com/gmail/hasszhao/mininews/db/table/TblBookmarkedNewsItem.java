package com.gmail.hasszhao.mininews.db.table;

public interface TblBookmarkedNewsItem {


	String TABLE_NAME = "bookmarked_news_items";
	String COL_URL = "Url";
	String COL_TITLE = "Title";
	String COL_CONTENT = "Content";
	String COL_PREVIEW = "Preview";
	String COL_IMAGE_URL = "ImageUrl";
	String COL_DATE = "Date";
	String COL_IS_HOT = "IsHot"; 
	String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + 
			    COL_URL + " TEXT PRIMARY KEY, " +
			    COL_TITLE + " TEXT, " +
			    COL_CONTENT + " TEXT, " +
			    COL_PREVIEW + " TEXT, " +
			    COL_IMAGE_URL + " TEXT, " +
			    COL_DATE + " INTEGER, " +
			    COL_IS_HOT + " INTEGER " + 
			");";
	String STMT_INSERT = "INSERT INTO " + TABLE_NAME + " ("
			+ COL_URL + "," 
			+ COL_TITLE + ","  
			+ COL_CONTENT + ","  
			+ COL_PREVIEW + ","  
			+ COL_IMAGE_URL + ","  
			+ COL_DATE + "," 
 + COL_IS_HOT
			+ ")" +
			" VALUES (?,?,?,?,?,?,?)";
	String STMT_DELETE_BY_URL = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_URL + "=?";
	String STMT_SELECT_BY_URL = "SELECT " + COL_URL + " FROM " + TABLE_NAME + " WHERE " + COL_URL + "=?";
	String STMT_SELECT_ALL = "SELECT " + COL_URL + " FROM " + TABLE_NAME;
}
