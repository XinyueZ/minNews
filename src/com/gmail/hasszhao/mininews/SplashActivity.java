package com.gmail.hasszhao.mininews;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.tasks.LoadNewsListTask;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class SplashActivity extends Activity implements ErrorListener {

	public static final String ACTION = "com.gmail.hasszhao.mininews.SplashActivity";
	private int mCallCount = 0;


	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		View view = View.inflate(getApplication(), R.layout.activity_splash, null);
		setContentView(view);
		startApp(view);
	}


	@Override
	protected void onDestroy() {
		TaskHelper.getRequestQueue().cancelAll(LoadNewsListTask.TAG);
		super.onDestroy();
	}


	public void startApp(View view) {
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
			}


			@Override
			public void onAnimationRepeat(Animation animation) {
			}


			@Override
			public void onAnimationStart(Animation animation) {
				payloading();
			}
		});
	}


	private void payloading() {
		if (Prefs.getInstance().isSupportEnglish()) {
			mCallCount++;
		}
		if (Prefs.getInstance().isSupportChinese()) {
			mCallCount++;
		}
		if (Prefs.getInstance().isSupportGerman()) {
			mCallCount++;
		}
		if (Prefs.getInstance().isSupportEnglish()) {
			new LoadNewsListTask(this.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class,
					new ResponseListener(this, "en"), this, new DOCookie(Prefs.getInstance().getNewsSize(), "en", ""))
					.execute();
		}
		if (Prefs.getInstance().isSupportChinese()) {
			new LoadNewsListTask(this.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class,
					new ResponseListener(this, "zh"), this, new DOCookie(Prefs.getInstance().getNewsSize(), "zh", ""))
					.execute();
		}
		if (Prefs.getInstance().isSupportGerman()) {
			new LoadNewsListTask(this.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class,
					new ResponseListener(this, "de"), this, new DOCookie(Prefs.getInstance().getNewsSize(), "de", ""))
					.execute();
		}
	}


	private void startMainActivity() {
		Intent i = new Intent(MainActivity.ACTION);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
		finish();
	}


	private synchronized int getCallCount() {
		return mCallCount;
	}


	private synchronized void decrementCallCount() {
		mCallCount--;
	}


	@Override
	public synchronized void onErrorResponse(VolleyError _error) {
		Log.e("news", "Ask: API_ErrorResponse");
		mCallCount--;
		if (mCallCount == 0) {
			startMainActivity();
		}
	}


	static class ResponseListener implements Listener<DOStatus> {

		private final String mLangauge;
		private final WeakReference<SplashActivity> mActivity;


		ResponseListener(SplashActivity _splashActivity, String _langauge) {
			super();
			mLangauge = _langauge;
			mActivity = new WeakReference<SplashActivity>(_splashActivity);
		}


		@Override
		public synchronized void onResponse(DOStatus _response) {
			SplashActivity act = mActivity.get();
			if (act != null) {
				act.decrementCallCount();
				if (_response != null) {
					try {
						switch (_response.getCode()) {
							case API.API_OK:
								Log.i("news", "Ask: API_OK");
								((App) act.getApplication()).addListNews(
										mLangauge,
										TaskHelper
												.getGson()
												.fromJson(
														new String(Base64.decode(_response.getData(), Base64.DEFAULT)),
														ListNews.class).getPulledNewss());
								break;
							case API.API_ACTION_FAILED:
								Log.e("news", "Ask: API_ACTION_FAILED");
								break;
							case API.API_SERVER_DOWN:
								Log.e("news", "Ask: API_SERVER_DOWN");
								break;
						}
						if (act.getCallCount() == 0) {
							act.startMainActivity();
						}
					} catch (Exception _e) {
						_e.printStackTrace();
					}
				}
			}
		}
	}
}
