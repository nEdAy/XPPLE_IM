package com.xpple.im.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xpple.im.R;
import com.xpple.im.util.SharePreferenceUtil;

public class SetActivity extends ActivityBase implements OnClickListener {

	RelativeLayout rl_switch_notification, rl_switch_voice, rl_switch_vibrate;

	ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;
	SharePreferenceUtil mSharedUtil;
	View view1, view2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		mSharedUtil = mApplication.getSpUtil();
		initView();
	}

	private void initView() {
		initTopBarForLeft("设置");
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		iv_open_notification = (ImageView) findViewById(R.id.iv_open_notification);
		iv_close_notification = (ImageView) findViewById(R.id.iv_close_notification);
		iv_open_voice = (ImageView) findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) findViewById(R.id.iv_close_vibrate);
		view1 = findViewById(R.id.view1);
		view2 = findViewById(R.id.view2);
		// 初始化
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

		if (isAllowNotify) {
			iv_open_notification.setVisibility(View.VISIBLE);
			iv_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_open_notification.setVisibility(View.INVISIBLE);
			iv_close_notification.setVisibility(View.VISIBLE);
		}
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice) {
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.INVISIBLE);
		} else {
			iv_open_voice.setVisibility(View.INVISIBLE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate) {
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_open_vibrate.setVisibility(View.INVISIBLE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_switch_notification:
			if (iv_open_notification.getVisibility() == View.VISIBLE) {
				iv_open_notification.setVisibility(View.INVISIBLE);
				iv_close_notification.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(false);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			} else {
				iv_open_notification.setVisibility(View.VISIBLE);
				iv_close_notification.setVisibility(View.INVISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE) {
				iv_open_voice.setVisibility(View.INVISIBLE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVoiceEnable(true);
			}

			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.INVISIBLE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;

		}
	}
}
