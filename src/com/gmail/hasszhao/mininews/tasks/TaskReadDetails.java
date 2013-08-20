package com.gmail.hasszhao.mininews.tasks;

import org.jsoup.Jsoup;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.gmail.hasszhao.mininews.dataset.DOReadNewsDetails;
import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;


public class TaskReadDetails extends AsyncTask<INewsListItem, DOReadNewsDetails, DOReadNewsDetails> {

	private AppDB mDB;


	public TaskReadDetails(AppDB _appDB) {
		mDB = _appDB;
	}


	@Override
	protected DOReadNewsDetails doInBackground(INewsListItem... _params) {
		INewsListItem item = _params[0];
		DOReadNewsDetails res = new DOReadNewsDetails();
		res.setLastPosition(mDB.getLastNewsDetailsPosition(item.getURL()));
		if (!mDB.findNewsDetails(item.getURL())) {
			try {
				String url = item.getURL();
				Configuration config = new Configuration();
				// https://github.com/GravityLabs/goose/issues/60
				config.setLocalStoragePath("/data/data/com.gmail.hasszhao.mininews/cache");
				config.setEnableImageFetching(false);
				Goose goose = new Goose(config);
				Article article = goose.extractContent(url);
				String cleaned = article.cleanedArticleText();
				if (!TextUtils.isEmpty(cleaned)) {
					res.setContent(cleaned);
				} else {
					throw new Exception();
				}
			} catch (Exception _e0) {
				try {
					_e0.printStackTrace();
					org.jsoup.nodes.Document doc = Jsoup.parse(item.getFullContent().replace("<br>", "\n\n")
							.replace("<p>", "\n\n"));
					res.setContent(doc.text());
				} catch (Exception _e1) {
					_e1.printStackTrace();
					res.setContent(null);
				}
			}
		} else {
			res.setContent(mDB.getLastNewsDetails(item.getURL()));
		}
		return res;
	}
}
