package com.xpple.im.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.bmob.im.BmobChat;
import cn.bmob.v3.update.BmobUpdateAgent;

import com.xpple.im.R;
import com.xpple.im.config.Config;

/**
 * 引导页
 * 
 * @ClassName: SplashActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class SplashActivity extends BaseActivity {

	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试
		BmobChat.DEBUG_MODE = true;
		// BmobIM SDK初始化--只需要这一段代码即可完成初始化
		BmobChat.getInstance(this).init(Config.applicationId);
		// // 创建自动更新表
		// BmobUpdateAgent.initAppVersion(this);
		// 静默下载更新:当用户进入应用首页后如果处于wifi环境检测更新，如果有更新，后台下载新版本，如果下载成功，则进行通知栏展示，用户点击通知栏开始安装。静默下载过程中如果wifi断开，则会停止下载。
		BmobUpdateAgent.silentUpdate(this);
		// 开始定位
		onLocation();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (userManager.getCurrentUser() != null) {
			// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
			updateUserInfos();
			mHandler.sendEmptyMessageDelayed(GO_HOME, 200);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 200);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				startAnimActivity(MainActivity.class);
				finish();
				break;
			case GO_LOGIN:
				startAnimActivity(LoginActivity.class);
				finish();
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// 停止定位
		stopLocation();
		super.onDestroy();
	}

}
