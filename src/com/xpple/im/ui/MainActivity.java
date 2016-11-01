package com.xpple.im.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.update.BmobUpdateAgent;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.im.CustomApplcation;
import com.xpple.im.MyMessageReceiver;
import com.xpple.im.R;
import com.xpple.im.ui.fragment.ContactFragment;
import com.xpple.im.ui.fragment.RecentFragment;
import com.xpple.im.ui.fragment.SettingsFragment;
import com.xpple.im.util.ImageLoadOptions;
import com.xpple.im.view.ActionSheet;
import com.xpple.im.view.ActionSheet.ActionSheetListener;
import com.xpple.im.view.DragLayout;
import com.xpple.im.view.DragLayout.DragListener;

/**
 * 登陆
 * 
 * @ClassName: MainActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class MainActivity extends ActivityBase implements EventListener,
		ActionSheetListener, OnClickListener {

	private Button[] mTabs;
	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private SettingsFragment settingFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	ImageView iv_recent_tips, iv_contact_tips;// 消息提示
	// 侧边栏
	TextView checkForUpdates, logout, about;
	private DragLayout dl;
	public TextView nick, nearPeople, addFriend, robot;;
	private ImageView iv_set_avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		// 如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		BmobChat.getInstance(this).startPollService(30);
		// 开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		initView();
		initTab();
		initDragLayout();
	}

	private void initDragLayout() {
		// 侧边栏
		addFriend = (TextView) findViewById(R.id.add_friend);
		nearPeople = (TextView) findViewById(R.id.near_people);
		checkForUpdates = (TextView) findViewById(R.id.check_for_updates);
		logout = (TextView) findViewById(R.id.logout);
		about = (TextView) findViewById(R.id.about);
		iv_set_avatar = (ImageView) findViewById(R.id.iv_set_avatar);
		robot = (TextView) findViewById(R.id.robot);

		robot.setOnClickListener(this);
		nearPeople.setOnClickListener(this);
		addFriend.setOnClickListener(this);
		checkForUpdates.setOnClickListener(this);
		logout.setOnClickListener(this);
		iv_set_avatar.setOnClickListener(this);
		about.setOnClickListener(this);

		loadInformation();
		dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
			}

			@Override
			public void onClose() {
			}

			@Override
			public void onDrag(float percent) {
			}
		});
	}

	private void loadInformation() {
		nick = (TextView) findViewById(R.id.nick);
		nick.setOnClickListener(this);
		BmobChatUser user = BmobUserManager.getInstance(this).getCurrentUser();
		nick.setText(user.getNick());
		iv_set_avatar = (ImageView) findViewById(R.id.iv_set_avatar);
		String Avatar = user.getAvatar();
		if (Avatar != null && !Avatar.equals("")) {
			ImageLoader.getInstance().displayImage(Avatar, iv_set_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_set_avatar.setImageResource(R.drawable.default_head);
		}
	}

	private void initView() {
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_message);
		mTabs[1] = (Button) findViewById(R.id.btn_contract);
		mTabs[2] = (Button) findViewById(R.id.btn_set);
		iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}

	private void initTab() {
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		settingFragment = new SettingsFragment();
		fragments = new Fragment[] { recentFragment, contactFragment,
				settingFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, recentFragment)
				.add(R.id.fragment_container, contactFragment)
				.hide(contactFragment).show(recentFragment).commit();
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_message:
			index = 0;
			break;
		case R.id.btn_contract:
			index = 1;
			break;
		case R.id.btn_set:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 小圆点提示
		if (BmobDB.create(this).hasUnReadMsg()) {
			iv_recent_tips.setVisibility(View.VISIBLE);
		} else {
			iv_recent_tips.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_contact_tips.setVisibility(View.VISIBLE);
		} else {
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}

	/**
	 * 刷新界面
	 * 
	 * @Title: refreshNewMsg
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshNewMsg(BmobMsg message) {
		// 声音提示
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		// 也要存储起来
		if (message != null) {
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(
					true, message);
		}
		if (currentTabIndex == 0) {
			// 当前页面如果为会话页面，刷新此页面
			if (recentFragment != null) {
				recentFragment.refresh();
			}
		}
	}

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	TagBroadcastReceiver userReceiver;

	private void initTagMessageBroadCast() {
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}

	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}

	/**
	 * 刷新好友请求
	 * 
	 * @Title: notifyAddUser
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = CustomApplcation.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if (currentTabIndex == 1) {
			if (contactFragment != null) {
				contactFragment.refresh();
			}
		} else {
			// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil()
					.isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}

	Boolean ActionSheetFlag = false;
	private static long firstTime;

	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (ActionSheetFlag) {
			super.onBackPressed();
		} else {
			if (firstTime + 2000 > System.currentTimeMillis()) {
				super.onBackPressed();
			} else {
				ShowToast("再按一次退出程序");
			}
			firstTime = System.currentTimeMillis();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 在这里做你想做的事情
			if (!ActionSheetFlag) {
				setTheme(R.style.ActionSheetStyleIOS7);
				showActionSheet(); // 调用这个，就可以弹出菜单
			}
		}
		return super.onKeyDown(keyCode, event);

	}

	public void showActionSheet() {
		ActionSheetFlag = true;
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消")
				.setOtherButtonTitles("版本更新", "帮助与反馈", "退出应用")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@SuppressLint("ShowToast")
	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {
		if (index == 0) {
			onUpdate();
		} else if (index == 1) {
			startAnimActivity(new Intent(MainActivity.this,
					SendFeedbackActivity.class));
		} else if (index == 2) {
			this.finish();
		}
		ActionSheetFlag = false;
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancle) {
		ActionSheetFlag = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		// 取消定时检测服务
		BmobChat.getInstance(this).stopPollService();
	}

	private void onUpdate() {
		BmobUpdateAgent.forceUpdate(this);
		ShowToast("已是最新版本");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_set_avatar:
			loadInformation();
			Intent intent = new Intent(this, SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startActivity(intent);
			break;
		case R.id.nick:
			loadInformation();
			Intent intent2 = new Intent(this, SetMyInfoActivity.class);
			intent2.putExtra("from", "me");
			startActivity(intent2);
			break;
		case R.id.near_people:
			startAnimActivity(new Intent(MainActivity.this, RadarActivity.class));
			break;
		case R.id.add_friend:
			startAnimActivity(new Intent(MainActivity.this,
					AddFriendActivity.class));
			break;
		case R.id.robot:
			startAnimActivity(new Intent(MainActivity.this, RobotActivity.class));
			break;
		case R.id.check_for_updates:
			onUpdate();
			break;
		case R.id.about:
			startAnimActivity(new Intent(MainActivity.this, AboutActivity.class));
			break;
		case R.id.logout:
			CustomApplcation.getInstance().logout();
			finish();
			startAnimActivity(new Intent(MainActivity.this, LoginActivity.class));
			break;
		}
	}

}
