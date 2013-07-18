package com.gmail.hasszhao.mininews.interfaces;

public interface INewsListItem {

	String getThumbUrl();


	String getTopline();


	String getHeadline();


	String getFullContent();


	String getDate();


	String getURL();


	boolean isHot();


	boolean isNew();


	long getTime();
}
