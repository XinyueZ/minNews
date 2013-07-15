package com.gmail.hasszhao.mininews.dataset.list;

import java.util.ArrayList;
import java.util.List;

import com.gmail.hasszhao.mininews.dataset.DONews;


public final class ListNews {

	private List<DONews> PulledNewss;
	private final int Count;


	public ListNews(List<DONews> _pulledNewss, int _count) {
		super();
		PulledNewss = _pulledNewss;
		Count = _count;
	}


	public List<DONews> getPulledNewss() {
		if (PulledNewss == null) {
			return PulledNewss = new ArrayList<DONews>();
		}
		return PulledNewss;
	}


	/** Return the max count of news on the server ready. */
	public int getCount() {
		return Count;
	}
}
