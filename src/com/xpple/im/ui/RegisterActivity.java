package com.xpple.im.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.xpple.im.R;
import com.xpple.im.bean.User;
import com.xpple.im.config.BmobConstants;
import com.xpple.im.util.CommonUtils;
import com.xpple.im.util.PhotoUtil;
import com.xpple.im.view.DeletableEditText;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	ImageView et_avatar;
	Button btn_register;
	BmobChatUser currentUser;
	DeletableEditText et_username, et_nickname, et_password, et_email;
	DeletableEditText et_gender;
	LinearLayout layout_all;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initTopBarForLeft("注册");
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		et_avatar = (ImageView) findViewById(R.id.et_avatar);
		et_avatar.setOnClickListener(this);
		et_username = (DeletableEditText) findViewById(R.id.et_username);
		et_nickname = (DeletableEditText) findViewById(R.id.et_nickname);
		et_password = (DeletableEditText) findViewById(R.id.et_password);
		et_email = (DeletableEditText) findViewById(R.id.et_email);
		et_gender = (DeletableEditText) findViewById(R.id.et_gender);
		et_gender.setOnClickListener(this);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				register();
			}
		});
	}

	String path, url;

	private void register() {
		String name = et_username.getText().toString();
		String nickname = et_nickname.getText().toString();
		String password = et_password.getText().toString();
		String pwd_again = et_email.getText().toString();
		String gender = et_gender.getText().toString();
		Log.i("gender", gender);
		if (TextUtils.isEmpty(url)) {
			ShowToast(R.string.toast_error_avatar_null);
			return;
		}
		if (TextUtils.isEmpty(gender)) {
			et_gender.setShakeAnimation();
			ShowToast(R.string.toast_error_gender_null);
			return;
		}
		if (TextUtils.isEmpty(name)) {
			et_username.setShakeAnimation();
			ShowToast(R.string.toast_error_username_null);
			return;
		}
		if (TextUtils.isEmpty(nickname)) {
			et_nickname.setShakeAnimation();
			ShowToast(R.string.toast_error_nickname_null);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			et_password.setShakeAnimation();
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!pwd_again.equals(password)) {
			et_email.setShakeAnimation();
			ShowToast(R.string.toast_error_comfirm_password);
			return;
		}

		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(
				RegisterActivity.this);
		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
		// 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
		final User bu = new User();
		bu.setSex(gender == "男" ? true : false);
		bu.setUsername(name);
		bu.setNick(nickname);
		bu.setPassword(password);
		bu.setAvatar(url);
		// 将user和设备id进行绑定
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		bu.signUp(RegisterActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();
				ShowToast("注册成功");
				// 将设备与username进行绑定
				userManager.bindInstallationForRegister(bu.getUsername());
				// 更新地理位置信息
				updateUserLocation();
				// 发广播通知登陆页面退出
				sendBroadcast(new Intent(
						BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
				// 启动主页
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i(arg1);
				ShowToast("注册失败:" + arg1);
				progress.dismiss();
			}
		});
	}

	public String filePath = "";
	AlertDialog albumDialog;

	@SuppressLint("InflateParams")
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
				url = bmobFile.getFileUrl(RegisterActivity.this);
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

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	@SuppressLint("SimpleDateFormat")
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
				et_avatar.setImageBitmap(bitmap);
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
								et_gender.setText(which == 0 ? "男" : "女");
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.et_avatar:
			showAvatarPop();
			break;

		case R.id.et_gender:// 性别
			showSexChooseDialog();
			break;

		}
	}

}
