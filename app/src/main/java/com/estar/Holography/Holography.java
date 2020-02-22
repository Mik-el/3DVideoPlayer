package com.estar.Holography;

import android.util.Log;

public class Holography {

	public static String TAG = "Holography";
	static {
		System.loadLibrary("holography");
	}

	public static void startFaceDetector() {

	}

	public static void stopFaceDetector() {

	}

	public void initHolography() {
		Log.d(TAG, "initHolography");
		HolographyInit();
	}

	public static native void deinitHolography();

	public static native void HolographyInit();

	// public static native void HolographySetSize(int x,int y);
	public static native void startAutoSwitch();

	public static native void stopAutoSwitch();

	// public static native void setTexture(int tid);
	public static native void update(int a, int b);

	public static native int getx();

	public static native int gety();

	public static native int getdis();

	public static native int getCurGS();

}
