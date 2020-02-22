package com.estar.video.data;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 本地数据库内容提供者（提供增删改查接口）
 * 
 * @author zgl
 * 
 */
public class EstarVideoContentProvider extends ContentProvider {
	public static final String AUTHORITY = "com.estar.video.contentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public final static String VIDEO_DATABASE_NAME = "takee_video";
	public final static String VIDEO_TABLE_NAME = "videos";

	private final static int DB_VERSION = 1;

	public static final int INDEX_ID = 0;
	public static final int INDEX_DATE_LAST_PLAY = 1;
	public static final int INDEX_IS_3D = 2;
	public static final int INDEX_CONVERGENCE = 3;

	public static final String[] PROJECTION = new String[] { "_id", "date_last_play", "stereo_type", "convergence" };

	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase db;

	private static final UriMatcher sMatcher;
	private static final int VIDEOS = 1;
	private static final int VIDEO_ID = 2;

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jacp.videos";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.jacp.video";

	static {
		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sMatcher.addURI(AUTHORITY, "videos", VIDEOS);
		sMatcher.addURI(AUTHORITY, "video/#", VIDEO_ID);
	}

	@Override
	public boolean onCreate() {
		Map<String, String> sqls = new HashMap<String, String>();
		sqls.put(VIDEO_TABLE_NAME, "CREATE TABLE IF NOT EXISTS " + VIDEO_TABLE_NAME + "( " + PROJECTION[INDEX_ID] + " integer primary key autoincrement," + PROJECTION[INDEX_DATE_LAST_PLAY]
				+ " long ," + PROJECTION[INDEX_CONVERGENCE] + " int default " + VideoObject.CONVERGENCE_UN_CHECK + "," + PROJECTION[INDEX_IS_3D] + " int default " + VideoObject.VIDEO_TYPE_UN_CHECK + ");");
		mDatabaseHelper = new DatabaseHelper(getContext(), VIDEO_DATABASE_NAME, null, DB_VERSION, sqls);
		db = mDatabaseHelper.getWritableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor cursor = db.query(true, VIDEO_TABLE_NAME, projection, selection, selectionArgs, sortOrder, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db.insert(VIDEO_TABLE_NAME, null, values);
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);
		return CONTENT_URI;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);
		return db.delete(VIDEO_TABLE_NAME, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);
		return db.update(VIDEO_TABLE_NAME, values, selection, selectionArgs);
	}

	@Override
	public String getType(Uri uri) {
		switch (sMatcher.match(uri)) {
		case VIDEOS:
			return CONTENT_TYPE;
		case VIDEO_ID:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
}
