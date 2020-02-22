package com.estar.video.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.estar.lcm3djni;
import android.util.Log;

/**
 * 捕获崩溃异常
 * @author Se7en
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	private static final String TAG = "CrashHandler";

	private static CrashHandler mintance;
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Activity mactivity;

	private CrashHandler() {
	}
	
	// 获得单例
	public static CrashHandler getInstance() {
		if (mintance == null) {
			mintance = new CrashHandler();
		}
		return mintance;
	}
	
	public void init(Activity activity) {
		Log.v(TAG, TAG+" init");
		mactivity = activity;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();    // 获取默认的异常处理函数
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	public void Destory(){
		Log.v(TAG, TAG+" Destory");
		mintance = null;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		if ( handleException(ex) ) {
			lcm3djni.EstarLcm3DOff();     // 关闭光栅
			mDefaultHandler.uncaughtException(thread, ex);    
		}
	}
	
	private boolean handleException(final Throwable ex) {   
		if (ex == null) {   
            return false;   
        }   
		Log.e( TAG, mactivity.toString() +" " + ex.getStackTrace() );
		return true;
	}

}
