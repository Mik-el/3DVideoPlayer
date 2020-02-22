package com.estar.video.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.android.takee.video.R;
import com.estar.video.data.SettingItem;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;

/**
 * 视频长按事件处理
 * 
 * @author zgl
 * 
 */
public class MovieItemLongClickListener extends BaseItemLongClickListener {
	/** 视频列表文件 */
	private List<VideoObject> videos;

	public MovieItemLongClickListener(Context context, List<VideoObject> videos, LayoutInflater inflater, BaseAdapter videoListAdapter) {
		super(context, inflater);
		this.videos = videos;
	}

	@Override
	public void initSettingItems() {
		settingItems.clear();

		SettingItem item = new SettingItem();
		item.setDrawableId(R.drawable.icn_setting_clear);
		item.setText(mContext.getString(R.string.delete));
		item.setOnClickListener(new SettingItem.OnClickListener() {
			@Override
			public void onClick(Object arg) {
				final VideoObject videoObject = (VideoObject) arg;
				new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.delete_file, videoObject.getTitle()))
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialoginterface, int id) {
								Utils.deleteFile(mContext,videoObject);
							}
						}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialoginterface, int i) {

							}
						}).create().show();
			}
		});
		settingItems.add(item);

		item = new SettingItem();
		item.setDrawableId(R.drawable.icn_setting_details);
		item.setText(mContext.getString(R.string.media_detail));
		item.setOnClickListener(new SettingItem.OnClickListener() {
			@Override
			public void onClick(Object videoObject) {
				MovieDetailDialog.showDetail(mContext, (VideoObject) videoObject);
			}
		});
		settingItems.add(item);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
		super.onItemLongClick(parent, view, position, id);
		lv_list.setOnItemClickListener(new MunuItemOnClick(videos.get(position)));
		return true;
	}

	/**
	 * 弹出的Menu item点击事件
	 * 
	 * @author zgl
	 * 
	 */
	public class MunuItemOnClick implements OnItemClickListener {
		private VideoObject videoObject;

		public MunuItemOnClick(VideoObject videoObject) {
			this.videoObject = videoObject;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			dialog.dismiss();
			settingItems.get(position).click(videoObject);
		}
	}
}
