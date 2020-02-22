package com.estar.video;

import android.app.Application;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.MediaStore;

import com.estar.video.data.DataLoadManager;
import com.estar.video.data.EstarVideoContentProvider;
import com.estar.video.utils.Constants;
import com.takee.airtouch.aidl.ServiceHelper;

/**
 * 程序Application
 * 
 * @author zgl
 * 
 */
public class PlayerApplication extends Application {
	private DataLoadManager dlm;

	/** 空中触控服务操作类 */
	public ServiceHelper serviceHelper = new ServiceHelper();
	public final static int LOAD_FINISH = 0;
	private Handler contentHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_FINISH:
				sendBroadcast(new Intent(Constants.UPDATE_ACTIVITY_BROADCAST));
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();

		ContentObserver contentObserver = new EstarContentObserver(contentHandler);
		getApplicationContext().getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, contentObserver);
		getApplicationContext().getContentResolver().registerContentObserver(EstarVideoContentProvider.CONTENT_URI, false, contentObserver);
		dlm = DataLoadManager.getDataLoadManager(this);
		// 未加载数据则要从数据库加载数据
		if (!Constants.hasLoaded) {
			dlm.loadData(contentHandler);
		}
	}

	/** 获取数据加载管理类实例 */
	public DataLoadManager getDataLoadManager() {
		return dlm;
	}

	/** 监听MediaStore数据库变化，并通知界面更新 */
	private class EstarContentObserver extends ContentObserver {
		private Handler handler;

		public EstarContentObserver(Handler handler) {
			super(handler);
			this.handler = handler;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			handler.removeCallbacks(contentRunnable);
			// handler.post(contentRunnable);
			// 延迟发送，避免短时间内修改多条MediaStore记录会重复刷新多次
			handler.postDelayed(contentRunnable, 1000);
		}

		private Runnable contentRunnable = new Runnable() {
			@Override
			public void run() {
				dlm.loadData(contentHandler);
			}
		};
	}
}
