package com.gmail.hasszhao.mininews.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.gmail.hasszhao.mininews.db.AppDB;
import com.gmail.hasszhao.mininews.interfaces.INewsListItem;
import com.gmail.hasszhao.mininews.utils.Util;
import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;

import org.jsoup.Jsoup;

public class TaskReadDetails extends AsyncTask<INewsListItem, String, String> {
    private AppDB mDB;

    public TaskReadDetails(AppDB _appDB) {
        mDB = _appDB;
    }

    @Override
    protected String doInBackground(INewsListItem... params) {
        INewsListItem item = params[0];
        if (!mDB.findNewsDetails(item.getURL())) {
            try {
                String url = item.getURL();
                Configuration config = new Configuration();
                // https://github.com/GravityLabs/goose/issues/60
                config.setLocalStoragePath("/data/data/com.gmail.hasszhao.mininews/cache");
                config.setEnableImageFetching(false);
                Goose goose = new Goose(config);
                Article article = goose.extractContent(url);
                return article.cleanedArticleText();
            } catch (Exception _e0) {
                try {
                    _e0.printStackTrace();
                    org.jsoup.nodes.Document doc = Jsoup.parse(item.getFullContent().replace("<br>", "\n\n")
                            .replace("<p>", "\n\n"));
                    return doc.text();
                } catch (Exception _e1) {
                    _e1.printStackTrace();
                    return null;
                }
            }
        } else {
            return mDB.getLastNewsDetails(item.getURL());
        }
    }
}
