package com.gmail.hasszhao.mininews.utils;

public final class BooleanUtil {

	public static boolean fromLong(long _i) {
		return _i == 0 ? false : true;
	}


	public static long toLong(boolean _bool) {
		return _bool ? 1 : 0;
	}
}
