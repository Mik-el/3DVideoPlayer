package com.estar.video.utils;

import java.io.File;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 常量类
 * 
 * @author zgl
 * 
 */
public class Constants {
	public final static int AIR_TOUCH_RIGHT = 1;
	public final static int AIR_TOUCH_LEFT = 2;
	public final static int AIR_TOUCH_UP = 3;
	public final static int AIR_TOUCH_DOWN = 4;
	/** 靠近 */
	public final static int AIR_TOUCH_CLOSE = 5;
	/** 顺时针 */
	public final static int AIR_TOUCH_CLOCKWISE = 7;
	/** 逆时针 */
	public final static int AIR_TOUCH_ANTI_CLOCKWISE = 8;
	public final static int AIR_TOUCH_BYE = 9;

	/** 数据是否已经加载至缓存 */
	public static boolean hasLoaded = false;

	/** MediaStore的content uri */
	public static final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

	/** 通知更新界面的广播 */
	public static final String UPDATE_ACTIVITY_BROADCAST = "com.estar.video.refrush";

	/** intent传递播放列表的参数名 */
	public static final String LIST_PARAMETER = "list";

	/** intent视频类型 */
	public final static String EXTRA_VIDEO_TYPE = "com.estar.video.video_type";

	/** 保存云立方视频缩略图的文件夹路径 */
	public final static String THUMBNAIL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video_cache/";
	/** 保存云立方视频列表的本地文件名 */
	public final static String ULIFANT_CACHE_LIST_FILE = THUMBNAIL_PATH + "temp.xml";

	/** 屏幕长宽 */
	public static int screenWidth, screenHeight;
}
