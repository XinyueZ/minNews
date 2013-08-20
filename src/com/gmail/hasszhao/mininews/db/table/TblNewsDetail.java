package com.gmail.hasszhao.mininews.db.table;

public interface TblNewsDetail {

	String TABLE_NAME = "news_details";
	String COL_URL = "Url";
	String COL_CONTENT = "Content";
	String COL_LAST_POS = "Last_Pos";
	String SQL_CREATE = new StringBuilder().append("CREATE TABLE ").append(TABLE_NAME).append(" (").append(COL_URL)
			.append(" TEXT PRIMARY KEY, ").append(COL_CONTENT).append(" TEXT, ").append(COL_LAST_POS)
			.append(" INTEGER ").append(");").toString();
	String STMT_INSERT = new StringBuilder().append("INSERT INTO ").append(TABLE_NAME).append(" (").append(COL_URL)
			.append(",").append(COL_CONTENT).append(",").append(COL_LAST_POS).append(")").append(" VALUES (?,?,?)")
			.toString();
	String STMT_UPDATE = new StringBuilder().append("UPDATE ").append(TABLE_NAME).append(" SET ").append(COL_CONTENT)
			.append(" = ?, ").append(COL_LAST_POS).append(" = ? WHERE ").append(COL_URL).append(" = ?").toString();
	String STMT_SELECT_POS_BY_URL = new StringBuilder().append("SELECT ").append(COL_LAST_POS).append(" FROM ")
			.append(TABLE_NAME).append(" WHERE ").append(COL_URL).append("=?").toString();
	String STMT_SELECT_DETAILS_BY_URL = new StringBuilder().append("SELECT ").append(COL_CONTENT).append(" FROM ")
			.append(TABLE_NAME).append(" WHERE ").append(COL_URL).append("=?").toString();
}
