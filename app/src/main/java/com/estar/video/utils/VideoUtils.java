package com.estar.video.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ContentResolver;
import android.content.Context;
import android.estar.lcm3djni;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.widget.ImageView;

import com.estar.video.MoviePlayer;
import com.estar.video.data.LocalDataBaseOperator;
import com.estar.video.data.VideoObject;

/**
 * 视频相关工具类
 * 
 * @author zgl
 * 
 */
public class VideoUtils {
	/** 3D检测的任务列表 */
	public final static List<Long> checkList = new ArrayList<Long>();

	// 存放缩放的图片的缓存
	// public final static Hashtable<String, SoftReference<Bitmap>>
	// imageThumbCache = new Hashtable<String, SoftReference<Bitmap>>();
	// private final static Vector<Long> createThumbList;
	/** 存放缩放的图片的缓存 */
	private final static BitmapCache bitmapCache;
	static {
		/** 获取缩略图的任务列表 */
		// createThumbList = new Vector<Long>();
		/** 一级内存缓存基于 LruCache */
		bitmapCache = BitmapCache.getInstance();
	}

	/** 最大线程数 */
	private final static int MAX_THREAD_NUM = 1;
	/** 线程池 */
	private static ExecutorService threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM);

	/** 缩略图生成完毕的回调 */
	public interface ImageCallback {
		public void imageLoadedNotify(ImageView imageView, Bitmap imgBitmap, ImageView iv_flag_3d, boolean is3d);
	}

	/** 生成实际缩略图的图片 */
	public Bitmap loadThumbImg(final ImageView imageView, final ImageView iv_flag_3d, final ImageCallback callback, final Context context) {
		final VideoObject videoObject = (VideoObject) imageView.getTag();
		Utils.showLogDebug("loadThumbImg file path :" + videoObject.getPath());
		if (bitmapCache.getBitmap(videoObject.getId()) != null && videoObject.getVideoType() != VideoObject.VIDEO_TYPE_UN_CHECK && bitmapCache.getBitmap(videoObject.getId()) instanceof Bitmap) {
			//从缓存读取缩略图
			Utils.showLogDebug("loadThumbImg from bitmap cache");
			return bitmapCache.getBitmap(videoObject.getId());
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Utils.showLogDebug("loadThumbImg callback, video type " + msg.arg1);
				callback.imageLoadedNotify(imageView, (Bitmap) msg.obj, iv_flag_3d, VideoObject.isVideoType3d(msg.arg1));
			}
		};
		if (!checkList.contains(videoObject.getId())) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					Utils.showLogDebug("check list add :" + videoObject.getId());
					checkList.add(videoObject.getId());
					Bitmap bitmap = bitmapCache.getBitmap(videoObject.getId());
					if (bitmap == null) {
						bitmap = getVideoThumbnail(context.getContentResolver(), videoObject);
					}
					if (bitmap != null) {
						// 如果是VIDEO_TYPE_UN_CHECK的视频，则暂时显示2D，并启动线程进行判断，判断如果是3D后再重绘ImageView
						// 如果是3D则截取一半
						switch (videoObject.getVideoType()) {
						case VideoObject.VIDEO_TYPE_UN_CHECK:
							Check3dThread check3dThread = new Check3dThread(videoObject, bitmap, context, handler, callback);

							if (threadPools.isShutdown()) {
								threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM);
							}
							threadPools.execute(check3dThread);
							break;
						case VideoObject.VIDEO_TYPE_3D_LR:
							bitmap = create3dThumbnailLR(bitmap, context);
							checkList.remove(videoObject.getId());
							break;
						case VideoObject.VIDEO_TYPE_3D_UD:
							bitmap = create3dThumbnailUD(bitmap, context);
							checkList.remove(videoObject.getId());
							break;
						case VideoObject.VIDEO_TYPE_2D:
							checkList.remove(videoObject.getId());
							break;
						default:
							break;
						}
						bitmapCache.putBitmap(videoObject.getId(), bitmap);
						Message msg = new Message();
						msg.obj = bitmap;
						msg.arg1 = videoObject.getVideoType();
						handler.sendMessage(msg);
					}
				}
			};
			thread.start();
		}
		return bitmapCache.getBitmap(videoObject.getId());
	}

	/** 关闭线程池 */
	public static void shutdownThreadPools() {
		Utils.showLogDebug("shut down ThreadPools");
		checkList.clear();
		threadPools.shutdownNow();
	}

	/** 获得缩略图 */
	public static Bitmap getVideoThumbnail(ContentResolver contentResolver, VideoObject videoObject) {
		Utils.showLogDebug("getVideoThumbnail");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		try {
			Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoObject.getId(), Images.Thumbnails.MINI_KIND, options);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/** 生成图片的缩略图 */
	public static Bitmap getThumbBitMap(String path) throws IOException {
		if (path == null)
			return null;
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, Images.Thumbnails.MINI_KIND);
		return bitmap;
	}

	/** 将2D左右格式缩略图转换成3D缩略图 */
	private static Bitmap create3dThumbnailLR(Bitmap bitmap, Context context) {
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 2, bitmap.getHeight());
		bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 2, bitmap.getHeight(), false);
		return bitmap;
	}

	/** 将2D上下格式缩略图转换成3D缩略图 */
	private static Bitmap create3dThumbnailUD(Bitmap bitmap, Context context) {
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight() / 2);
		bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight() * 2, false);
		return bitmap;
	}

	private class Check3dThread extends Thread {
		private final VideoObject videoObject;
		private final Handler handler;
		private Bitmap bitmap;
		private Context context;

		public Check3dThread(VideoObject videoObject, Bitmap bitmap, Context context, Handler handler, ImageCallback callback) {
			this.videoObject = videoObject;
			this.handler = handler;
			this.bitmap = bitmap;
			this.context = context;
		}

		@Override
		public void run() {
			Utils.showLogDebug("Check3dThread run,videoObject path:" + videoObject.getPath());
			// 如果硬解码检测是3D视频，则截取一半图片显示
			int ret = naviveCheckVideo(context, videoObject);
			if (ret == VideoObject.VIDEO_TYPE_3D_LR) {
				// 如果是3D视频，则截取一半图片，并设置到ImageView
				bitmap = create3dThumbnailLR(bitmap, context);
			} else if (ret == VideoObject.VIDEO_TYPE_3D_UD) {
				// 如果是3D视频，则截取一半图片，并设置到ImageView
				bitmap = create3dThumbnailUD(bitmap, context);
			}
			if (VideoObject.isVideoType3d(ret)) {
				bitmapCache.putBitmap(videoObject.getId(), bitmap);
				Message msg = new Message();
				msg.obj = bitmap;
				msg.arg1 = ret;
				handler.sendMessage(msg);
			}
			checkList.remove(videoObject.getId());
			// createThumbList.remove(videoObject.getId());
			super.run();
		}

	}

	/**
	 * 硬解码检测2D/3D
	 * 
	 * @param filePath
	 *            视频文件物理路径
	 * @return -2:该视频不能播放; -1:出错; 0:2D视频 1:左右3D视频 2:上下3D视频
	 */
	public synchronized static int naviveCheckVideo(Context context, VideoObject videoObject) {
		int ret = -1;
		String filePath = videoObject.getPath();
		// t1和t1s硬解,平板软解
		if (lcm3djni.hasLoadedV3() && ("takee 1".equals(Build.MODEL) || "takee 1S".equals(Build.MODEL))) {
			ret = lcm3djni.native_hardwareDecode_setDataSource(filePath);
			if (ret == 1) {
				ret = lcm3djni.native_hardwareDecode_Is3D();
				Utils.showLogDebug("native_hardwareDecode_Is3D return " + ret);
			} else {
				ret = -1;
			}
		} else {
			ret = lcm3djni.natvie_estarIsVideo3D_filename(filePath);
			Utils.showLogDebug("natvie_estarIsVideo3D_filename return " + ret);
		}
		
		switch (ret) {
		case VideoObject.VIDEO_TYPE_3D_LR:
			LocalDataBaseOperator.updateIs3d(context, videoObject, VideoObject.VIDEO_TYPE_3D_LR);// 更新数据库
			break;
		case VideoObject.VIDEO_TYPE_3D_UD:
			LocalDataBaseOperator.updateIs3d(context, videoObject, VideoObject.VIDEO_TYPE_3D_UD);// 更新数据库
			break;
		default:
			LocalDataBaseOperator.updateIs3d(context, videoObject, VideoObject.VIDEO_TYPE_2D);// 更新数据库
			break;
		}
		return ret;
	}

	/** 检测2D、3D，带handler回调 */
	public synchronized static void naviveCheckVideoWithHandler(final Context context, final VideoObject videoObject, final Handler handler) {
		new Thread() {
			public void run() {
				Utils.showLogDebug("naviveCheckVideoWithHandler begin");
				int ret = naviveCheckVideo(context, videoObject);
				Message msg = new Message();
				msg.obj = ret;
				msg.what = MoviePlayer.CHECK_FINISHED;
				handler.sendMessage(msg);
				Utils.showLogDebug("naviveCheckVideoWithHandler end return result " + ret);
			};
		}.start();
	}
}
