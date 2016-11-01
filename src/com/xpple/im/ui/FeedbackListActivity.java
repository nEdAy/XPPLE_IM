package com.xpple.im.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.xpple.im.R;
import com.xpple.im.bean.Feedback;

public class FeedbackListActivity extends ActivityBase {

	ListView listView;
	FeedbackAdapter adapter;
	TextView emptyView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_list);

		listView = (ListView) findViewById(R.id.lv_feedbacks);
		initTopBarForLeft("反馈信息");
		emptyView = new TextView(this);
		emptyView.setText("没有反馈记录");
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(25); // 设置字体大小
		@SuppressWarnings("deprecation")
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addContentView(emptyView, params);
		listView.setEmptyView(emptyView);

		BmobQuery<Feedback> query = new BmobQuery<Feedback>();
		query.order("-createdAt");
		query.findObjects(this, new FindListener<Feedback>() {

			@Override
			public void onSuccess(List<Feedback> arg0) {
				// TODO Auto-generated method stub
				adapter = new FeedbackAdapter(FeedbackListActivity.this, arg0);
				listView.setAdapter(adapter);
			}

			@Override
			public void onError(int code, String arg0) {
				// TODO Auto-generated method stub
				emptyView.setText(arg0);
			}
		});

	}

	static class FeedbackAdapter extends BaseAdapter {

		List<Feedback> fbs;
		LayoutInflater inflater;

		public FeedbackAdapter(Context context, List<Feedback> feedbacks) {
			this.fbs = feedbacks;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fbs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return fbs.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = inflater.inflate(R.layout.item_feedback, null);
				holder.content = (TextView) convertView
						.findViewById(R.id.tv_content);
				holder.time = (TextView) convertView.findViewById(R.id.tv_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Feedback feedback = fbs.get(position);

			holder.content.setText(feedback.getContent());
			holder.time.setText(feedback.getCreatedAt());

			return convertView;
		}

		static class ViewHolder {
			TextView content;
			TextView time;
		}

	}

}
