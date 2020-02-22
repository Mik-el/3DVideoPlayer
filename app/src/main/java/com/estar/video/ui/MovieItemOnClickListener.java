package com.estar.video.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.estar.video.HarderPlayerActivity;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Constants;

/**
 * 视频文件点击监听
 * 
 * @author zgl
 * 
 */
public class MovieItemOnClickListener implements OnItemClickListener {
	private final Context context;
	private List<VideoObject> videos;

	public MovieItemOnClickListener(Context context, List<VideoObject> videos) {
		this.context = context;
		this.videos = videos;
	}

	public void updateData(List<VideoObject> videos) {
		this.videos = videos;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VideoObject videoObject = DataLoadManager.getVideoById(id);
		Intent intent = new Intent();

		intent.setData(videoObject.getContentUri());
		intent.setClass(context, HarderPlayerActivity.class);

		long[] playList = new long[videos.size()];
		int i = 0;
		for (VideoObject video : videos) {
			playList[i++] = video.getId();
		}
		intent.putExtra(Constants.LIST_PARAMETER, playList);

		context.startActivity(intent);
	}
}
