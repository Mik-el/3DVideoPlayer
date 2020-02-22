package com.estar.video.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.estar.video.HarderPlayerActivity;
import com.android.takee.video.R;
import com.estar.video.data.SettingItem;

/**
 * 封装弹出的菜单栏PopupWindow
 * 
 * @author Se7en
 * 
 */
public class MenuPopupWindow extends PopupWindow {
	private Context mActivity;

	private ListView lv_list;
	private BaseAdapter listAdapter;
	private final List<SettingItem> mSettingItems = new ArrayList<SettingItem>();
	private final UpdateSettingCallback mUpdateSettingCallback;

	public MenuPopupWindow(Activity activity, LayoutInflater inflater, int background ,Drawable divider, UpdateSettingCallback updateSettingCallback) {
		super();
		this.mActivity = activity;
		this.mUpdateSettingCallback = updateSettingCallback;

		setBackgroundDrawable(mActivity.getResources().getDrawable(background));
		setFocusable(true);
		setOutsideTouchable(true);
		
		lv_list = (ListView) inflater.inflate(R.layout.menu_list, null);
		listAdapter = new MenuListAdapter(mSettingItems, inflater);
		lv_list.setAdapter(listAdapter);
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mSettingItems.get(position).click(null);
				dismiss();
			}
		});
		lv_list.setDivider(divider);
		setContentView(lv_list);
	}
	
	@Override
	public void setContentView(View contentView) {
		super.setContentView(contentView);
	}

	public void show(View root_view, int x, int y, int width, int height) {
		updateItems();
		showAtLocation(root_view, Gravity.TOP | Gravity.RIGHT, 0, 0);
		update(x, y, width, LayoutParams.WRAP_CONTENT, true);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (mActivity != null && mActivity instanceof HarderPlayerActivity && ((HarderPlayerActivity) mActivity).getMovieControllerOverlay() != null) {
			((HarderPlayerActivity) mActivity).getMovieControllerOverlay().repostHideRunnable();
		}
	}

	/** 更新item的值 */
	public void updateItems() {
		if (mUpdateSettingCallback != null) {
			mUpdateSettingCallback.updateSettingItems(mSettingItems);
		}
		listAdapter.notifyDataSetChanged();
	}

	/** 更新需要显示的设置项 */
	public interface UpdateSettingCallback {
		/** 返回设置项 */
		public void updateSettingItems(List<SettingItem> settingItems);
	}
}
