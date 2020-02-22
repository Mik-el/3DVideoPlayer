package com.estar.video.ui;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.data.SettingItem;

/**
 * 弹出菜单栏选项适配器
 * @author zgl
 *
 */
public class MenuListAdapter extends BaseAdapter {
	/** 设置项 */
	private final List<SettingItem> settingItems;
	private final LayoutInflater mInflater;
	public MenuListAdapter(List<SettingItem> settingItems,LayoutInflater inflater){
		this.settingItems = settingItems;
		this.mInflater = inflater;
	}
	
	final class ViewHolder {
		ImageView iv_menu_item;
		TextView tv_menu_item;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menu_list_item, null);
			holder = new ViewHolder();
			holder.iv_menu_item = (ImageView) convertView.findViewById(R.id.iv_menu_item);
			holder.tv_menu_item = (TextView) convertView.findViewById(R.id.tv_menu_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SettingItem item = settingItems.get(position);
		int drawableId = item.getDrawableId();
		String text = item.getText();
		holder.iv_menu_item.setImageResource(drawableId);
		holder.tv_menu_item.setText(text);
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return settingItems.get(position).getDrawableId();
	}

	@Override
	public Object getItem(int position) {
		return settingItems.get(position);
	}

	@Override
	public int getCount() {
		return settingItems.size();
	}
}
