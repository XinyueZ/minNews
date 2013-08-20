package com.gmail.hasszhao.mininews.dataset.list;

import com.gmail.hasszhao.mininews.dataset.DONewsType;


public final class ListNewsType {

	private DONewsType[] Items;


	public ListNewsType(DONewsType[] _items) {
		super();
		Items = _items;
	}


	public DONewsType[] getItems() {
		return Items;
	}
}
