package com.estar.video.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.estar.video.PlayerApplication;
import com.estar.video.ui.HistoryFragment;
import com.estar.video.utils.Constants;
import com.estar.video.utils.Utils;

/**
 * 数据加载管理类
 * 
 * @author zgl
 * 
 */
public class DataLoadManager {
	private static Context context;
	private static DataLoadManager instance;

	public final static Vector<VideoObject> videos = new Vector<VideoObject>();

	private DataLoadManager() {
	}

	/** 获取数据管理实例 */
	public static DataLoadManager getDataLoadManager(Context context) {
		DataLoadManager.context = context;
		if (instance == null) {
			instance = new DataLoadManager();
		}
		return instance;
	}

	/**
	 * 内存无数据时，将从MediaStore扫描数据
	 */
	public void loadData(final Handler handler) {
		MediaStoreOperator.getQueryHandler(context.getContentResolver(), new MediaStoreOperator.OnQueryCompleteListerner() {
			@Override
			public void onQueryComplete(int token, Object cookie, Cursor cursor) {
				Vector<VideoObject> tempVector = new Vector<VideoObject>();
				if (cursor == null || cursor.getCount() == 0) {
					Utils.showLogError("没有扫描到视频");
					videos.clear();
					handler.sendEmptyMessage(PlayerApplication.LOAD_FINISH);
				} else {
					Cursor localCursor = null;
					try {
						if (cursor != null && cursor.moveToFirst()) {
							VideoObject videoObject;
							ContentValues cv;
							do {
								videoObject = MediaStoreOperator.saveCursorToObject(cursor);
								try {
									localCursor = LocalDataBaseOperator.query(context, videoObject.getId());
									if (localCursor == null || !localCursor.moveToFirst()) {
										// 本地不存在，添加至本地数据库
										cv = new ContentValues();
										if(videoObject.getPath() != null && videoObject.getPath().contains("com.estarCool/loadMovie")){
											videoObject.setVideoType(VideoObject.VIDEO_TYPE_3D_LR);
											cv.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_IS_3D], VideoObject.VIDEO_TYPE_3D_LR);
										}else{
											cv.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_IS_3D], VideoObject.VIDEO_TYPE_UN_CHECK);
										}
										cv.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID], videoObject.getId());
										cv.put(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_DATE_LAST_PLAY], 0);
										LocalDataBaseOperator.insert(context, cv);
									} else {
										long dateLastPlay = localCursor.getLong(localCursor.getColumnIndex(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_DATE_LAST_PLAY]));
										int is3d = localCursor.getInt(localCursor.getColumnIndex(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_IS_3D]));
										int convergence = localCursor.getInt(localCursor.getColumnIndex(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_CONVERGENCE]));
										videoObject.setDateLastPlay(dateLastPlay);
										videoObject.setConvergence(convergence);
										videoObject.setVideoType(is3d);
									}
								} finally {
									if (localCursor != null) {
										localCursor.close();
									}
								}
								tempVector.add(videoObject);
							} while (cursor.moveToNext());
						}
						synchronized (videos) {
							videos.clear();
							videos.addAll(tempVector);
						}
						Constants.hasLoaded = true;
					} finally {
						handler.sendEmptyMessage(PlayerApplication.LOAD_FINISH);
						if (cursor != null) {
							cursor.close();
						}
					}
				}
			}
		}).startQuery(0, null, Constants.VIDEO_URI, MediaStoreOperator.PROJECTION, null, null, MediaStoreOperator.ORDER_COLUMN);
	}

	/**
	 * 内存有数据，将检查每一条数据对应文件是否存在，不存在则删除数据
	 */
	public void checkData() {
		Cursor cursor = LocalDataBaseOperator.queryAll(context);
		VideoObject videoObject;
		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					// long dateLastPlay =
					// cursor.getLong(cursor.getColumnIndex(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_DATE_LAST_PLAY]));
					// int is3d =
					// cursor.getInt(cursor.getColumnIndex(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_IS_3D]));
					int id = cursor.getInt(cursor.getColumnIndex(EstarVideoContentProvider.PROJECTION[EstarVideoContentProvider.INDEX_ID]));
					videoObject = MediaStoreOperator.queryVideo(context, id);
					if (videoObject == null) {
						// MediaStore不存在该记录，删除本地数据库中对应数据
						LocalDataBaseOperator.delete(context, id);
					}
				} while (cursor.moveToNext());
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/** 获取指定目录下的视频 */
	public static List<VideoObject> getBucketVideos(long bucketId) {
		List<VideoObject> subVideos = new ArrayList<VideoObject>();
		for (VideoObject videoObject : videos) {
			if (videoObject.getBucketId() == bucketId) {
				subVideos.add(videoObject);
			}
		}
		return subVideos;
	}
	
	public static int getIndexInList(VideoObject object,List<VideoObject> list){
		int ret = 0;
		if(videos != null){
			for (VideoObject videoObject : list) {
				if (videoObject.getId() == object.getId()) {
					ret = list.indexOf(videoObject);
					break;
				}
			}
		}
		return ret;
	}

	/** 根据uri获取视频 */
	public static VideoObject getVideoByUri(Uri uri) {
		VideoObject ret = null;
		if (uri != null) {
			for (VideoObject videoObject : videos) {
				if (uri.equals(videoObject.getContentUri())) {
					ret = videoObject;
					break;
				}
			}
		}
		return ret;
	}

	/** 根据id获取视频 */
	public static VideoObject getVideoById(long id) {
		VideoObject ret = null;
		for (VideoObject videoObject : videos) {
			if (videoObject.getId() == id) {
				ret = videoObject;
				break;
			}
		}
		return ret;
	}

	/** 根据id数组获取视频 */
	public static List<VideoObject> getVideosByIdArray(long[] ids) {
		List<VideoObject> subVideos = new ArrayList<VideoObject>();
		for (long id : ids) {
			for (VideoObject videoObject : videos) {
				if (videoObject.getId() == id) {
					subVideos.add(videoObject);
				}
			}
		}

		return subVideos;
	}

	/** 根据实际路径获取视频content uri */
	public static VideoObject getVideoObject(Uri uri) {
		if(!Utils.isMediaUri(uri)){
			return null;
		}
		long id = Long.parseLong(uri.getLastPathSegment());
		for (VideoObject videoObject : videos) {
			if (id == videoObject.getId()) {
				return videoObject;
			}
		}
		return null;
	}

	/** 根据实际路径获取视频content uri */
	public static Uri getContentUri(String path) {
		for (VideoObject videoObject : videos) {
			if (videoObject.getPath().equals(path)) {
				return videoObject.getContentUri();
			}
		}
		return null;
	}

	// /** 获取所有视频文件夹 */
	// public List<Map<Long, String>> getFolders() {
	// List<Map<Long, String>> folders = new ArrayList<Map<Long, String>>();
	// // Map<Long,String> folders = new HashMap<Long,String>();
	// // for (VideoObject videoObject : videos) {
	// // if (!folders.containsKey(videoObject.getBucketId())) {
	// // folders.put(videoObject.getBucketId(),
	// // videoObject.getBucket_display_name());
	// // }
	// // }
	// return folders;
	// }

	/** 获取所有视频文件夹 */
	public List<BucketInfo> getFolders() {
		List<BucketInfo> buckets = new ArrayList<BucketInfo>();
		for (VideoObject videoObject : videos) {
			boolean isContains = false;
			for (BucketInfo bucketInfo : buckets) {
				if (bucketInfo.getBucketId() == videoObject.getBucketId()) {
					isContains = true;
					break;
				}
			}
			if (!isContains) {
				BucketInfo bucketInfo = new BucketInfo();
				bucketInfo.setBucketId(videoObject.getBucketId());
				bucketInfo.setBucket_display_name(videoObject.getBucket_display_name());
				bucketInfo.setPath(videoObject.getPath().substring(0, videoObject.getPath().lastIndexOf("/")));
				buckets.add(bucketInfo);
			}
		}

		return buckets;
	}

	/** 获取播放历史记录列表 */
	public Map<Integer, List<VideoObject>> getHistory() {
		List<VideoObject> today = new ArrayList<VideoObject>();
		List<VideoObject> yesterday = new ArrayList<VideoObject>();
		List<VideoObject> beforeYesterday = new ArrayList<VideoObject>();

		Map<Integer, List<VideoObject>> historys = new HashMap<Integer, List<VideoObject>>();

		long todayMillis = Utils.getMillisZero(0);
		long yesterdayMillis = Utils.getMillisZero(-1);
		for (VideoObject videoObject : videos) {
			if (videoObject.getDateLastPlay() > todayMillis) {
				today.add(videoObject);
			} else if (videoObject.getDateLastPlay() > yesterdayMillis && videoObject.getDateLastPlay() < todayMillis) {
				yesterday.add(videoObject);
			} else if (videoObject.getDateLastPlay() > 0) {
				beforeYesterday.add(videoObject);
			}
		}
		Collections.sort(today, new SortByDateLasyPlay());
		Collections.sort(yesterday, new SortByDateLasyPlay());
		Collections.sort(beforeYesterday, new SortByDateLasyPlay());

		if (today.size() > 0) {
			historys.put(HistoryFragment.HISTORY_TODAY, today);
		}
		if (yesterday.size() > 0) {
			historys.put(HistoryFragment.HISTORY_YESTERDAY, yesterday);
		}
		if (beforeYesterday.size() > 0) {
			historys.put(HistoryFragment.HISTORY_BEFORE_YESTERDAY, beforeYesterday);
		}
		return historys;
	}

	/** 根据最后播放时间排序 */
	class SortByDateLasyPlay implements Comparator<VideoObject> {
		@Override
		public int compare(VideoObject videoObject1, VideoObject videoObject2) {
			return (int) (videoObject2.getDateLastPlay() - videoObject1.getDateLastPlay());
		}
	}
}
