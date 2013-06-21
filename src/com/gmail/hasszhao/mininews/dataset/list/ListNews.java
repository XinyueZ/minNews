package com.gmail.hasszhao.mininews.dataset.list;

import java.util.List;

import com.gmail.hasszhao.mininews.dataset.DONews;


public final class ListNews {

	private final List<DONews> PulledNewss;


	public ListNews(List<DONews> _pulledNewss) {
		super();
		PulledNewss = _pulledNewss;
	}


	public List<DONews> getPulledNewss() {
		return PulledNewss;
	}
}
