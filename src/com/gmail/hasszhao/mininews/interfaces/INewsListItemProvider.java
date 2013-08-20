package com.gmail.hasszhao.mininews.interfaces;

import com.gmail.hasszhao.mininews.fragments.dialog.AskOpenDetailsMethodFragment.OpenContentMethod;

import java.util.List;


public interface INewsListItemProvider {

	INewsListItem getNewsListItem();


	void openDetails(OpenContentMethod _method);


	List<? extends INewsListItem> getList();
}
