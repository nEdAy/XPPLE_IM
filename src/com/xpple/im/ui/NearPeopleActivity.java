package com.xpple.im.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.xpple.im.R;
import com.xpple.im.adapter.NearPeopleAdapter;
import com.xpple.im.bean.User;
import com.xpple.im.util.CollectionUtils;
import com.xpple.im.view.xlist.XListView;
import com.xpple.im.view.xlist.XListView.IXListViewListener;

/**
 * 附近的人列表
 * 
 * @ClassName: NewFriendActivity
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class NearPeopleActivity extends ActivityBase implements
		IXListViewListener, OnItemClickListener {

	XListView mListView;
	NearPeopleAdapter adapter;
	String from = "";

	List<User> nears = new ArrayList<User>();

	private double QUERY_KILOMETERS = 1;// 默认查询1公里范围内的人

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_people);
		initView();
	}

	private void initView() {
		initTopBarForLeft("附近的人");
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_near);
		mListView.setOnItemClickListener(this);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		//
		mListView.pullRefreshing();

		adapter = new NearPeopleAdapter(this, nears);
		mListView.setAdapter(adapter);
		initNearByList(false);
	}

	int curPage = 0;
	ProgressDialog progress;

	private void initNearByList(final boolean isUpdate) {
		if (!isUpdate) {
			progress = new ProgressDialog(NearPeopleActivity.this);
			progress.setMessage("正在查询附近的人...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}

		if (!mApplication.getLatitude().equals("")
				&& !mApplication.getLongtitude().equals("")) {
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longtitude = Double
					.parseDouble(mApplication.getLongtitude());
			// 封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
			// 此方法默认每页查询10条数据,若想查询多于10条，可在查询之前设置BRequest.QUERY_LIMIT_COUNT，如：BRequest.QUERY_LIMIT_COUNT=20
			// 此方法是新增的查询指定10公里内的性别为女性的用户列表，默认包含好友列表
			// 如果你不想查询性别为女的用户，可以将equalProperty设为null或者equalObj设为null即可
			// 如果你不想包含好友列表的话，将查询条件中的isShowFriends设置为false就行
			userManager.queryKiloMetersListByPage(isUpdate, 0, "location",
					longtitude, latitude, true, QUERY_KILOMETERS, null, null,
					new FindListener<User>() {

						@Override
						public void onSuccess(List<User> arg0) {
							// TODO Auto-generated method stub
							if (CollectionUtils.isNotNull(arg0)) {
								if (isUpdate) {
									nears.clear();
								}
								adapter.addAll(arg0);
								if (arg0.size() < BRequest.QUERY_LIMIT_COUNT) {
									mListView.setPullLoadEnable(false);
									ShowToast("附近的人搜索完成!");
								} else {
									mListView.setPullLoadEnable(true);
								}
							} else {
								ShowToast("暂无附近的人!");
							}

							if (!isUpdate) {
								progress.dismiss();
							} else {
								refreshPull();
							}
						}

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							ShowToast("暂无附近的人!");
							mListView.setPullLoadEnable(false);
							if (!isUpdate) {
								progress.dismiss();
							} else {
								refreshPull();
							}
						}

					});
		} else {
			ShowToast("暂无附近的人!");
			progress.dismiss();
			refreshPull();
		}

	}

	/**
	 * 查询更多
	 * 
	 * @Title: queryMoreNearList
	 * @Description: TODO
	 * @param @param page
	 * @return void
	 * @throws
	 */
	private void queryMoreNearList(int page) {
		double latitude = Double.parseDouble(mApplication.getLatitude());
		double longtitude = Double.parseDouble(mApplication.getLongtitude());
		// 查询1公里范围内的用户列表
		userManager.queryKiloMetersListByPage(true, page, "location",
				longtitude, latitude, true, QUERY_KILOMETERS, null, null,
				new FindListener<User>() {

					@Override
					public void onSuccess(List<User> arg0) {
						// TODO Auto-generated method stub
						if (CollectionUtils.isNotNull(arg0)) {
							adapter.addAll(arg0);
						}
						refreshLoad();
					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowLog("查询更多附近的人出错:" + arg1);
						mListView.setPullLoadEnable(false);
						refreshLoad();
					}

				});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		User user = (User) adapter.getItem(position - 1);
		Intent intent = new Intent(this, SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		initNearByList(true);
	}

	private void refreshLoad() {
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}

	private void refreshPull() {
		if (mListView.getPullRefreshing()) {
			mListView.stopRefresh();
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		double latitude = Double.parseDouble(mApplication.getLatitude());
		double longtitude = Double.parseDouble(mApplication.getLongtitude());
		// 这是查询1公里范围内的用户总数
		userManager.queryKiloMetersTotalCount(User.class, "location",
				longtitude, latitude, true, QUERY_KILOMETERS, null, null,
				new CountListener() {

					@Override
					public void onSuccess(int arg0) {
						// TODO Auto-generated method stub
						if (arg0 > nears.size()) {
							curPage++;
							queryMoreNearList(curPage);
						} else {
							ShowToast("数据加载完成");
							mListView.setPullLoadEnable(false);
							refreshLoad();
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowLog("查询附近的人总数失败" + arg1);
						refreshLoad();
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
