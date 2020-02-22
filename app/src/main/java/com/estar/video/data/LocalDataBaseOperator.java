package com.estar.video.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 本地数据库实际操作类
 * 
 * @author zgl
 * 
 */
public class LocalDataBaseOperator {
	/** 插入一条记录 */
	public static void insert(Context context, ContentValues cv) {
		context.getContentResolver().insert(EstarVideoContentProvider.CONTENT_URI, cv);
	}

	/** 删除一条记录 */
	public static int delete(Context context, long id) {
		return context.getContentResolver().delete(EstarVideoContentProvider.CONTENT_URI, EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID] + "=" + id, null);
	}

	/** 更新一条记录 */
	public static int update(Context context, ContentValues values, String where) {
		return context.getContentResolver().update(EstarVideoContentProvider.CONTENT_URI, values, where, null);
	}

	/** 查询一条记录 */
	public static Cursor query(Context context, long id) {
		return context.getContentResolver().query(EstarVideoContentProvider.CONTENT_URI, EstarVideoContentProvider.PROJECTION,
				EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID] + " = " + id, null, null);
	}

	/** 查询一条记录 */
	public static Cursor queryAll(Context context) {
		return context.getContentResolver().query(EstarVideoContentProvider.CONTENT_URI, EstarVideoContentProvider.PROJECTION, null, null, null);
	}

	/** 更新是否3D */
	public static int updateIs3d(Context context, VideoObject videoObject, int is3d) {
		videoObject.setVideoType(is3d);// 更新缓存

		ContentValues values = new ContentValues();
		values.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_IS_3D], is3d);
		String where = EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID] + "=" + videoObject.getId();
		return update(context, values, where);
	}
	
	/** 更新收敛性 */
	public static int updateConvergence(Context context, VideoObject videoObject, int convergence) {
		videoObject.setConvergence(convergence);// 更新缓存

		ContentValues values = new ContentValues();
		values.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_CONVERGENCE], convergence);
		String where = EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID] + "=" + videoObject.getId();
		return update(context, values, where);
	}

	/** 更新上次播放日期 */
	public static int updateDateLastPlay(Context context, VideoObject videoObject, long date_last_play) {
		videoObject.setDateLastPlay(date_last_play);

		ContentValues values = new ContentValues();
		values.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_DATE_LAST_PLAY], date_last_play);
		String where = EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID] + "=" + videoObject.getId();
		return update(context, values, where);
	}

	/** 清除一条播放记录 */
	public static int clearDateLastPlay(Context context,VideoObject videoObject) {
		ContentValues values = new ContentValues();
		values.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_DATE_LAST_PLAY], 0);
		return context.getContentResolver().update(EstarVideoContentProvider.CONTENT_URI, values, EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID] + "=" + videoObject.getId(), null);
	}
	
	/** 清除播放记录 */
	public static int clearDateLastPlay(Context context) {
		ContentValues values = new ContentValues();
		values.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_DATE_LAST_PLAY], 0);
		return context.getContentResolver().update(EstarVideoContentProvider.CONTENT_URI, values, null, null);
	}
}
