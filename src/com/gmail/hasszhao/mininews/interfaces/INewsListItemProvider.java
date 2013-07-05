package com.gmail.hasszhao.mininews.interfaces;

import com.gmail.hasszhao.mininews.fragments.AskOpenDetailsMethodFragment.OpenContentMethod;


public interface INewsListItemProvider {

	INewsListItem getNewsListItem();



	void openDetails(OpenContentMethod _method);
}
