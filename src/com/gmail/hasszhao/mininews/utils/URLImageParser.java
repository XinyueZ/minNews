package com.gmail.hasszhao.mininews.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.View;


//http://stackoverflow.com/questions/7424512/android-html-imagegetter-as-asynctask
public final class URLImageParser implements ImageGetter {

	private final WeakReference<View> mContainer;


	/***
	 * Construct the URLImageParser which will execute AsyncTask and refresh the
	 * container
	 * 
	 * @param _container
	 * @param _context
	 */
	public URLImageParser(View _container) {
		this.mContainer = new WeakReference<View>(_container);
	}


	@Override
	public Drawable getDrawable(String source) {
		URLDrawable urlDrawable = new URLDrawable();
		// get the actual source
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable, mContainer);
		asyncTask.execute(source);
		// return reference to URLDrawable where I will change with actual image
		// from
		// the src tag
		return urlDrawable;
	}


	public static class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {

		URLDrawable urlDrawable;
		WeakReference<View> container;


		public ImageGetterAsyncTask(URLDrawable d, WeakReference<View> _container) {
			urlDrawable = d;
			container = _container;
		}


		@Override
		protected Drawable doInBackground(String... params) {
			String source = params[0];
			return fetchDrawable(source);
		}


		@Override
		protected void onPostExecute(Drawable result) {
			if (urlDrawable != null && result != null) {
				// set the correct bound according to the result from HTTP call
				urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
				// change the reference of the current drawable to the result
				// from the HTTP call
				urlDrawable.drawable = result;
				// redraw the image by invalidating the container
				if (container.get() != null) {
					container.get().invalidate();
				}
			}
		}


		/***
		 * Get the Drawable from URL
		 * 
		 * @param urlString
		 * @return
		 */
		public Drawable fetchDrawable(String urlString) {
			try {
				InputStream is = fetch(urlString);
				Drawable drawable = Drawable.createFromStream(is, "src");
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				return drawable;
			} catch (Exception e) {
				return null;
			}
		}


		private InputStream fetch(String urlString) throws MalformedURLException, IOException {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlString);
			HttpResponse response = httpClient.execute(request);
			return response.getEntity().getContent();
		}
	}
}