package com.gmail.hasszhao.mininews.db.table;

public interface TblLastFeeds {
    String TABLE_NAME = "last_feeds";
    String COL_URL = "Url";
    String COL_LANG = "Language";
    String COL_FEEDS = "Feeds";
    String COL_CREATED_TIME = "Created_Time";
    String SQL_CREATE = new StringBuilder().append("CREATE TABLE ").append(TABLE_NAME).append(" (").append(COL_URL).append(" TEXT PRIMARY KEY, ").append(COL_URL).append(" TEXT, ").append(COL_LANG).append(" TEXT, ").append(COL_FEEDS).append(" TEXT ").append(");").toString();
    String STMT_INSERT = new StringBuilder().append("INSERT INTO ").append(TABLE_NAME).append(" (").append(COL_URL).append(",").append(COL_LANG).append(",").append(COL_FEEDS).append(")").append(" VALUES (?,?,?)").toString();
    String STMT_UPDATE = new StringBuilder().append("UPDATE ").append(TABLE_NAME).append(" SET ").append(COL_FEEDS).append(" = ? ").append(" WHERE ").append(COL_URL).append(" = ?").append(" AND ").append(COL_LANG).append(" = ?").toString();
    String STMT_SELECT_POS_BY_LANG = new StringBuilder().append("SELECT ").append(COL_FEEDS).append(" FROM ").append(TABLE_NAME).append(" WHERE ").append(COL_URL).append(" = ?").append(" AND ").append(COL_LANG).append(" = ?").toString();
}
