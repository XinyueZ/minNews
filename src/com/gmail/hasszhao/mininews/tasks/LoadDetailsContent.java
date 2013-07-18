package com.gmail.hasszhao.mininews.tasks;

import java.lang.ref.WeakReference;

import org.jsoup.Jsoup;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;


public class LoadDetailsContent extends AsyncTask<String, Article, Article> {

	private final WeakReference<TextView> mOutput;
	private final WeakReference<Button> mVisitWeb;
	private final INewsListItem mNewsItem;


	public LoadDetailsContent(TextView _output, Button _visitWeb, INewsListItem _item) {
		super();
		mOutput = new WeakReference<TextView>(_output);
		mVisitWeb = new WeakReference<Button>(_visitWeb);
		_output.setVisibility(View.GONE);
		_visitWeb.setVisibility(View.GONE);
		mNewsItem = _item;
	}


	@Override
	protected Article doInBackground(String... _params) {
		String url = mNewsItem.getURL();
		Configuration config = new Configuration();
		// https://github.com/GravityLabs/goose/issues/60
		config.setLocalStoragePath("/data/data/com.gmail.hasszhao.mininews/cache");
		Goose goose = new Goose(config);
		Article article = goose.extractContent(url);
		return article;
	}


	@Override
	protected void onPostExecute(Article _result) {
		if (_result != null) {
			TextView tv = mOutput.get();
			if (tv != null) {
				String cleaned = _result.cleanedArticleText();
				if (!TextUtils.isEmpty(cleaned)) {
					tv.setText(cleaned);
					tv.setVisibility(View.VISIBLE);
					Log.i("mini", "Show normal");
				} else {
					showFallback();
				}
			}
		} else {
			showFallback();
		}
		Button btn = mVisitWeb.get();
		if (btn != null) {
			btn.setVisibility(View.VISIBLE);
		}
	}


	private void showFallback() {
		TextView tv = mOutput.get();
		if (tv != null) {
			org.jsoup.nodes.Document doc = Jsoup.parse(mNewsItem.getFullContent().replace("<br>", "\n\n")
					.replace("<p>", "\n\n"));
			tv.setText(doc.text());
			// Elements media = doc.select("[src]");
			// for (Element src : media) {
			// if (src.tagName().equals("img")) {
			// Log.d("mini",
			// "Ask: "
			// + String.format(" * %s: <%s> %sx%s (%s)", src.tagName(),
			// src.attr("abs:src"), src.attr("width"),
			// src.attr("height"),
			// Util.trim(src.attr("alt"), 20)));
			// }
			// }
			tv.setVisibility(View.VISIBLE);
			Log.i("mini", "Show fallback");
		}
	}
}
