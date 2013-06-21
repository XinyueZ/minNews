package com.gmail.hasszhao.mininews.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


public final class TaskHelper {

	private static final int MAX_IMAGE_CACHE_ENTIRES = 100;
	private static RequestQueue sRequestQueue;
	private static ImageLoader sImageLoader;
	private static final Gson sGson = new Gson();


	private TaskHelper() {
		// no instances
	}


	public static void init(Context context) {
		sRequestQueue = Volley.newRequestQueue(context);
		sImageLoader = new ImageLoader(sRequestQueue, new BitmapLruCache(MAX_IMAGE_CACHE_ENTIRES));
	}


	public static RequestQueue getRequestQueue() {
		if (sRequestQueue != null) {
			return sRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}


	/**
	 * Returns instance of ImageLoader initialized with {@see FakeImageCache}
	 * which effectively means that no memory caching is used. This is useful
	 * for images that you know that will be show only once.
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		if (sImageLoader != null) {
			return sImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}


	public static class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

		public BitmapLruCache(int maxSize) {
			super(maxSize);
		}


		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}


		@Override
		public Bitmap getBitmap(String url) {
			return get(url);
		}


		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			put(url, bitmap);
		}
	}


	public static Gson getGson() {
		return sGson;
	}
}
