package com.gmail.hasszhao.mininews.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;


public final class Util {

	private static final String TAG = Util.class.getName();
	public static final String MIME_TYPE_TEXT_HTML = "text/html";
	public static final String UTF_8 = "UTF-8";
	private static final int STREAM_CACHE = 1024 * 1024;
	private static final String T_URL = "http://tinyurl.com/api-create.php?url=";


	public static String getTinyUrl(String _longUrl) {
		String tinyUrl = "";
		String urlString = T_URL + _longUrl;
		try {
			URL url = new URL(urlString);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				tinyUrl += str;
			}
			in.close();
		} catch (Exception _e) {
		}
		return tinyUrl;
	}


	public static String streamToString(InputStream _in) {
		BufferedReader reader = null;
		String line = null;
		StringBuilder content = null;
		try {
			reader = new BufferedReader(new InputStreamReader(_in), STREAM_CACHE);
			content = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
		} catch (Exception _e) {
			Log.e(TAG, "Paser inputstream to string error: " + _e.toString());
		} finally {
			try {
				_in.close();
			} catch (IOException _e) {
				_in = null;
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException _e) {
					reader = null;
				}
			}
			line = null;
		}
		if (content != null) {
			return content.toString();
		}
		return null;
	}


	public static String readAssetsFile(Context _cxt, String _path) {
		InputStream input = null;
		String text = null;
		byte[] buffer = null;
		try {
			input = _cxt.getAssets().open(_path);
			int size = input.available();
			buffer = new byte[size];
			input.read(buffer);
			text = new String(buffer);
		} catch (Exception _e) {
			Log.e(TAG, "Error while reading file: " + _e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException _e) {
					Log.e(TAG, "Error while closing: " + _e.getMessage());
				} finally {
					input = null;
				}
			}
			buffer = null;
		}
		return text;
	}


	private static final String GACCOUNT = "com.google";


	public static String getGmailNameBeforeAt(Context _context) {
		AccountManager am = null;
		Account[] accounts = null;
		String email = null;
		String name = null;
		try {
			am = AccountManager.get(_context.getApplicationContext());
			accounts = am.getAccountsByType(GACCOUNT);
			if (accounts != null && accounts.length > 0) {
				email = accounts[0].name;
			}
			String[] strs = email.split("@");
			if (strs.length > 0) {
				name = strs[0];
			}
		} catch (Exception _e) {
			Log.e(TAG, "Error in getGmailNameBeforeAt: " + _e.getMessage());
		} finally {
			am = null;
			accounts = null;
			email = null;
		}
		return name;
	}


	/*
	 * http://stackoverflow.com/a/4952066/1835650
	 */
	public static String[] generateRandomWords(int _numberOfWords) {
		String[] randomStrings = null;
		Random random = null;
		try {
			random = new Random();
			randomStrings = new String[_numberOfWords];
			for (int i = 0; i < _numberOfWords; i++) {
				char[] word = new char[random.nextInt(8) + 3]; // words of
																// length 3
																// through 10.
																// (1 and 2
																// letter words
																// are boring.)
				for (int j = 0; j < word.length; j++) {
					word[j] = (char) ('a' + random.nextInt(26));
				}
				randomStrings[i] = new String(word);
			}
		} catch (Exception _e) {
			randomStrings = null;
			Log.e(TAG, "Error in generateRandomWords: " + _e.getMessage());
		} finally {
			random = null;
		}
		return randomStrings;
	}


	public static void openSMS(Context _context, String _telNum, String _text) {
		if (_context != null) {
			String phoneNumber = _telNum;
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(new StringBuilder().append("smsto:")
					.append(phoneNumber).toString()));
			intent.putExtra("sms_body", _text);
			intent.putExtra("compose_mode", true);
			_context.startActivity(intent);
		}
	}


	public static void openEmail(Context _context, String _to, String _title, String _body) {
		if (_context != null) {
			final Intent i = new Intent(Intent.ACTION_VIEW);
			i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { _to });
			i.setType("text/plain");
			final StringBuilder uri = new StringBuilder("mailto:");
			uri.append("?subject=").append(_title);
			uri.append("&body=").append(_body);
			i.setData(Uri.parse(uri.toString()));
			_context.startActivity(i);
		}
	}


	public static void openEmail(Context _context, String _to) {
		openEmail(_context, _to, null, null);
	}


	public static void openTel(Context _context, String _to) {
		if (_context != null) {
			String number = "tel:" + Uri.encode(_to);
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(number));
			_context.startActivity(intent);
		}
	}


	public static void openUrl(Context _context, String _to) {
		if (_context != null) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(_to));
			_context.startActivity(i);
		}
	}


	public static void openPdf(Context _context, String _to) {
		if (_context != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_to));
			_context.startActivity(intent);
		}
	}


	public static void openMapApp(Context _context, String _fromLatLng, String _toLatLng, int _zoomLevel) {
		String q = new StringBuilder().append("http://maps.google.com/maps?").append("saddr=").append(_fromLatLng)
				.append("&daddr=").append(_toLatLng).toString();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(q.trim()));
		_context.startActivity(intent);
	}


	public static void openStandardSharing(Context _context, String _listTitle, String _subject, String _body) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, _subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, _body);
		_context.startActivity(Intent.createChooser(sharingIntent, _listTitle));
	}


	public static void showLongToast(Context _context, int _messageId) {
		Toast.makeText(_context, _context.getString(_messageId), Toast.LENGTH_LONG).show();
	}


	public static void showShortToast(Context _context, int _messageId) {
		Toast.makeText(_context, _context.getString(_messageId), Toast.LENGTH_SHORT).show();
	}


	public static void showLongToast(Context _context, String _message) {
		Toast.makeText(_context, _message, Toast.LENGTH_LONG).show();
	}


	public static void showShortToast(Context _context, String _message) {
		Toast.makeText(_context, _message, Toast.LENGTH_SHORT).show();
	}


	@SuppressWarnings("deprecation")
	public static boolean isAirplaneModeOn(Context _context) {
		return Settings.System.getInt(_context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}


	public static boolean isOnline(Context _context) {
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	public static void alwaysExpandingExpandableListView(ExpandableListView _list) {
		BaseExpandableListAdapter adp = null;
		try {
			adp = (BaseExpandableListAdapter) _list.getExpandableListAdapter();
			for (int i = 0, cnt = adp.getGroupCount(); i < cnt; i++) {
				_list.expandGroup(i);
			}
			_list.setOnGroupClickListener(new OnGroupClickListener() {

				@Override
				public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
					return true;
				}
			});
		} catch (Exception _e) {
			Log.e(TAG, "Error in configList: " + _e.getMessage());
		} finally {
			adp = null;
		}
	}


	public static void enableViews(Context _app, ViewGroup _root, boolean _enabled, int _colorResIdEnable,
			int _colorResIdDisable) {
		for (int i = 0, cnt = _root.getChildCount(); i < cnt; i++) {
			View child = _root.getChildAt(i);
			child.setEnabled(_enabled);
			child.setClickable(_enabled);
			// if (child instanceof CheckBox) {
			// CheckBox cb = (CheckBox) child;
			// if (cb.isChecked()) {
			// cb.setCompoundDrawablesWithIntrinsicBounds(0, 0,
			// !_enabled ? R.drawable.btn_check_on_disabled_focused_holo_dark
			// : R.drawable.selector_checkbox, 0);
			// } else {
			// cb.setCompoundDrawablesWithIntrinsicBounds(0, 0,
			// !_enabled ? R.drawable.btn_check_off_disabled_focused_holo_dark
			// : R.drawable.selector_checkbox, 0);
			// }
			// }
			if (child instanceof TextView) {
				((TextView) child).setTextColor(_app.getResources().getColor(
						_enabled ? _colorResIdEnable : _colorResIdDisable));
			}
			if (child instanceof ViewGroup) {
				enableViews(_app, (ViewGroup) child, _enabled, _colorResIdEnable, _colorResIdDisable);
			}
		}
		_root.setEnabled(_enabled);
		_root.setClickable(_enabled);
	}


	public static void removeViews(ViewGroup _root) {
		for (int i = 0, cnt = _root.getChildCount(); i < cnt; i++) {
			View child = _root.getChildAt(i);
			if (child instanceof ViewGroup) {
				removeViews((ViewGroup) child);
			}
			_root.removeView(child);
		}
	}


	public static void hideKeyboard(Activity _ctx, View _v) {
		if (_ctx != null) {
			// InputMethodManager imm = (InputMethodManager)
			// _ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
			// imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			if (_v != null) {
				InputMethodManager imm = (InputMethodManager) _ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(_v.getApplicationWindowToken(), 0);
			} else {
				_ctx.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		}
	}


	public static String uppercaseFirst(String _text) {
		if (_text == null) {
			return null;
		}
		return new StringBuilder().append(_text.substring(0, 1).toUpperCase()).append(_text.substring(1)).toString();
	}


	public static Spannable linkifyHtml(String html, int linkifyMask) {
		Spanned text = Html.fromHtml(html);
		URLSpan[] currentSpans = text.getSpans(0, text.length(), URLSpan.class);
		SpannableString buffer = new SpannableString(text);
		Linkify.addLinks(buffer, linkifyMask);
		for (URLSpan span : currentSpans) {
			int end = text.getSpanEnd(span);
			int start = text.getSpanStart(span);
			buffer.setSpan(span, start, end, 0);
		}
		return buffer;
	}


	public static String encode(String _keywords) {
		try {
			// For space we should think about this :)
			// http://stackoverflow.com/questions/4737841/urlencoder-not-able-to-translate-space-character
			return URLEncoder.encode(_keywords, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException _e1) {
			return new String(_keywords.trim().replace(" ", "%20").replace("&", "%26").replace(",", "%2c")
					.replace("(", "%28").replace(")", "%29").replace("!", "%21").replace("=", "%3D")
					.replace("<", "%3C").replace(">", "%3E").replace("#", "%23").replace("$", "%24")
					.replace("'", "%27").replace("*", "%2A").replace("-", "%2D").replace(".", "%2E")
					.replace("/", "%2F").replace(":", "%3A").replace(";", "%3B").replace("?", "%3F")
					.replace("@", "%40").replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
					.replace("_", "%5F").replace("`", "%60").replace("{", "%7B").replace("|", "%7C")
					.replace("}", "%7D"));
		}
	}


	public static String replaceToEncode(String _keywords) {
		return new String(_keywords.trim().replace(" ", "%20").replace("&", "%26").replace(",", "%2c")
				.replace("(", "%28").replace(")", "%29").replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
				.replace(">", "%3E").replace("#", "%23").replace("$", "%24").replace("'", "%27").replace("*", "%2A")
				.replace("-", "%2D").replace(".", "%2E").replace("/", "%2F").replace(":", "%3A").replace(";", "%3B")
				.replace("?", "%3F").replace("@", "%40").replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
				.replace("_", "%5F").replace("`", "%60").replace("{", "%7B").replace("|", "%7C").replace("}", "%7D"));
	}
}