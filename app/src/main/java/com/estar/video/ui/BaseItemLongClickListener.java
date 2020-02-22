package com.estar.video.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.takee.video.R;
import com.estar.video.data.SettingItem;
import com.estar.video.utils.Utils;

/**
 * 长按事件基类
 * @author zgl
 *
 */
public abstract class BaseItemLongClickListener implements OnItemLongClickListener {
	protected Context mContext;
	protected LayoutInflater mInflater;
	/** 长按后的弹出框 */
	protected Dialog dialog;

	protected View settingView;
	protected ListView lv_list;
	protected BaseAdapter listAdapter;
	/** 设置项 */
	protected final List<SettingItem> settingItems = new ArrayList<SettingItem>();

	protected BaseItemLongClickListener(Context context, LayoutInflater inflater){
		this.mContext = context;
		this.mInflater = inflater;
		
		initSettingItems();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		settingView = mInflater.inflate(R.layout.menu_list, null);
		settingView.setBackgroundColor(Color.WHITE);
		lv_list = (ListView) settingView.findViewById(R.id.lv_list);

		listAdapter = new MenuListAdapter(settingItems,mInflater);
		lv_list.setAdapter(listAdapter);
		lv_list.setDivider(mContext.getResources().getDrawable(R.drawable.pop_division));
		dialog = Utils.showDialog(mContext, settingView);
		return true;
	}

	/**
	 * 初始化要显示的设置项
	 */
	protected abstract void initSettingItems();
}
