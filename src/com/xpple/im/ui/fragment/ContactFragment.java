package com.xpple.im.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.xpple.im.CustomApplcation;
import com.xpple.im.R;
import com.xpple.im.adapter.UserFriendAdapter;
import com.xpple.im.bean.User;
import com.xpple.im.ui.AddFriendActivity;
import com.xpple.im.ui.FragmentBase;
import com.xpple.im.ui.SetMyInfoActivity;
import com.xpple.im.util.CharacterParser;
import com.xpple.im.util.CollectionUtils;
import com.xpple.im.util.PinyinComparator;
import com.xpple.im.view.ClearEditText;
import com.xpple.im.view.HeaderLayout.onRightImageButtonClickListener;
import com.xpple.im.view.MyLetterView;
import com.xpple.im.view.MyLetterView.OnTouchingLetterChangedListener;
import com.xpple.im.view.dialog.NiftyDialogBuilder;

/**
 * 联系人
 * 
 * @ClassName: ContactFragment
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
@SuppressLint("DefaultLocale")
public class ContactFragment extends FragmentBase implements
		OnItemClickListener, OnItemLongClickListener {

	ClearEditText mClearEditText;

	TextView dialog;

	ListView list_friends;
	MyLetterView right_letter;

	private UserFriendAdapter userAdapter;// 好友

	List<User> friends = new ArrayList<User>();

	private InputMethodManager inputMethodManager;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
	}

	private void init() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initTopBarForRight("联系人", R.drawable.base_action_bar_add_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						startAnimActivity(AddFriendActivity.class);
					}
				});
		initListView();
		initRightLetterView();
		initEditText();
	}

	private void initEditText() {
		mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<User> filterDateList = new ArrayList<User>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = friends;
		} else {
			filterDateList.clear();
			for (User sortModel : friends) {
				String name = sortModel.getNick();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		userAdapter.updateListView(filterDateList);
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String username = sortModel.getNick();
			// 若没有username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel.getNick());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// 根据a-z进行排序
		Collections.sort(friends, pinyinComparator);
	}

	TextView tv_new_name;

	private void initListView() {
		list_friends = (ListView) findViewById(R.id.list_friends);
		userAdapter = new UserFriendAdapter(getActivity(), friends);
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);
		list_friends.setOnItemLongClickListener(this);

		list_friends.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(
								getActivity().getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		if (isVisibleToUser) {
			queryMyfriends();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initRightLetterView() {
		right_letter = (MyLetterView) findViewById(R.id.right_letter);
		dialog = (TextView) findViewById(R.id.dialog);
		right_letter.setTextView(dialog);
		right_letter
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// 该字母首次出现的位置
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				list_friends.setSelection(position);
			}
		}
	}

	/**
	 * 获取好友列表 queryMyfriends
	 * 
	 * @return void
	 * @throws
	 */
	private void queryMyfriends() {
		// 在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		CustomApplcation.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));

		Map<String, BmobChatUser> users = CustomApplcation.getInstance()
				.getContactList();
		// 组装新的User
		filledData(CollectionUtils.map2list(users));
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			list_friends.setAdapter(userAdapter);
		} else {
			userAdapter.notifyDataSetChanged();
		}

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

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		User user = (User) userAdapter.getItem(position);
		// 先进入好友的详细资料页面
		Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
		intent.putExtra("from", "other");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		User user = (User) userAdapter.getItem(position);
		showDeleteDialog(user);
		return true;
	}

	public void showDeleteDialog(final User user) {
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(getActivity());
		dialogBuilder.withTitle(user.getNick()).withMessage("删除联系人?")
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
						deleteContact(user);
						dialogBuilder.getDismiss();
					}
				}).show();
	}

	/**
	 * 删除联系人 deleteContact
	 * 
	 * @return void
	 * @throws
	 */
	private void deleteContact(final User user) {
		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("删除成功");
				// 删除内存
				CustomApplcation.getInstance().getContactList()
						.remove(user.getUsername());
				// 更新界面
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						userAdapter.remove(user);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("删除失败：" + arg1);
				progress.dismiss();
			}
		});
	}

}
