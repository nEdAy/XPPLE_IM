package com.xpple.im.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.listener.SaveListener;

import com.xpple.im.R;
import com.xpple.im.bean.Feedback;
import com.xpple.im.view.HeaderLayout.onRightImageButtonClickListener;

public class SendFeedbackActivity extends ActivityBase implements
		OnClickListener {

	EditText et_content;
	static String msg;
	Button btn_feedback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendfeedback);
		btn_feedback = (Button) findViewById(R.id.btn_feedback);
		btn_feedback.setOnClickListener(this);
		initTopBarForBoth("帮助与反馈", R.drawable.btn_chat_send_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						startAnimActivity(new Intent(SendFeedbackActivity.this,
								FeedbackListActivity.class));
					}
				});
		et_content = (EditText) findViewById(R.id.et_content);
	}

	public void sendFeedback() {
		// TODO Auto-generated method stub
		String content = et_content.getText().toString();
		if (!TextUtils.isEmpty(content)) {
			if (content.equals(msg)) {
				Toast.makeText(this, "请勿重复提交反馈", Toast.LENGTH_SHORT).show();
			} else {
				msg = content;
				saveFeedbackMsg(content);
				Toast.makeText(this, "您的反馈信息已发送", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "请填写反馈内容", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 保存反馈信息到服务器
	 * 
	 * @param msg
	 *            反馈信息
	 */
	private void saveFeedbackMsg(String msg) {
		Feedback feedback = new Feedback();
		feedback.setContent(msg);
		feedback.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i("bmob", "反馈信息已保存到服务器");
			}

			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				Log.e("bmob", "保存反馈信息失败：" + arg0);
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		sendFeedback();
	}

}
