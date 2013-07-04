package com.gmail.hasszhao.mininews.utils;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


//http://stackoverflow.com/questions/7424512/android-html-imagegetter-as-asynctask
@SuppressWarnings("deprecation")
public final class URLDrawable extends BitmapDrawable {

	// the drawable that you need to set, you could set the initial drawing
	// with the loading image if you need to
	protected Drawable drawable;


	@Override
	public void draw(Canvas canvas) {
		// override the draw to facilitate refresh function later
		if (drawable != null) {
			drawable.draw(canvas);
		}
	}
}