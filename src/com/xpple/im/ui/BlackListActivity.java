package com.xpple.im.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.xpple.im.CustomApplcation;
import com.xpple.im.R;
import com.xpple.im.adapter.BlackListAdapter;
import com.xpple.im.util.CollectionUtils;
import com.xpple.im.view.HeaderLayout;
import com.xpple.im.view.dialog.NiftyDialogBuilder;

/**
 * 黑名单列表
 * 
 * @ClassName: BlackListActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class BlackListActivity extends ActivityBase implements
		OnItemClickListener {

	ListView listview;
	BlackListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacklist);
		initView();
	}

	private void initView() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.layout_register);
		initTopBarForLeft("黑名单");
		adapter = new BlackListAdapter(this, BmobDB.create(this).getBlackList());
		listview = (ListView) findViewById(R.id.list_blacklist);
		listview.setOnItemClickListener(this);
		listview.setAdapter(adapter);
	}

	/**
	 * 显示移除黑名单对话框
	 * 
	 * @Title: showRemoveBlackDialog
	 * @Description: TODO
	 * @param @param position
	 * @param @param invite
	 * @return void
	 * @throws
	 */
	public void showRemoveBlackDialog(final int position,
			final BmobChatUser user) {
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(this);
		dialogBuilder.withTitle("移出黑名单")
				.withMessage("你确定将" + user.getNick() + "移出黑名单吗?")
				.isCancelableOnTouchOutside(true).isCancelable(true)
				.withDuration(700).withButton1Text("取消").withButton2Text("确定")
				.setCustomView(0, this)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.getDismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						adapter.remove(position);
						userManager.removeBlack(user.getUsername(),
								new UpdateListener() {

									@Override
									public void onSuccess() {
										// TODO Auto-generated method stub
										ShowToast("移出黑名单成功");
										// 重新设置下内存中保存的好友列表
										CustomApplcation
												.getInstance()
												.setContactList(
														CollectionUtils
																.list2map(BmobDB
																		.create(getApplicationContext())
																		.getContactList()));
									}

									@Override
									public void onFailure(int arg0, String arg1) {
										// TODO Auto-generated method stub
										ShowToast("移出黑名单失败:" + arg1);
									}
								});
						dialogBuilder.getDismiss();
					}
				}).show();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		BmobChatUser invite = (BmobChatUser) adapter.getItem(arg2);
		showRemoveBlackDialog(arg2, invite);
	}

}
