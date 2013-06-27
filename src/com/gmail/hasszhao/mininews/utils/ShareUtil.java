package com.gmail.hasszhao.mininews.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.gmail.hasszhao.mininews.R;
import com.gmail.hasszhao.mininews.interfaces.ISharable;


public final class ShareUtil {

	public void openShare(Fragment _f, ISharable _sharable) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, _sharable.getSubject());
		intent.putExtra(Intent.EXTRA_TEXT, _sharable.getText());
		_f.startActivity(Intent.createChooser(intent, _f.getString(R.string.action_share)));
	}


	public void openShare(Context _context, ISharable _sharable) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, _sharable.getSubject());
		intent.putExtra(Intent.EXTRA_TEXT, _sharable.getText());
		_context.startActivity(Intent.createChooser(intent, _context.getString(R.string.action_share)));
	}
}
