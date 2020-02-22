package com.takee.airtouch.aidl;

/**
 * 空中触控相关常量
 * 
 * @author zgl
 * 
 */
public class Constants {

	public static final int GESTURE_NONE = 0;// 没有手势

	public static final int GESTURE_RIGHT = 1;
	public static final int GESTURE_LEFT = 2;
	public static final int GESTURE_UP = 3;
	public static final int GESTURE_DOWN = 4;

	public static final int GESTURE_FORWARD = 5; // 接近 sensor
	public static final int GESTURE_BACKWARD = 6; // 远离 ensor
	public static final int GESTURE_CLOCKWISE = 7; // 顺时针旋转
	public static final int GESTURE_COUNT_CLOCKWISE = 8; // 逆时针旋转
	public static final int GESTURE_WAVE = 9; // 快速三次挥手

	public static final int INIT_GESTURE = 10; // 初始化手势
	public static final int AIRTOUCH_IS_ENABLE = 11;
	public static final int APPLY_FAILED = 12;
	public static final int KEEP_SCREEN_ON = 13;
	public static final int CLEAR_SCREEN_ON = 14;

	public static final int WORKSPACE_FOCUS_FALSE = 15;
	public static final int WORKSPACE_FOCUS_TRUE = 16;

	// /** 手势的handler模板 */
	// Handler mHandler3 = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// boolean bool = ((LauncherApplication) getApplication()).serviceHelper
	// .isSpaceEyesConnected();
	// if (!bool) {
	// // return;//如果不是太空眼则不做处理
	// }
	// switch (msg.what) {
	// case Constants.GESTURE_RIGHT:
	//
	// break;
	// case Constants.GESTURE_LEFT:
	//
	// break;
	// case Constants.GESTURE_UP:
	//
	// break;
	// case Constants.GESTURE_DOWN:
	//
	// break;
	// case Constants.GESTURE_FORWARD:
	//
	// break;
	// case Constants.GESTURE_CLOCKWISE:
	//
	// break;
	// case Constants.GESTURE_COUNT_CLOCKWISE:
	//
	// break;
	// case Constants.GESTURE_WAVE:
	//
	// break;
	// default:
	// break;
	// }
	// };
	// };

}
