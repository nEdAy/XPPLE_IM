package com.xpple.im.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xpple.im.R;
import com.xpple.im.adapter.ChatMessageAdapter;
import com.xpple.im.bean.ChatMessage;
import com.xpple.im.bean.ChatMessage.Type;
import com.xpple.im.util.CommonUtils;
import com.xpple.im.util.HttpUtils;
import com.xpple.im.view.EmoticonsEditText;
import com.xpple.im.view.xlist.XListView;

public class RobotActivity extends ActivityBase implements OnClickListener {
	/**
	 * 展示消息的窗体
	 */
	XListView mChatView;
	/**
	 * 文本域
	 */
	private EditText mMsg;
	/**
	 * 存储聊天消息
	 */
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
	/**
	 * 适配器
	 */
	private ChatMessageAdapter mAdapter;
	private Button btn_chat_send, btn_chat_keyboard, btn_chat_voice;
	EmoticonsEditText edit_user_comment;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			ChatMessage from = (ChatMessage) msg.obj;
			mDatas.add(from);
			mAdapter.notifyDataSetChanged();
			mChatView.setSelection(mDatas.size() - 1);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_robot);

		initView();

		mAdapter = new ChatMessageAdapter(this, mDatas);
		mChatView.setAdapter(mAdapter);

	}

	@SuppressLint("CutPasteId")
	private void initView() {
		initTopBarForLeft("与小陌对话");
		mChatView = (XListView) findViewById(R.id.id_chat_listView);
		mMsg = (EditText) findViewById(R.id.edit_user_comment);
		edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		edit_user_comment.setOnClickListener(this);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);

		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btn_chat_keyboard.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(s)) {
					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
					btn_chat_voice.setVisibility(View.GONE);
				} else {
					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
						btn_chat_voice.setVisibility(View.VISIBLE);
						btn_chat_send.setVisibility(View.GONE);
						btn_chat_keyboard.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
		btn_chat_voice.setOnClickListener(this);
		mDatas.add(new ChatMessage(Type.INPUT, "我是小陌机器人，很高兴为您服务"));
	}

	public void sendMessage() {
		final String msg = mMsg.getText().toString();
		if (TextUtils.isEmpty(msg)) {
			Toast.makeText(this, "您还没有填写信息呢...", Toast.LENGTH_SHORT).show();
			return;
		}

		ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
		to.setDate(new Date());
		mDatas.add(to);

		mAdapter.notifyDataSetChanged();
		mChatView.setSelection(mDatas.size() - 1);

		mMsg.setText("");

		new Thread() {
			@Override
			public void run() {
				ChatMessage from = null;
				try {
					from = HttpUtils.sendMsg(msg);
				} catch (Exception e) {
					from = new ChatMessage(Type.INPUT, "我艹！服务器挂了？");
				}
				Message message = Message.obtain();
				message.obj = from;
				mHandler.sendMessage(message);
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_user_comment:// 点击文本输入框
			mChatView.setSelection(mChatView.getCount() - 1);
			break;

		case R.id.btn_chat_voice:// 语音按钮
			btn_chat_voice.setVisibility(View.GONE);
			btn_chat_keyboard.setVisibility(View.VISIBLE);
			hideSoftInputView();
			break;
		case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
			btn_chat_keyboard.setVisibility(View.GONE);
			btn_chat_voice.setVisibility(View.VISIBLE);
			edit_user_comment.requestFocus();
			break;
		case R.id.btn_chat_send:// 发送文本
			sendMessage();
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected) {
				ShowToast(R.string.network_tips);
			}
			break;
		default:
			break;
		}

	}

}
