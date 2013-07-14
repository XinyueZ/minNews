package com.gmail.hasszhao.mininews;

public interface API {

	public static final long TIME_OUT = 20 * 1000;
	public static final String HOST = "http://breaking-news-2013.appspot.com";
	public static final String GLAT = HOST + "/glat"; // Get latest news
	public static final String GLANG = HOST + "/glan"; // Get language list
	public static final String GRUBRICS = HOST + "/grubrics"; // Get news type
																// list(rubric)
	public static final int API_OK = 900;
	public static final int API_ACTION_FAILED = 901;
	public static final int API_SERVER_DOWN = 902;
}
