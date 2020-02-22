package com.estar.video.ui;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.takee.video.R;
import com.estar.video.PlayerApplication;
import com.estar.video.VideoFileListActivity;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Constants;

/**
 * 播放历史列表
 * 
 * @author zgl
 * 
 */
public class HistoryFragment extends BaseFragment {
	public final static int HISTORY_TODAY = 0;
	public final static int HISTORY_YESTERDAY = 1;
	public final static int HISTORY_BEFORE_YESTERDAY = 2;

	/** 播放历史列表 */
	private Map<Integer, List<VideoObject>> historys;

	private ExpandableListView history_lists;
	/** ExpandableListView数据适配器 */
	private HistoryExpandableListAdapter dataAdapter;

	/** 是否是执行onCreateView执行onResume */
	private boolean isFromCreateView = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.history_list, null);
		history_lists = (ExpandableListView) contentView.findViewById(R.id.history_lists);
		View emptyView = contentView.findViewById(R.id.empty_view);
		history_lists.setEmptyView(emptyView);
		// return super.onCreateView(inflater, container, savedInstanceState);

		this.mInflater = inflater;
		this.container = container;
		if (DataLoadManager.videos.size() > 0) {
			initList();
		}
		// initList();
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case UPDATE_ACTIVITY:
					if (dataAdapter == null) {
						initList();
					} else {
						updateList();
					}
					break;
				default:
					break;
				}
			}
		};

		return contentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			if (dataAdapter != null) {
				if (!isFromCreateView) {
					updateList();
				} else {
					isFromCreateView = false;
				}
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			if (dataAdapter != null) {
				if (!isFromCreateView) {
					updateList();
				} else {
					isFromCreateView = false;
				}
			}
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	protected void initList() {
		historys = ((PlayerApplication) getActivity().getApplication()).getDataLoadManager().getHistory();

		dataAdapter = new HistoryExpandableListAdapter(getActivity(), historys, mInflater);
		history_lists.setAdapter(dataAdapter);
		history_lists.setGroupIndicator(null);
		history_lists.setDivider(null);
		int count = history_lists.getCount();
		for (int i = 0; i < count; i++) {
			history_lists.expandGroup(i);
		}
		history_lists.setOnChildClickListener(new HistoryItemOnClickListener(this));

		history_lists.setOnItemLongClickListener(new HistoryItemLongClickListener(getActivity(), historys, mInflater, dataAdapter));
		isFromCreateView = true;
	}

	@Override
	protected void updateList() {
		historys = ((PlayerApplication) getActivity().getApplication()).getDataLoadManager().getHistory();
		dataAdapter.updateData(historys);
		dataAdapter.notifyDataSetChanged();
		int count = historys.size();
		for (int i = 0; i < count; i++) {
			history_lists.expandGroup(i);
		}
	}

	private int mAirtouchSelectedIndex = -1;

	@Override
	public boolean isSelected() {
		return mAirtouchSelectedIndex != -1;
	}

	@Override
	public void moveCursor(int action) {
		if (history_lists == null || history_lists.getChildCount() <= 0) {
			return;
		}
		int childCount = history_lists.getChildCount() - dataAdapter.getGroupCount();
		if (mAirtouchSelectedIndex == -1) {
			mAirtouchSelectedIndex = 0;
		} else {
			switch (action) {
			case Constants.AIR_TOUCH_RIGHT:
				((VideoFileListActivity)getActivity()).navigationToRight();
				break;
			case Constants.AIR_TOUCH_LEFT:
				((VideoFileListActivity)getActivity()).navigationToLeft();
				break;
			case Constants.AIR_TOUCH_DOWN:
				if (mAirtouchSelectedIndex < childCount - 1) {
					mAirtouchSelectedIndex++;
				}
				break;
			case Constants.AIR_TOUCH_UP:
				mAirtouchSelectedIndex--;
				break;
			case Constants.AIR_TOUCH_CLOSE:
				int groupPosition = 0;
				if (mAirtouchSelectedIndex < historys.get(0).size()) {
					groupPosition = 0;
				} else if (mAirtouchSelectedIndex < (historys.get(0).size() + historys.get(1).size())) {
					groupPosition = 1;
				} else {
					groupPosition = 2;
				}
				int childPosition = mAirtouchSelectedIndex;
				for(int i = 0; i < historys.size();i++){
					childPosition = childPosition - historys.get(i).size();
				}
				history_lists.performItemClick(
						history_lists.getAdapter().getView(mAirtouchSelectedIndex + groupPosition + 1, null, null),
						mAirtouchSelectedIndex + groupPosition + 1,
						history_lists.getAdapter().getItemId(mAirtouchSelectedIndex + groupPosition + 1));
				break;
			default:
				break;
			}
		}
		history_lists.smoothScrollToPosition(mAirtouchSelectedIndex);
		dataAdapter.setSelectedPosition(mAirtouchSelectedIndex);
		dataAdapter.notifyDataSetChanged();
	}
}
