package com.xpple.im.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

import com.xpple.im.R;
import com.xpple.im.bean.User;
import com.xpple.im.config.BmobConstants;
import com.xpple.im.util.CommonUtils;
import com.xpple.im.view.DeletableEditText;

/**
 * @ClassName: LoginActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	DeletableEditText et_username, et_password;
	Button btn_login;
	TextView btn_register;
	ImageView logo;
	Animation shake;
	Boolean shakeFlag;
	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		logo = (ImageView) findViewById(R.id.iv_icon);
		shake = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.button_shake);
		shake.reset();
		shakeFlag = true;
		shake.setFillAfter(true);
		logo.startAnimation(shake);
		logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (shakeFlag) {
					shake.cancel();
					shakeFlag = false;
				} else {
					shake.start();
					shakeFlag = true;
				}
			}
		});
		init();
		// 注册退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
	}

	private void init() {
		et_username = (DeletableEditText) findViewById(R.id.et_username);
		et_password = (DeletableEditText) findViewById(R.id.user_password_input);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (TextView) findViewById(R.id.btn_register);
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& BmobConstants.ACTION_REGISTER_SUCCESS_FINISH
							.equals(intent.getAction())) {
				finish();
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == btn_register) {
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
		} else {
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected) {
				ShowToast(R.string.network_tips);
				return;
			}
			login();
		}
	}

	private void login() {
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();

		if (TextUtils.isEmpty(name)) {
			et_username.setShakeAnimation();
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			et_password.setShakeAnimation();
			ShowToast(R.string.toast_error_password_null);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		User user = new User();
		user.setUsername(name);
		user.setPassword(password);
		userManager.login(user, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.setMessage("正在获取好友列表...");
					}
				});
				// 更新用户的地理位置以及好友的资料
				updateUserInfos();
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				// TODO Auto-generated method stub
				progress.dismiss();
				BmobLog.i(arg0);
				ShowToast(arg0);
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
