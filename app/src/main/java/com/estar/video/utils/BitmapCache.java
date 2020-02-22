package com.estar.video.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 图片缓存管理类
 * 
 * @author zgl
 * 
 */
public class BitmapCache {
	private static BitmapCache instance;
	private LruCache<Object, Bitmap> mCache;

	private BitmapCache() {
		// 申请24M空间
		int maxSize = 24 * 1024 * 1024;
		mCache = new LruCache<Object, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(Object key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

			@Override
			protected void entryRemoved(boolean evicted, Object key, Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
	}

	public static BitmapCache getInstance() {
		if (instance == null) {
			instance = new BitmapCache();
		}
		return instance;
	}

	public Bitmap getBitmap(Object id) {
		Bitmap bitmap = null;
		if(id != null){
			bitmap = mCache.get(id);
		}
		return bitmap;
	}

	public void putBitmap(Object id, Bitmap bitmap) {
		if (id != null && bitmap != null)
			mCache.put(id, bitmap);
	}
}
