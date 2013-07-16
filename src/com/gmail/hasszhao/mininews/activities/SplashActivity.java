package com.gmail.hasszhao.mininews.activities;

import java.lang.ref.WeakReference;

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
import com.gmail.hasszhao.mininews.API;
import com.gmail.hasszhao.mininews.App;
import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.dataset.DOCookie;
import com.gmail.hasszhao.mininews.dataset.DOStatus;
import com.gmail.hasszhao.mininews.dataset.list.ListNews;
import com.gmail.hasszhao.mininews.fragments.ErrorFragment;
import com.gmail.hasszhao.mininews.fragments.ErrorFragment.ErrorType;
import com.gmail.hasszhao.mininews.fragments.ErrorFragment.IErrorResponsible;
import com.gmail.hasszhao.mininews.tasks.LoadNewsListTask;
import com.gmail.hasszhao.mininews.tasks.TaskHelper;
import com.gmail.hasszhao.mininews.utils.prefs.Prefs;


public final class SplashActivity extends BasicActivity implements ErrorListener, IErrorResponsible {

	public static final String ACTION = "com.gmail.hasszhao.mininews.SplashActivity";
	private int mCallCount = 0;


	private static boolean errorHandling(SplashActivity act) {
		boolean hasSomePayloaded = ((App) act.getApplication()).hasSomePayloaded();
		if (!hasSomePayloaded) {
			act.findViewById(R.id.pb_loading).setVisibility(View.GONE);
			act.replaceOpenFragment(ErrorFragment.newInstance(act, ErrorType.DATA_LOADING_ERROR), ErrorFragment.TAG);
			return true;
		}
		return false;
	}


	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		View view = View.inflate(getApplication(), R.layout.activity_splash, null);
		setContentView(view);
		startApp(view);
	}


	@Override
	public void onBackPressed() {
		TaskHelper.getRequestQueue().cancelAll(LoadNewsListTask.TAG);
		super.onBackPressed();
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
				load();
			}
		});
	}


	private void load() {
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
					new ResponseListener(this, "en"), this, new DOCookie(1, "en", "")).execute();
		}
		if (Prefs.getInstance().isSupportChinese()) {
			new LoadNewsListTask(this.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class,
					new ResponseListener(this, "zh"), this, new DOCookie(1, "zh", "")).execute();
		}
		if (Prefs.getInstance().isSupportGerman()) {
			new LoadNewsListTask(this.getApplicationContext(), Method.GET, API.GLAT, DOStatus.class,
					new ResponseListener(this, "de"), this, new DOCookie(1, "de", "")).execute();
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
		mCallCount--;
		if (mCallCount == 0) {
			if (!errorHandling(this)) {
				startMainActivity();
			}
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
										TaskHelper.getGson().fromJson(
												new String(Base64.decode(_response.getData(), Base64.DEFAULT)),
												ListNews.class));
								break;
							case API.API_ACTION_FAILED:
								Log.e("news", "Ask: API_ACTION_FAILED");
								break;
							case API.API_SERVER_DOWN:
								Log.e("news", "Ask: API_SERVER_DOWN");
								break;
						}
						if (act.getCallCount() == 0) {
							if (!errorHandling(act)) {
								act.startMainActivity();
							}
						}
					} catch (Exception _e) {
						_e.printStackTrace();
					}
				}
			}
		}
	}


	@Override
	public void retry() {
		load();
	}


	@Override
	public boolean isDataCached() {
		return false;
	}
}
