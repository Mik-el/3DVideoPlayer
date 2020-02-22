package com.estar.video.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.estar.video.VideoFileListActivity;
import com.estar.video.data.DataLoadManager;
import com.estar.video.utils.Constants;

/**
 * 所有Fragment基类
 * 
 * @author zgl
 * 
 */
public abstract class BaseFragment extends Fragment implements SupportAirtouch {
	/** 数据适配器 */
	protected BaseAdapter listAdapter;
	/** 数据更改，通知更新界面 */
	public static final int UPDATE_ACTIVITY = 0;
	protected LayoutInflater mInflater;
	protected Handler handler;
	protected View contentView;
	protected ViewGroup container;

	/** 电影的点击监听 */
	protected MovieItemOnClickListener mItemOnClickListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().registerReceiver(refrushReceiver, new IntentFilter(Constants.UPDATE_ACTIVITY_BROADCAST));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(refrushReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
		this.mInflater = inflater;
		this.container = container;
		if(DataLoadManager.videos.size() > 0){
			initList();
		}
//		initList();
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case UPDATE_ACTIVITY:
					if (listAdapter == null) {
						initList();
					} else {
						updateList();
					}
					break;
				default:
					break;
				}
			}
		};

		return contentView;
	};

	/**
	 * 进入界面或数据加载完毕初始化界面
	 */
	protected abstract void initList();

	/**
	 * 数据更新时执行刷新界面
	 */
	protected abstract void updateList();

	/** 监听MediaStore数据变化 */
	private BroadcastReceiver refrushReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			new Thread() {
				public void run() {
					handler.sendEmptyMessage(UPDATE_ACTIVITY);
				};
			}.start();
		}
	};
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			((VideoFileListActivity)getActivity()).fragment = this;
		}
	}
}
