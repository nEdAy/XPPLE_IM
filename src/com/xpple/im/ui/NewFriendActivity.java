package com.xpple.im.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import com.xpple.im.R;
import com.xpple.im.adapter.NewFriendAdapter;
import com.xpple.im.view.dialog.NiftyDialogBuilder;

/**
 * 新朋友
 * 
 * @ClassName: NewFriendActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class NewFriendActivity extends ActivityBase implements
		OnItemLongClickListener {

	ListView listview;

	NewFriendAdapter adapter;

	String from = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friend);
		from = getIntent().getStringExtra("from");
		initView();
	}

	private void initView() {
		initTopBarForLeft("新朋友");
		listview = (ListView) findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		adapter = new NewFriendAdapter(this, BmobDB.create(this)
				.queryBmobInviteList());
		listview.setAdapter(adapter);
		if (from == null) {// 若来自通知栏的点击，则定位到最后一条
			listview.setSelection(adapter.getCount());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
		showDeleteDialog(position, invite);
		return true;
	}

	public void showDeleteDialog(final int position, final BmobInvitation invite) {
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(this);
		dialogBuilder.withTitle(invite.getFromname()).withMessage("删除好友请求")
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
						deleteInvite(position, invite);
						dialogBuilder.getDismiss();
					}
				}).show();
	}

	/**
	 * 删除请求 deleteRecent
	 * 
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteInvite(int position, BmobInvitation invite) {
		adapter.remove(position);
		BmobDB.create(this).deleteInviteMsg(invite.getFromid(),
				Long.toString(invite.getTime()));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (from == null) {
			startAnimActivity(MainActivity.class);
		}
	}

}
