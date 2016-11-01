package com.xpple.im.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.im.R;
import com.xpple.im.adapter.base.BaseListAdapter;
import com.xpple.im.adapter.base.ViewHolder;
import com.xpple.im.util.ImageLoadOptions;

/**
 * 黑名单
 * 
 * @ClassName: BlackListAdapter
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class BlackListAdapter extends BaseListAdapter<BmobChatUser> {

	public BlackListAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("InflateParams")
	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_blacklist, null);
		}
		final BmobChatUser contract = getList().get(arg0);
		TextView tv_friend_name = ViewHolder.get(convertView,
				R.id.tv_friend_name);
		ImageView iv_avatar = ViewHolder.get(convertView,
				R.id.img_friend_avatar);
		String avatar = contract.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}
		tv_friend_name.setText(contract.getNick());
		return convertView;
	}

}
