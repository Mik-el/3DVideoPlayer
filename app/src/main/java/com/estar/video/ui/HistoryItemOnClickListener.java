package com.estar.video.ui;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.estar.video.HarderPlayerActivity;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;

/**
 * 历史记录视频的点击事件
 * 
 * @author zgl
 * 
 */
public class HistoryItemOnClickListener implements OnChildClickListener {
	private final Fragment mFragment;

	public HistoryItemOnClickListener(Fragment mFragment) {
		this.mFragment = mFragment;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		VideoObject videoObject = DataLoadManager.getVideoById(id);
		if (videoObject != null) {
			Intent intent = new Intent();
			intent.setClass(mFragment.getActivity(), HarderPlayerActivity.class);
			intent.setData(videoObject.getContentUri());
			mFragment.startActivity(intent);
		}
		return true;
	}

}
