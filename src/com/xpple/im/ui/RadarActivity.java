package com.xpple.im.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.FindListener;

import com.xpple.im.R;
import com.xpple.im.bean.User;
import com.xpple.im.util.CollectionUtils;
import com.xpple.im.view.SearchDevicesView;

public class RadarActivity extends ActivityBase {
	private double QUERY_KILOMETERS = 100;// 默认查询1公里范围内的人
	SearchDevicesView search_device_view;
	String[] username, nick;
	double[] location_left, location_right;
	TextView text;
	ImageView image;
	private SensorManager sensorManager;
	boolean sensorFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_redar);
		initView();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// 开始定位
		onLocation();
	}

	private void initView() {
		initTopBarForLeft("巧遇附近");
		text = (TextView) findViewById(R.id.text_redar);
		text.setText("摇一摇");
		image = (ImageView) findViewById(R.id.image_redar);
		search_device_view = (SearchDevicesView) findViewById(R.id.search_device_view);
		search_device_view.setWillNotDraw(false);
		search_device_view.setSearching(true);
	}

	protected int arg_size;

	private void initNearByList() {

		// 更新地理位置信息
		updateUserLocation();
		if (!mApplication.getLatitude().equals("")
				&& !mApplication.getLongtitude().equals("")) {
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longtitude = Double
					.parseDouble(mApplication.getLongtitude());
			// 封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
			// 此方法默认每页查询100条数据,若想查询多于100条，可在查询之前设置BRequest.QUERY_LIMIT_COUNT，如：BRequest.QUERY_LIMIT_COUNT=20
			// 此方法是新增的查询指定10公里内的性别为女性的用户列表，默认包含好友列表
			// 如果你不想查询性别为女的用户，可以将equalProperty设为null或者equalObj设为null即可
			// 如果你不想包含好友列表的话，将查询条件中的isShowFriends设置为false就行
			BRequest.QUERY_LIMIT_COUNT = 100;// 查询100条数据
			userManager.queryKiloMetersListByPage(false, 0, "location",
					longtitude, latitude, true, QUERY_KILOMETERS, null, false,
					new FindListener<User>() {
						@Override
						public void onSuccess(List<User> arg0) {
							// TODO Auto-generated method stub
							if (CollectionUtils.isNotNull(arg0)) {
								// TODO
								arg_size = arg0.size();
								username = new String[arg_size];
								nick = new String[arg_size];
								location_left = new double[arg_size];
								location_right = new double[arg_size];
								for (int i = 0; i < arg_size; i++) {
									User user = arg0.get(i);
									username[i] = user.getUsername();
									nick[i] = user.getNick();
									location_left[i] = user.getLocation()
											.getLatitude();
									location_right[i] = user.getLocation()
											.getLongitude();
								}
								text.setText("查询成功，正在构造地图...");
								mHandler.sendEmptyMessageDelayed(10, 5000);
							} else {
								search_device_view.setSearching(false);
								text.setText("暂无附近的人!");
							}
						}

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							text.setText("因网络不稳定查询失败!");
							search_device_view.setSearching(false);
						}

					});
		} else {
			text.setText("用户个人位置信息未找到!");
			search_device_view.setSearching(false);
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			finish();
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("type", "near");
			bundle.putInt("size", arg_size);
			bundle.putStringArray("username", username);
			bundle.putStringArray("nick", nick);
			bundle.putDoubleArray("loc_left", location_left);
			bundle.putDoubleArray("loc_right", location_right);
			intent.putExtras(bundle);
			intent.setClass(RadarActivity.this, LocationActivity.class);
			RadarActivity.this.startActivity(intent);

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	MediaPlayer mMediaPlayer;

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 17;// 三星 i9250怎么晃都不会超过20，没办法，最高只设置19

			if ((Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math
					.abs(z) > medumValue) && (!sensorFlag)) {
				mMediaPlayer = MediaPlayer.create(getBaseContext(),
						R.raw.sensor);
				mMediaPlayer.start();
				initNearByList();
				text.setText("正在查询附近的人...");
				image.setVisibility(View.INVISIBLE);
				search_device_view.setVisibility(View.VISIBLE);
				sensorFlag = true;
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	@Override
	protected void onDestroy() {
		// 停止定位
		stopLocation();
		super.onDestroy();
	}
}
