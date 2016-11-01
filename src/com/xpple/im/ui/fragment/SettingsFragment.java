package com.xpple.im.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xpple.im.R;
import com.xpple.im.ui.AddFriendActivity;
import com.xpple.im.ui.BlackListActivity;
import com.xpple.im.ui.FragmentBase;
import com.xpple.im.ui.NewFriendActivity;
import com.xpple.im.ui.RadarActivity;
import com.xpple.im.ui.RobotActivity;
import com.xpple.im.ui.SetActivity;
import com.xpple.im.ui.SetMyInfoActivity;
import com.xpple.im.util.ImageLoadOptions;
import com.xpple.im.view.RoundImageView;

/**
 * 设置
 * 
 * @ClassName: SetFragment
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
@SuppressLint("SimpleDateFormat")
public class SettingsFragment extends FragmentBase implements OnClickListener,
		OnTouchListener {
	private View parentView;
	private ImageView push_btn, set_btn;
	private RoundImageView avatar;
	TextView image1, image2, image3, image4, name;
	public ImageLoader imageLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_set, container, false);
		return parentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// Inflate the layout for this fragment
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		setUpViews();
	}

	private void setUpViews() {
		initTopBarForOnlyTitle("发现");
		name = (TextView) parentView.findViewById(R.id.showName);
		avatar = (RoundImageView) parentView.findViewById(R.id.avatar);
		image1 = (TextView) parentView.findViewById(R.id.image1);
		image2 = (TextView) parentView.findViewById(R.id.image2);
		image3 = (TextView) parentView.findViewById(R.id.image3);
		image4 = (TextView) parentView.findViewById(R.id.image4);

		push_btn = (ImageView) parentView.findViewById(R.id.push_btn);
		set_btn = (ImageView) parentView.findViewById(R.id.set_btn);
		push_btn.setOnClickListener(this);
		set_btn.setOnClickListener(this);

		name.setOnClickListener(this);
		avatar.setOnClickListener(this);
		image1.setOnClickListener(this);
		image2.setOnClickListener(this);
		image3.setOnClickListener(this);
		image4.setOnClickListener(this);

		push_btn.setOnTouchListener(this);
		set_btn.setOnTouchListener(this);

		image1.setOnTouchListener(this);
		image2.setOnTouchListener(this);
		image3.setOnTouchListener(this);
		image4.setOnTouchListener(this);
		loadInformation();
	}

	private void loadInformation() {
		BmobChatUser user = BmobUserManager.getInstance(getActivity())
				.getCurrentUser();
		name.setText(user.getUsername());

		String Avatar = user.getAvatar();
		if (Avatar != null && !Avatar.equals("")) {
			ImageLoader.getInstance().displayImage(Avatar, avatar,
					ImageLoadOptions.getOptions());
		} else {
			avatar.setImageResource(R.drawable.default_head);
		}

	}

	private void setMyInfo() {
		Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
		intent.putExtra("from", "me");
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.push_btn:
			Intent intent = new Intent(getActivity(), NewFriendActivity.class);
			intent.putExtra("from", "contact");
			startAnimActivity(intent);
			break;
		case R.id.set_btn:
			startAnimActivity(SetActivity.class);
			break;
		case R.id.name:
			setMyInfo();
			break;
		case R.id.avatar:
			setMyInfo();
			break;
		case R.id.image1:
			startAnimActivity(RadarActivity.class);
			break;
		case R.id.image2:
			startAnimActivity(AddFriendActivity.class);
			break;
		case R.id.image3:
			startAnimActivity(RobotActivity.class);
			break;
		case R.id.image4:
			startAnimActivity(BlackListActivity.class);
			break;
		default:
			break;
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO 自动生成的方法存根
		int Action = event.getAction();
		if (Action == MotionEvent.ACTION_DOWN) {
			playHeartbeatAnimation(v);
		}
		return false;
	}
}