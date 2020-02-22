package com.takee.airtouch.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
/**
 * 空中触控服务操作类
 * @author zgl
 *
 */
public class ServiceHelper {
	private static String TAG = "service";
	private static final String ACTION_SERVICE = "com.takee.airtouch.aidl.AirTouchService2";
	private AirtouchAidl2 airtouchAidl;
	private Activity activity;
	private Handler handler;
	private boolean isBind = false;

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			airtouchAidl = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			airtouchAidl = AirtouchAidl2.Stub.asInterface(service);
			doAirtouch();
		}
	};
	
	public void setActivity(Activity activity) {
		if(this.activity==null){
			this.activity = activity;
		}
	}

	// step1
	public void bindService(Activity mActivity) {
		setActivity(mActivity);
		if (!isBind) {
			try{
				isBind = activity.getApplicationContext().bindService(new Intent(ACTION_SERVICE), connection, Context.BIND_AUTO_CREATE);
			}catch(Exception e){
				Log.v(TAG, "no service.");
				e.printStackTrace();
			}
		}
		Log.v(TAG, "--------bind service: " + isBind);
	}

	// step2
	public void setHandler(Handler mHandler) {
		handler = mHandler;
		Log.v(TAG, "-------setHandler  handler is null: " + (mHandler == null));
	}

	// step3
	public void unBindService() {
		if (activity != null && isBind) {
			isBind = false;
			Log.v(TAG, "-------unBindService");
			activity.getApplicationContext().unbindService(connection);
		}
	}

	private void doAirtouch() {
		try {
			if (airtouchAidl.getOuterAirTouchState() != 0 || airtouchAidl.getInnerAirTouchState() != 0) {
				airtouchAidl.releaseAirTouch();
			}
			boolean isEnable = airtouchAidl.getAirTouchIsEnable();
			boolean isSuccess = false;
			if (isEnable) {
				isSuccess = airtouchAidl.setOnGestureChangedListener(new OnGestureChangedListener.Stub() {
					@Override
					public void onChanged(int gesture) throws RemoteException {
						// if (!isSpaceEyesConnected()) {
						// return;
						// }
						if (handler != null) {
							handler.sendEmptyMessage(gesture);
							mHandler.sendEmptyMessage(Constants.KEEP_SCREEN_ON);
							Log.v(TAG, "-------gesture: " + gesture);
						}
					}
				});
				if (!isSuccess) {
					Log.v(TAG, "-------apply use gesture failed");
					if (handler != null) {
						handler.sendEmptyMessage(Constants.APPLY_FAILED);
					}
				}
			} else {
				Log.v(TAG, "-------airtouch is disable");
				if (handler != null) {
					handler.sendEmptyMessage(Constants.AIRTOUCH_IS_ENABLE);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/** 设置屏幕长亮 */
	private void setScreenOn() {
		if (activity != null) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mHandler.sendEmptyMessageDelayed(Constants.CLEAR_SCREEN_ON, 1000);
		}
	}

	/** 设置屏幕长亮 */
	private void clearScreenON() {
		if (activity != null) {
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.KEEP_SCREEN_ON:
				setScreenOn();
				break;
			case Constants.CLEAR_SCREEN_ON:
				clearScreenON();
				break;
			default:
				break;
			}
		};
	};

	/** 太空眼是否连接 */
	public boolean isSpaceEyesConnected() {
		if (airtouchAidl != null) {
			try {
				if (airtouchAidl.getOuterAirTouchState() != 0) {
					return true;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/** 空中触控是否已打开 */
	public boolean isUseAirtouch() {
		try {
			if (airtouchAidl != null && (airtouchAidl.getOuterAirTouchState() != 0 || airtouchAidl.getInnerAirTouchState() != 0)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 在activity的onResume中调用
	public void onResume(Handler mHandler) {
		Log.v(TAG, "-------ServiceHelper onResume");
		try {
			setHandler(mHandler);
			if (!isUseAirtouch()) {
				doAirtouch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 在activity的onWindowFocusChanged中调用
	public void onWindowFocusChanged(Activity mActivity, boolean hasFocus, Handler mHandler) {
		Log.v(TAG, "-------ServiceHelper onWindowFocusChanged 1: " + hasFocus);
		try {
			if (airtouchAidl != null) {
				if (hasFocus) {
					setHandler(mHandler);
					// if (isNotUseAirtouch()) {
					Log.v(TAG, "-------ServiceHelper onWindowFocusChanged 2: " + hasFocus);
					doAirtouch();
					// }
				}
			} else {
				Log.v(TAG, "-------ServiceHelper onWindowFocusChanged 3: " + hasFocus);
				setHandler(mHandler);
				setActivity(mActivity);
				bindService(activity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 在activity的onWindowFocusChanged中调用
	public void onWindowFocusChanged(boolean hasFocus, Handler mHandler) {
		Log.v(TAG, "-------ServiceHelper onWindowFocusChanged 1: " + hasFocus);
		try {
			if (airtouchAidl != null) {
				if (hasFocus) {
					setHandler(mHandler);
					if (!isUseAirtouch()) {
						Log.v(TAG, "-------ServiceHelper onWindowFocusChanged 2: " + hasFocus);
						doAirtouch();
					}
				}
				else{
					setHandler(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 在activity的onPause中调用
	public void onPause() {
		Log.v(TAG, "-------ServiceHelper onPause");
		try {
			if (airtouchAidl != null) {
				airtouchAidl.releaseAirTouch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
