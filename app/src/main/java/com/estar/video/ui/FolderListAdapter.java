package com.estar.video.ui;

import java.util.List;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.data.BucketInfo;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.VideoUtils;
import com.estar.video.utils.VideoUtils.ImageCallback;

/**
 * 视频文件夹列表数据适配器
 * 
 * @author zgl
 * 
 */
public class FolderListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<BucketInfo> folders;

	public FolderListAdapter(Activity activity, LayoutInflater inflater, List<BucketInfo> folders) {
		this.activity = activity;
		this.inflater = inflater;

		// dlm = ((TakeeVideoApplication)
		// activity.getApplication()).getDataLoadManager();
		// folders = dlm.getFolders();
		this.folders = folders;
	}

	/** 设置数据 */
	public void setData(List<BucketInfo> folders) {
		this.folders = folders;
	}

	final class ViewHolder {
		ImageView iv_folder;
		TextView tv_folder_name;
		TextView tv_file_count;
	}

	@Override
	public int getCount() {
		return folders.size();
	}

	@Override
	public Object getItem(int position) {
		return folders.get(position);
	}

	@Override
	public long getItemId(int position) {
		return folders.get(position).getBucketId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.folder_list_item, null);
			holder = new ViewHolder();
			holder.iv_folder = (ImageView) convertView.findViewById(R.id.iv_folder);
			holder.tv_folder_name = (TextView) convertView.findViewById(R.id.tv_folder_name);
			holder.tv_file_count = (TextView) convertView.findViewById(R.id.tv_file_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// if(folders.size() <= 0){
		// return convertView;
		// }
		final BucketInfo bucketInfo = folders.get(position);
		holder.tv_folder_name.setText(bucketInfo.getBucket_display_name());
		holder.tv_file_count.setText(activity.getString(R.string.detail_file_count, DataLoadManager.getBucketVideos(bucketInfo.getBucketId()).size()));

		List<VideoObject> videos = DataLoadManager.getBucketVideos(folders.get(position).getBucketId());
		if (videos != null && videos.size() > 0) {
			final VideoObject videoObject = DataLoadManager.getBucketVideos(folders.get(position).getBucketId()).get(0);

			holder.iv_folder.setTag(videoObject);
			Bitmap bitmap = new VideoUtils().loadThumbImg(holder.iv_folder, null, new ImageCallback() {
				@Override
				public void imageLoadedNotify(ImageView imageView, Bitmap imgBitmap, ImageView iv_flag_3d, boolean is3d) {
					if (videoObject != null && videoObject.equals(imageView.getTag())) {
						imageView.setImageBitmap(imgBitmap);
					}
				}
			}, activity);
			if (bitmap != null) {
				if (!bitmap.isRecycled()) {
					holder.iv_folder.setImageBitmap(bitmap);
				}
			}
		}
		if (mSelectedPosition == position && VideoFileListActivity.isAirtouchEnable()) {
			convertView.setBackgroundResource(R.drawable.airtouch_focused);
		} else {
			convertView.setBackgroundResource(R.drawable.airtouch_not_focused);
		}
		return convertView;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private int mSelectedPosition = -1;

	public void setSelectedPosition(int selectedPosition) {
		mSelectedPosition = selectedPosition;
	}
}
