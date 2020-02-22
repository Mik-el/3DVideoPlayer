package com.estar.ulifang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.utils.BitmapCache;
import com.estar.video.utils.Constants;

/**
 * 云立方视频列表适配器
 * @author zgl
 *
 */
public class UlifangListAdapter extends BaseAdapter {
	/** 存放缩放的图片的缓存 */
	private final static BitmapCache bitmapCache;
	static {
		/** 一级内存缓存基于 LruCache */
		bitmapCache = BitmapCache.getInstance();
	}
	
	private LayoutInflater mInflater;
	private List<UlifangMovieObject> mLists;
	public UlifangListAdapter(LayoutInflater inflater,List<UlifangMovieObject> lists){
		mInflater = inflater;
		mLists = lists;
	}
	
	final class ViewHolder {
		ImageView iv_thumbnail;
		ImageView iv_flag_3d;
		TextView tv_title;
		TextView tv_duration;
		TextView tv_file_size;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.video_grid_item, null);
			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
			holder.iv_thumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
			holder.iv_flag_3d = (ImageView) convertView.findViewById(R.id.iv_flag_3d);
			holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final UlifangMovieObject object = mLists.get(position);

		holder.tv_title.setText(object.getFilmName());
		holder.tv_duration.setText(formateUlifangDuration(object.getTime()));
		holder.tv_file_size.setVisibility(View.GONE);

		holder.iv_flag_3d.setVisibility(View.VISIBLE);

		holder.iv_thumbnail.setImageResource(R.drawable.thumbnail_default);
		holder.iv_thumbnail.setTag(object);
		Bitmap bitmap = getBitmapByUrl(object.getSnapTransverse(), holder.iv_thumbnail, new ImageCallback() {
			@Override
			public void imageLoadedNotify(ImageView imageView, Bitmap bm) {
				if (object != null && object.equals(imageView.getTag())) {
					imageView.setImageBitmap(bm);
				}
			}
		});
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				holder.iv_thumbnail.setImageBitmap(bitmap);
			}
		} else {
			holder.iv_thumbnail.setImageResource(R.drawable.thumbnail_default);
		}
		if (mSelectedPosition == position && VideoFileListActivity.isAirtouchEnable()) {
			convertView.setBackgroundResource(R.drawable.airtouch_focused);
		} else {
			convertView.setBackgroundResource(R.drawable.airtouch_not_focused);
		}
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return mLists.get(position).getId();
	}

	@Override
	public Object getItem(int position) {
		return mLists.get(position);
	}

	@Override
	public int getCount() {
		return mLists.size();
	}
	
	/** 格式化云立方视频的时间显示 */
	private String formateUlifangDuration(long time) {
		int h = (int) time / 60;
		int m = (int) time % 60;
		return String.format("%1$02d:%2$02d:00", h, m);
	}
	
	public interface ImageCallback {
		public void imageLoadedNotify(ImageView imageView, Bitmap imgBitmap);
	}

	private final List<Long> loadTasks = new ArrayList<Long>();

	private Bitmap getBitmapByUrl(final String uri, final ImageView container, final ImageCallback callback) {
		final UlifangMovieObject object = (UlifangMovieObject) container.getTag();
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bitmap bitmap = (Bitmap) msg.obj;
				callback.imageLoadedNotify(container, bitmap);
			}
		};

		if (bitmapCache.getBitmap(object) != null && bitmapCache.getBitmap(object) instanceof Bitmap) {
//			Message msg = new Message();
//			msg.obj = bitmapCache.getBitmap(object);
//			handler.sendMessage(msg);
			return bitmapCache.getBitmap(object);
		}

		Bitmap bitmap = BitmapFactory.decodeFile(Constants.THUMBNAIL_PATH + object.getFilmName() + ".png");
		if (bitmap != null) {
			bitmapCache.putBitmap(object, bitmap);
//			Message msg = new Message();
//			msg.obj = bitmap;
//			handler.sendMessage(msg);
			return bitmap;
		}

		if (loadTasks.contains(object.getId())) {
			return null;
		}

		loadTasks.add(object.getId());

		new Thread() {
			public void run() {
				Bitmap bitmap = null;
				URL url;
				try {
					url = new URL(uri);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while ((len = is.read(buffer)) > -1) {
						baos.write(buffer, 0, len);
					}
					baos.flush();

					InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
					InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());

					Options options = new Options();
					options.inJustDecodeBounds = true;
					bitmap = BitmapFactory.decodeStream(stream1, null, options);
					options.inSampleSize = options.outWidth / 200;
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(stream2, null, options);

					bitmapCache.putBitmap(object, bitmap);
					saveBitmapToStorage(bitmap, object.getFilmName() + ".png");

					Message msg = new Message();
					msg.obj = bitmap;
					handler.sendMessage(msg);
					loadTasks.remove(object.getId());

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
		return null;
	}
	
	/**
	 * 保存bitmap到文件
	 * 
	 * @param bitName
	 * @param bitmap
	 * @throws IOException
	 */
	private void saveBitmapToStorage(Bitmap bitmap, String imageName) throws IOException {
		File file = new File(Constants.THUMBNAIL_PATH + imageName);
		File fileDir = new File(Constants.THUMBNAIL_PATH);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		// 创建.nomedia文件，防止MediaStore扫描
		File nomediaFile = new File(Constants.THUMBNAIL_PATH + ".nomedia");
		if (!nomediaFile.exists()) {
			nomediaFile.createNewFile();
		}
		file.createNewFile();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
			fos.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			fos.close();
		}
	}
	
	private int mSelectedPosition = -1;

	public void setSelectedPosition(int selectedPosition) {
		mSelectedPosition = selectedPosition;
	}
}
