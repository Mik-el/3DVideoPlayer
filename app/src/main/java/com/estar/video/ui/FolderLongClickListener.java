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
import com.estar.video.data.BucketInfo;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.SettingItem;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;

/**
 * 视频长按事件处理
 * 
 * @author zgl
 * 
 */
public class FolderLongClickListener extends BaseItemLongClickListener{
	/** 视频列表文件 */
	private List<BucketInfo> folders;

	public FolderLongClickListener(Context context, List<BucketInfo> folders, LayoutInflater inflater, BaseAdapter videoListAdapter) {
		super(context, inflater);
		this.folders = folders;
	}
	
	/**
	 * 初始化要显示的设置项
	 */
	public void initSettingItems() {
		settingItems.clear();

		SettingItem item = new SettingItem();
		item.setDrawableId(R.drawable.icn_setting_clear);
		item.setText(mContext.getString(R.string.delete));
		item.setOnClickListener(new SettingItem.OnClickListener() {
			@Override
			public void onClick(Object arg) {
				final BucketInfo bucketInfo = (BucketInfo) arg;
				new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.delete_folder, bucketInfo.getBucket_display_name()))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialoginterface, int id) {
						List<VideoObject> videos = DataLoadManager.getBucketVideos(bucketInfo.getBucketId());
						Utils.deleteFolder(mContext, videos);
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
			public void onClick(Object arg) {
				final BucketInfo bucketInfo = (BucketInfo) arg;
				BucketDetailDialog.showDetail(mContext, bucketInfo);
			}
		});
		settingItems.add(item);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
		super.onItemLongClick(parent, view, position, id);
		lv_list.setOnItemClickListener(new MunuItemOnClick(folders.get(position)));
		return true;
	}
	
	/**
	 * 弹出的Menu item点击事件
	 * 
	 * @author zgl
	 * 
	 */
	public class MunuItemOnClick implements OnItemClickListener {
		private BucketInfo bucketInfo;

		public MunuItemOnClick(BucketInfo bucketInfo) {
			this.bucketInfo = bucketInfo;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			dialog.dismiss();
			settingItems.get(position).click(bucketInfo);
		}
	}
}
