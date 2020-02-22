package com.estar.video;

import java.io.IOException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.estar.Holography.CameraOption;

public class CameraManagerService extends IntentService {
	private final static String TAG = "CameraManagerService";
	
	public final static String ACTION_OPEN_CAMERA = "takee.camera.open";
	public final static String ACTION_CLOSE_CAMERA = "takee.camera.close";
	public final static String ACTION_START_PREVIEW = "takee.camera.preview.start";
	public final static String ACTION_STOP_PREVIEW = "takee.camera.preview.stop";

	public final static int OPEN_CAMERA = 0;
	public final static int CLOSE_CAMERA = 1;
	public final static int START_PREVIEW = 2;
	public final static int STOP_PREVIEW = 3;
	
	private static int mCameraState = -1;

	public static CameraOption mCameraOption;

	public CameraManagerService() {
		super("CameraManagerService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e(TAG,"CameraManagerService thread id " + Thread.currentThread().getId());
		if (mCameraOption == null)
			return;
		if (ACTION_OPEN_CAMERA.equals(intent.getAction())) {
			if(mCameraState == OPEN_CAMERA){
				return;
			}
			mCameraState = OPEN_CAMERA;
			Log.d(TAG,"camera open");
			try {
				mCameraOption.start();
				isCameraOpened = true;
				try {
					mCameraOption.startPreview();
					Log.d(TAG,"camera startPreview success 0");
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.d(TAG,"camera open success");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (ACTION_CLOSE_CAMERA.equals(intent.getAction())) {
			if(mCameraState == CLOSE_CAMERA){
				return;
			}
			mCameraState = CLOSE_CAMERA;
			Log.d(TAG,"camera close");
			mCameraOption.stop();
			isCameraOpened = false;
		} else if (ACTION_START_PREVIEW.equals(intent.getAction())) {
			if(mCameraState == START_PREVIEW || mCameraState == OPEN_CAMERA){
				return;
			}
			mCameraState = START_PREVIEW;
			Log.d(TAG,"camera start preview");
			try {
				mCameraOption.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (ACTION_STOP_PREVIEW.equals(intent.getAction())) {
			if(mCameraState == STOP_PREVIEW){
				return;
			}
			mCameraState = STOP_PREVIEW;
			Log.d(TAG,"camera stop preview");
			mCameraOption.stopPreview();
		}
	}
	
	public static boolean isCameraOpened = false;
	
	public static void mcoStartPreview(Context context){
		Intent intent = new Intent(context, CameraManagerService.class);
		intent.setAction(CameraManagerService.ACTION_START_PREVIEW);
		context.startService(intent);
	}
	
	public static void mcoStopPreview(Context context){
		Intent intent = new Intent(context, CameraManagerService.class);
		intent.setAction(CameraManagerService.ACTION_STOP_PREVIEW);
		context.startService(intent);
	}
	
	public static void mcoStart(Context context){
		Intent intent = new Intent(context, CameraManagerService.class);
		intent.setAction(CameraManagerService.ACTION_OPEN_CAMERA);
		context.startService(intent);
	}
	
	public static void mcoStop(Context context){
		Intent intent = new Intent(context, CameraManagerService.class);
		intent.setAction(CameraManagerService.ACTION_CLOSE_CAMERA);
		context.startService(intent);
	}
}
