package com.xpple.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.xpple.im.R;
import com.xpple.im.adapter.MessageRecentAdapter;
import com.xpple.im.ui.ChatActivity;
import com.xpple.im.ui.FragmentBase;
import com.xpple.im.view.dialog.NiftyDialogBuilder;

/**
 * 最近会话
 * 
 * @ClassName: ConversationFragment
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class RecentFragment extends FragmentBase implements
		OnItemClickListener, OnItemLongClickListener {

	ListView listview;

	MessageRecentAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		initTopBarForOnlyTitle("会话");
		listview = (ListView) findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(getActivity(),
				R.layout.item_conversation, BmobDB.create(getActivity())
						.queryRecents());
		listview.setAdapter(adapter);
	}

	/**
	 * 删除会话 deleteRecent
	 * 
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteRecent(BmobRecent recent) {
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}

	public void showDeleteDialog(final BmobRecent recent) {
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(getActivity());
		dialogBuilder.withTitle(recent.getNick()).withMessage("删除会话?")
				.isCancelableOnTouchOutside(true).isCancelable(true)
				.withDuration(700).withButton1Text("取消").withButton2Text("确定")
				.setCustomView(0, getActivity())
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogBuilder.getDismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteRecent(recent);
						dialogBuilder.getDismiss();
					}
				}).show();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		// 重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		// 组装聊天对象
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(),
							R.layout.item_conversation, BmobDB.create(
									getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

}
