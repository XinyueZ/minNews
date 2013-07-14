package com.gmail.hasszhao.mininews.dataset.list;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.gmail.hasszhao.mininews.dataset.DONews;


public final class ListNews {

	private List<DONews> PulledNewss;
	private final int Count;


	private ListNews(List<DONews> _pulledNewss, int _count) {
		super();
		PulledNewss = _pulledNewss;
		Count = _count;
	}


	public List<DONews> getPulledNewss() {
		Log.d("mini", "Ask: Count: " + Count);
		if (PulledNewss == null) {
			return PulledNewss = new ArrayList<DONews>();
		}
		return PulledNewss;
	}


	public int getCount() {
		return Count;
	}
}
