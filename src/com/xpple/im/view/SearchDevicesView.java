package com.xpple.im.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.xpple.im.R;

public class SearchDevicesView extends BaseView {
	private boolean isSearching = false;
	private float offsetArgs = 0;
	private Bitmap bitmap;
	private Bitmap bitmap2;

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
		offsetArgs = 0;
		invalidate();
	}

	public SearchDevicesView(Context context) {
		super(context);
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBitmap();
	}

	private void initBitmap() {
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.gplus_search_bg));
		}
		if (bitmap2 == null) {
			bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.gplus_search_args));
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		invalidate();

		canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2,
				getHeight() / 3 - bitmap.getHeight() / 2, null);

		if (isSearching) {
			Rect rMoon = new Rect(getWidth() / 2 - bitmap2.getWidth(),
					getHeight() / 3, getWidth() / 2, getHeight() / 3
							+ bitmap2.getHeight());
			canvas.rotate(offsetArgs, getWidth() / 2, getHeight() / 3);
			canvas.drawBitmap(bitmap2, null, rMoon, null);
			offsetArgs = offsetArgs + 3;
		} else {
			canvas.drawBitmap(bitmap2, getWidth() / 2 - bitmap2.getWidth(),
					getHeight() / 3, null);
		}
		if (isSearching)
			invalidate();
	}

}