package com.gmail.hasszhao.mininews.dataset.list;

import java.util.ArrayList;
import java.util.List;

import com.gmail.hasszhao.mininews.dataset.DONews;


public final class ListNews {

	private List<DONews> PulledNewss;
	private int Count;


	public ListNews(List<DONews> _pulledNewss, int _count) {
		super();
		PulledNewss = _pulledNewss;
		Count = _count;
	}


	private ListNews() {
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


	@Override
	public Object clone() throws CloneNotSupportedException {
		ListNews obj = new ListNews();
		obj.getPulledNewss().addAll(this.PulledNewss);
		obj.Count = this.Count;
		return obj;
	}
}
