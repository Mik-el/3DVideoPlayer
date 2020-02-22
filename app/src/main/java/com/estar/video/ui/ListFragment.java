package com.estar.video.ui;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Constants;

/**
 * 视频文件列表
 * 
 * @author zgl
 * 
 */
public class ListFragment extends BaseFragment {
	private List<VideoObject> videos;

	protected GridView gv_list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.video_list, null);
		gv_list = (GridView) contentView.findViewById(R.id.gv_list);
		View emptyView = contentView.findViewById(R.id.empty_view);
		gv_list.setEmptyView(emptyView);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			if (listAdapter != null) {
				updateList();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			if (listAdapter != null) {
				updateList();
			}
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	protected void initList() {
		videos = DataLoadManager.videos;
		listAdapter = new GridAdapter(getActivity(), mInflater, videos);
		gv_list.setAdapter(listAdapter);
		mItemOnClickListener = new MovieItemOnClickListener(getActivity(), videos);
		gv_list.setOnItemClickListener(mItemOnClickListener);
		gv_list.setOnItemLongClickListener(new MovieItemLongClickListener(getActivity(), videos, mInflater, listAdapter));
	}

	@Override
	protected void updateList() {
		videos = DataLoadManager.videos;
		((GridAdapter) listAdapter).setData(videos);
		listAdapter.notifyDataSetChanged();
	}

	private int mAirtouchSelectedIndex = -1;

	@Override
	public boolean isSelected() {
		return mAirtouchSelectedIndex != -1;
	}

	@Override
	public void moveCursor(int action) {
		if (gv_list == null || gv_list.getChildCount() <= 0) {
			return;
		}
		if (mAirtouchSelectedIndex == -1) {
			mAirtouchSelectedIndex = 0;
		} else {
			int columnCount = gv_list.getNumColumns();
			int childCount = listAdapter.getCount();
			int lastRowCount = childCount % columnCount;
			int rowCount = childCount / columnCount + (lastRowCount == 0 ? 0 : 1);

			// 现在所在行列号
			int rowNum = mAirtouchSelectedIndex / columnCount;
			int colNum = mAirtouchSelectedIndex % columnCount;

			switch (action) {
			case Constants.AIR_TOUCH_RIGHT:
				if (rowNum == rowCount - 1 && colNum == lastRowCount - 1) {
					((VideoFileListActivity)getActivity()).navigationToRight();
				} else if (colNum == columnCount - 1) {
					((VideoFileListActivity)getActivity()).navigationToRight();
				} else {
					mAirtouchSelectedIndex++;
				}
				break;
			case Constants.AIR_TOUCH_LEFT:
				if (colNum > 0) {
					mAirtouchSelectedIndex--;
				}else{
					((VideoFileListActivity)getActivity()).navigationToLeft();
				}
				break;
			case Constants.AIR_TOUCH_DOWN:
				if (((rowNum + 1) * columnCount + colNum) < childCount) {
					mAirtouchSelectedIndex = mAirtouchSelectedIndex + columnCount;
				}
				break;
			case Constants.AIR_TOUCH_UP:
				if (rowNum == 0) {
					mAirtouchSelectedIndex = -1;
				} else {
					mAirtouchSelectedIndex = mAirtouchSelectedIndex - columnCount;
				}
				break;
			case Constants.AIR_TOUCH_CLOSE:
				gv_list.performItemClick(gv_list.getChildAt(mAirtouchSelectedIndex), mAirtouchSelectedIndex, gv_list.getItemIdAtPosition(mAirtouchSelectedIndex));
				break;
			default:
				break;
			}
		}
		gv_list.smoothScrollToPosition(mAirtouchSelectedIndex);
		((GridAdapter) listAdapter).setSelectedPosition(mAirtouchSelectedIndex);
		listAdapter.notifyDataSetChanged();
	}
}
