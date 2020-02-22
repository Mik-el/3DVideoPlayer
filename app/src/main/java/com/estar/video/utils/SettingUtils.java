package com.estar.video.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 设置项存储和获取
 * @author zgl
 *
 */
public class SettingUtils {
	/** SharedPreferences文件名 */
	public final static String SP_NAME = "settings";

	/** 是否循环播放 */
	public final static String KEY_CYCLE = "cycle";
	/** 是否连续播放 */
	public final static String KEY_CONTINUE = "continue";
	/** 是否记忆播放 */
	public final static String KEY_REMEMBER = "remember";
	/** 是否显示字幕 */
	public final static String KEY_SUBTITLE = "subtitle";
	/** 用户名 */
	public final static String KEY_USERNAME = "username";
	/** 密码 */
	public final static String KEY_PASSWORD = "password";

	/** 获取是否连续播放 */
	public static boolean getIsContinue(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		return sharedPreferences.getBoolean(KEY_CONTINUE, false);
	}

	/** 设置是否连续播放 */
	public static void setIsContinue(Context context, boolean isContinue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(KEY_CONTINUE, isContinue);
		editor.commit();
	}

	/** 是否记忆播放 */
	public static boolean getIsRemember(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		return sharedPreferences.getBoolean(KEY_REMEMBER, true);
	}

	/** 设置是否记忆播放 */
	public static void setIsRemember(Context context, boolean isRemember) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(KEY_REMEMBER, isRemember);
		editor.commit();
	}

	/** 是否显示字幕 */
	public static boolean getShowSubtitle(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		return sharedPreferences.getBoolean(KEY_SUBTITLE, true);
	}

	/** 设置是否显示字幕 */
	public static void setShowSubtitle(Context context, boolean isShowSubtitle) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(KEY_SUBTITLE, isShowSubtitle);
		editor.commit();
	}
	
	/** 获取用户名 */
	public static String getUsername(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		return sharedPreferences.getString(KEY_USERNAME, "");
	}

	/** 保存用户名和密码 */
	public static void saveUserInfo(Context context, String username,String password) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();
	}
	
	/** 获取密码 */
	public static String getPassword(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
		return sharedPreferences.getString(KEY_PASSWORD,"");
	}
}
