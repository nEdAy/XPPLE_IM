package com.xpple.im.ui;

import android.os.Bundle;

import com.xpple.im.R;

/**
 * 关于软件
 * 
 * @author nEdAy
 */
public class AboutActivity extends ActivityBase {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initTopBarForLeft("关于");
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
