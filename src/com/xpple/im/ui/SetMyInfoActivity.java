package com.xpple.im.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.im.CustomApplcation;
import com.xpple.im.R;
import com.xpple.im.bean.User;
import com.xpple.im.config.BmobConstants;
import com.xpple.im.util.CollectionUtils;
import com.xpple.im.util.ImageLoadOptions;
import com.xpple.im.util.PhotoUtil;
import com.xpple.im.view.dialog.NiftyDialogBuilder;

/**
 * 个人资料页面
 * 
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility",
		"InflateParams" })
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

	TextView tv_set_name, tv_set_nick, tv_set_gender, black_tips;
	ImageView iv_set_avatar, btn_clear;
	LinearLayout layout_all;

	Button btn_chat, btn_back, btn_add_friend, btn_logout;

	String from = "";
	String username = "";
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_info);
		from = getIntent().getStringExtra("from");// me add other
		username = getIntent().getStringExtra("username");
		initView();
	}

	private void initView() {
		initTopBarForOnlyTitle("发现");
		iv_set_avatar = (ImageView) findViewById(R.id.iv_set_avatar);
		btn_clear = (ImageView) findViewById(R.id.btn_clear);
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		black_tips = (TextView) findViewById(R.id.black_tips);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
		btn_clear.setOnClickListener(this);
		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		btn_back.setEnabled(false);
		if (from.equals("me")) {
			iv_set_avatar.setOnClickListener(this);
			tv_set_nick.setOnClickListener(this);
			tv_set_gender.setOnClickListener(this);
			btn_logout.setVisibility(View.VISIBLE);
			btn_logout.setOnClickListener(this);
			btn_back.setVisibility(View.GONE);
			btn_chat.setVisibility(View.GONE);
			btn_add_friend.setVisibility(View.GONE);
		} else {
			// 不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
			btn_chat.setVisibility(View.VISIBLE);
			btn_logout.setVisibility(View.GONE);
			btn_chat.setOnClickListener(this);
			if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
				if (mApplication.getContactList().containsKey(username)) {// 是好友
					// btn_chat.setVisibility(View.VISIBLE);
					// btn_chat.setOnClickListener(this);
					btn_back.setVisibility(View.VISIBLE);
					btn_back.setOnClickListener(this);
				} else {
					// btn_chat.setVisibility(View.GONE);
					btn_back.setVisibility(View.GONE);
					btn_add_friend.setVisibility(View.VISIBLE);
					btn_add_friend.setOnClickListener(this);
				}
			} else {// 查看他人
				// btn_chat.setVisibility(View.VISIBLE);
				// btn_chat.setOnClickListener(this);
				btn_back.setVisibility(View.VISIBLE);
				btn_back.setOnClickListener(this);
			}
			initOtherData(username);
		}
	}

	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("sex= " + user.getSex());
		initOtherData(user.getUsername());
	}

	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					btn_chat.setEnabled(true);
					btn_back.setEnabled(true);
					btn_add_friend.setEnabled(true);
					updateUser(user);
				} else {
					ShowLog("onSuccess 查无此人");
				}
			}
		});
	}

	private void updateUser(User user) {
		// 更改
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");
		// 检测是否为黑名单用户
		if (from.equals("other")) {
			if (BmobDB.create(this).isBlackUser(user.getUsername())) {
				btn_back.setVisibility(View.GONE);
				black_tips.setVisibility(View.VISIBLE);
			} else {
				btn_back.setVisibility(View.VISIBLE);
				black_tips.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_set_avatar.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (from.equals("me")) {
			initMeData();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_chat:// 发起聊天
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", user);
			startAnimActivity(intent);
			finish();
			break;
		case R.id.iv_set_avatar:
			showAvatarPop();
			break;
		case R.id.tv_set_nick:
			startAnimActivity(UpdateInfoActivity.class);
			break;
		case R.id.tv_set_gender:// 性别
			showSexChooseDialog();
			break;
		case R.id.btn_back:// 黑名单
			showBlackDialog(user.getUsername());
			break;
		case R.id.btn_add_friend:// 添加好友
			addFriend();
			break;
		case R.id.btn_clear:// 关闭界面
			finish();
			break;
		case R.id.btn_logout:// 注销
			CustomApplcation.getInstance().logout();
			this.finish();
			startActivity(new Intent(this, LoginActivity.class));
			break;
		}
	}

	String[] sexs = new String[] { "男", "女" };

	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
				.setTitle("单选框")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(sexs, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								BmobLog.i("点击的是" + sexs[which]);
								updateInfo(which);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	/**
	 * 修改资料 updateInfo
	 * 
	 * @Title: updateInfo
	 * @return void
	 * @throws
	 */
	private void updateInfo(int which) {
		final User u = new User();
		if (which == 0) {
			u.setSex(true);
		} else {
			u.setSex(false);
		}
		updateUserData(u, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("修改成功");
				tv_set_gender.setText(u.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	/**
	 * 添加好友请求
	 * 
	 * @Title: addFriend
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求
		BmobChatManager.getInstance(this).sendTagMessage(
				BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
				new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
						ShowLog("发送请求失败:" + arg1);
					}
				});
	}

	/**
	 * 显示黑名单提示框
	 * 
	 * @Title: showBlackDialog
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void showBlackDialog(final String username) {
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(this);
		dialogBuilder.withTitle("加入黑名单")
				.withMessage("加入黑名单，你将不再收到对方的消息，确定要继续吗？")
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
						// 添加到黑名单列表
						userManager.addBlack(username, new UpdateListener() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								ShowToast("黑名单添加成功!");
								btn_back.setVisibility(View.GONE);
								black_tips.setVisibility(View.VISIBLE);
								// 重新设置下内存中保存的好友列表
								CustomApplcation.getInstance().setContactList(
										CollectionUtils.list2map(BmobDB.create(
												SetMyInfoActivity.this)
												.getContactList()));
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ShowToast("黑名单添加失败:" + arg1);
							}
						});
						dialogBuilder.getDismiss();
					}
				}).show();
	}

	public String filePath = "";
	AlertDialog albumDialog;

	private void showAvatarPop() {
		albumDialog = new AlertDialog.Builder(this).create();
		albumDialog.setCanceledOnTouchOutside(false);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_usericon,
				null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView) v.findViewById(R.id.camera_pic);
		cameraPic.setOnClickListener(new OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowLog("点击拍照");
				albumDialog.dismiss();
				File dir = new File(BmobConstants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 原图
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()));
				filePath = file.getAbsolutePath();// 获取相片的保存路径
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		albumPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowLog("点击相册");
				albumDialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});

	}

	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (albumDialog != null) {
				albumDialog.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			if (albumDialog != null) {
				albumDialog.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropavatar(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	private void updateUserAvatar(final String url) {
		User u = new User();
		u.setAvatar(url);
		updateUserData(u, new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("头像更新成功！");
				// 更新头像
				refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}

	String path;

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropavatar(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avatar.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()) + ".png";
				path = BmobConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	private void updateUserData(User user, UpdateListener listener) {
		User current = userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}

}
