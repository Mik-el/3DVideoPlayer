package com.estar.video.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;
import com.estar.video.utils.VideoUtils;
import com.estar.video.utils.VideoUtils.ImageCallback;

/**
 * 视频文件列表数据适配器
 * 
 * @author zgl
 * 
 */
public class GridAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<VideoObject> videos;

	public GridAdapter(Context context, LayoutInflater inflater, List<VideoObject> videos) {
		this.context = context;
		this.inflater = inflater;
		this.videos = videos;
	}

	public void setData(List<VideoObject> videos) {
		this.videos = videos;
	}

	final class ViewHolder {
		ImageView iv_thumbnail;
		ImageView iv_flag_3d;
		TextView tv_title;
		TextView tv_duration;
		TextView tv_file_size;
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return videos.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.video_grid_item, null);
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

		// if(videos.size() <= 0){
		// return convertView;
		// }
		final VideoObject videoObject = videos.get(position);
		holder.tv_title.setText(videoObject.getTitle());
		holder.tv_duration.setText(Utils.formatDuration((int) videoObject.getDuration()));
		holder.tv_file_size.setText(Formatter.formatFileSize(context, videoObject.getSize()));

		if (videoObject.is3dVideo()) {
			holder.iv_flag_3d.setVisibility(View.VISIBLE);
		} else {
			holder.iv_flag_3d.setVisibility(View.GONE);
		}
		holder.iv_thumbnail.setTag(videoObject);
		Bitmap bitmap = new VideoUtils().loadThumbImg(holder.iv_thumbnail, holder.iv_flag_3d, new ImageCallback() {
			@Override
			public void imageLoadedNotify(ImageView imageView, Bitmap imgBitmap, ImageView iv_flag_3d, boolean is3d) {
				if (videoObject != null && videoObject.equals(imageView.getTag())) {
					imageView.setImageBitmap(imgBitmap);
					if (is3d) {
						iv_flag_3d.setVisibility(View.VISIBLE);
					} else {
						iv_flag_3d.setVisibility(View.GONE);
					}
				}
			}
		}, context);
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

	private int mSelectedPosition = -1;

	public void setSelectedPosition(int selectedPosition) {
		mSelectedPosition = selectedPosition;
	}
}
