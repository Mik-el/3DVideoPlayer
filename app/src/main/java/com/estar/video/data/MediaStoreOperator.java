package com.estar.video.data;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;

import com.estar.video.utils.Constants;

/**
 * MediaStore相关操作类
 * 
 * @author Se7en
 * 
 */
public class MediaStoreOperator {
	private static final int INDEX_ID = 0;
	private static final int INDEX_DISPLAY_NAME = 1;
	private static final int INDEX_MIME_TYPE = 2;
	private static final int INDEX_DATE_TAKEN = 3;
	private static final int INDEX_DATE_ADDED = 4;
	private static final int INDEX_DATE_MODIFIED = 5;
	private static final int INDEX_DATA = 6;
	private static final int INDEX_DURATION = 7;
	private static final int INDEX_BUCKET_ID = 8;
	private static final int INDEX_SIZE = 9;
	private static final int INDEX_BUCKET_DISPLAY_NAME = 10;
	private static final int INDEX_RESOLUTION = 11;
	private static final int INDEX_BOOKMARK = 12;

	public static final String[] PROJECTION = new String[] { VideoColumns._ID, VideoColumns.DISPLAY_NAME, VideoColumns.MIME_TYPE, VideoColumns.DATE_TAKEN, VideoColumns.DATE_ADDED,
			VideoColumns.DATE_MODIFIED, VideoColumns.DATA, VideoColumns.DURATION, VideoColumns.BUCKET_ID, VideoColumns.SIZE, VideoColumns.BUCKET_DISPLAY_NAME, VideoColumns.RESOLUTION,
			VideoColumns.BOOKMARK };
	public static final String ORDER_COLUMN = VideoColumns.TITLE + " ASC, " + VideoColumns.DATE_TAKEN + " DESC, " + BaseColumns._ID + " DESC ";

	/** 获取MediaStore中的视频信息 */
	public static List<VideoObject> queryVideos(Context context) {
		List<VideoObject> videos = new ArrayList<VideoObject>();
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION, null, null, ORDER_COLUMN);
			if (cursor != null && cursor.moveToFirst()) {
				do{
					VideoObject videoObject = saveCursorToObject(cursor);
					videos.add(videoObject);
				}while(cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return videos;
	}

	/** 获取一条MediaStore中的视频信息 */
	public static VideoObject queryVideo(Context context, long id) {
		VideoObject videoObject = null;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION, PROJECTION[INDEX_ID] + "=" + id, null, ORDER_COLUMN);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					videoObject = saveCursorToObject(cursor);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
		return videoObject;
	}

	/** 更新MediaStore中指定的视频信息 */
	public void updateVideo(VideoObject object) {

	}

	/** 删除MediaStore中指定的视频 */
	public void deleteVideo(VideoObject object) {

	}

	public static QueryHandler getQueryHandler(ContentResolver cr, OnQueryCompleteListerner onQueryCompleteListerner) {
		return new MediaStoreOperator().new QueryHandler(cr, onQueryCompleteListerner);
	}

	public class QueryHandler extends AsyncQueryHandler {
		private OnQueryCompleteListerner onQueryCompleteListerner;

		public QueryHandler(final ContentResolver cr, OnQueryCompleteListerner onQueryCompleteListerner) {
			super(cr);
			this.onQueryCompleteListerner = onQueryCompleteListerner;
		}

		@Override
		protected void onQueryComplete(final int token, final Object cookie, final Cursor cursor) {
			onQueryCompleteListerner.onQueryComplete(token, cookie, cursor);
		}
	}

	/** 保存游标内数据至VideoObject */
	public static VideoObject saveCursorToObject(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_ID]));
		long bucketId = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_BUCKET_ID]));
		String path = cursor.getString(cursor.getColumnIndex(PROJECTION[INDEX_DATA]));
		String title = cursor.getString(cursor.getColumnIndex(PROJECTION[INDEX_DISPLAY_NAME]));
		String mimeType = cursor.getString(cursor.getColumnIndex(PROJECTION[INDEX_MIME_TYPE]));
		long duration = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_DURATION]));
		long dateTaken = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_DATE_TAKEN]));
		long dateModified = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_DATE_MODIFIED]));
		long dateAdded = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_DATE_ADDED]));
		long size = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_SIZE]));

		String bucket_display_name = cursor.getString(cursor.getColumnIndex(PROJECTION[INDEX_BUCKET_DISPLAY_NAME]));
		String resolution = cursor.getString(cursor.getColumnIndex(PROJECTION[INDEX_RESOLUTION]));

		long bookMark = cursor.getLong(cursor.getColumnIndex(PROJECTION[INDEX_BOOKMARK]));

		VideoObject videoObject = DataLoadManager.getVideoById(id);
		if(videoObject == null){
			videoObject = new VideoObject();
		}
		
		videoObject.setId(id);
		videoObject.setBucketId(bucketId);
		videoObject.setPath(path);
		videoObject.setTitle(title);
		videoObject.setMimeType(mimeType);
		videoObject.setDuration(duration);
		videoObject.setDateTaken(dateTaken);
		videoObject.setDateModified(dateModified);
		videoObject.setDateAdded(dateAdded);
		videoObject.setSize(size);
		videoObject.setBucket_display_name(bucket_display_name);
		videoObject.setResolution(resolution);
		videoObject.setBookMark(bookMark);
		return videoObject;
	}

	/** 从MediaStore取数据完毕的回调 */
	public interface OnQueryCompleteListerner {
		public void onQueryComplete(final int token, final Object cookie, final Cursor cursor);
	}

	/** 更新播放记录时间 */
	public static int updateBookMark(Context context, VideoObject videoObject, long bookMark) {
		videoObject.setBookMark(bookMark);
		ContentValues cv = new ContentValues();
		cv.put(PROJECTION[INDEX_BOOKMARK], bookMark);
		return context.getContentResolver().update(videoObject.getContentUri(), cv, null, null);
	}
	
	/** 清除播放记录 */
	public static int clearBookMark(Context context){
		ContentValues cv = new ContentValues();
		cv.put(PROJECTION[INDEX_BOOKMARK], 0);
		return context.getContentResolver().update(Constants.VIDEO_URI, cv, null, null);
	}
}
