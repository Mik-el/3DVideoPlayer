package com.estar.video.ui;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.android.takee.video.R;
import com.estar.video.PlayerApplication;
import com.estar.video.VideoFileListActivity;
import com.estar.video.data.BucketInfo;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Constants;

/**
 * 文件夹列表
 * 
 * @author zgl
 * 
 */
public class FolderFragment extends BaseFragment {
	private List<BucketInfo> folders;
	private List<VideoObject> videos;
	private static State state = State.FOLDER;
	/** 当state为MOVIE时，记录当前所处的文件夹id */
	private long bucketId;

	protected ListView lv_list;
	protected GridView gv_list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.folder_list, null);
		lv_list = (ListView) contentView.findViewById(R.id.lv_list);
		View emptyView = contentView.findViewById(R.id.empty_view);
		lv_list.setEmptyView(emptyView);

		gv_list = (GridView) contentView.findViewById(R.id.gv_list);
		gv_list.setEmptyView(emptyView);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private enum State {
		FOLDER, MOVIE
	}

	/** 是否是显示文件夹中视频的状态 */
	public boolean isMovieState() {
		return State.MOVIE.equals(state);
	}

	/** 视频文件夹点击监听 */
	public class FolderItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			lv_list.setVisibility(View.GONE);
			gv_list.setVisibility(View.VISIBLE);

			BucketInfo bucketInfo = folders.get(position);
			videos = DataLoadManager.getBucketVideos(bucketInfo.getBucketId());
			listAdapter = new GridAdapter(getActivity(), mInflater, videos);
			mMovieSelectedIndex = 0;
			((GridAdapter)listAdapter).setSelectedPosition(mMovieSelectedIndex);
			gv_list.setAdapter(listAdapter);
			mItemOnClickListener = new MovieItemOnClickListener(getActivity(), videos);
			gv_list.setOnItemClickListener(mItemOnClickListener);
			gv_list.setOnItemLongClickListener(new MovieItemLongClickListener(getActivity(), videos, mInflater, listAdapter));
			state = State.MOVIE;
			bucketId = bucketInfo.getBucketId();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			if (listAdapter != null) {
				if (!isFromCreateView) {
					updateList();
				} else {
					isFromCreateView = false;
				}
			}
		}
	}

	public void onBackPressed() {
		if (isMovieState()) {
			gv_list.setVisibility(View.GONE);
			lv_list.setVisibility(View.VISIBLE);

			listAdapter = new FolderListAdapter(getActivity(), mInflater, folders);
			((FolderListAdapter)listAdapter).setSelectedPosition(mAirtouchSelectedIndex);
			lv_list.setAdapter(listAdapter);
			lv_list.setOnItemClickListener(new FolderItemClickListener());
			lv_list.setOnItemLongClickListener(new FolderLongClickListener(getActivity(), folders, mInflater, listAdapter));
			((FolderListAdapter) listAdapter).setSelectedPosition(mAirtouchSelectedIndex);
			state = State.FOLDER;
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			if (listAdapter != null) {
				if (!isFromCreateView) {
					updateList();
				} else {
					isFromCreateView = false;
				}
			}
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private boolean isFromCreateView = false;

	@Override
	protected void initList() {
		folders = ((PlayerApplication) getActivity().getApplication()).getDataLoadManager().getFolders();
		listAdapter = new FolderListAdapter(getActivity(), mInflater, folders);
		lv_list.setAdapter(listAdapter);
		lv_list.setOnItemClickListener(new FolderItemClickListener());
		lv_list.setOnItemLongClickListener(new FolderLongClickListener(getActivity(), folders, mInflater, listAdapter));
		isFromCreateView = true;
	}

	@Override
	protected void updateList() {
		if (listAdapter instanceof FolderListAdapter) {
			folders = ((PlayerApplication) getActivity().getApplication()).getDataLoadManager().getFolders();
			((FolderListAdapter) listAdapter).setData(folders);
			listAdapter.notifyDataSetChanged();
		} else if (listAdapter instanceof GridAdapter) {
			videos = DataLoadManager.getBucketVideos(bucketId);
			((GridAdapter) listAdapter).setData(videos);
			listAdapter.notifyDataSetChanged();
		}
	}

	private int mAirtouchSelectedIndex = -1;
	private int mMovieSelectedIndex = 0;
	@Override
	public boolean isSelected() {
		return mAirtouchSelectedIndex != -1;
	}

	@Override
	public void moveCursor(int action) {
		if (listAdapter == null || listAdapter.isEmpty()) {
			return;
		}
		if (isMovieState()) {
			int columnCount = gv_list.getNumColumns();
			int childCount = listAdapter.getCount();
			int lastRowCount = childCount % columnCount;
			int rowCount = childCount / columnCount + (lastRowCount == 0 ? 0 : 1);

			// 现在所在行列号
			int rowNum = mMovieSelectedIndex / columnCount;
			int colNum = mMovieSelectedIndex % columnCount;
			
			switch (action) {
			case Constants.AIR_TOUCH_RIGHT:
				if (rowNum == rowCount - 1 && colNum == lastRowCount - 1) {
					((VideoFileListActivity)getActivity()).navigationToRight();
				} else if (colNum == columnCount - 1) {
					((VideoFileListActivity)getActivity()).navigationToRight();
				} else {
					mMovieSelectedIndex++;
				}
				break;
			case Constants.AIR_TOUCH_LEFT:
				if (colNum > 0) {
					mMovieSelectedIndex--;
				}else{
					((VideoFileListActivity)getActivity()).navigationToLeft();
				}
				break;
			case Constants.AIR_TOUCH_DOWN:
				if (((rowNum + 1) * columnCount + colNum) < childCount) {
					mMovieSelectedIndex = mMovieSelectedIndex + columnCount;
				}
				break;
			case Constants.AIR_TOUCH_UP:
				if (rowNum == 0) {
					mMovieSelectedIndex = -1;
				} else {
					mMovieSelectedIndex = mMovieSelectedIndex - columnCount;
				}
				break;
			case Constants.AIR_TOUCH_CLOSE:
				gv_list.performItemClick(gv_list.getChildAt(mMovieSelectedIndex), mMovieSelectedIndex, gv_list.getItemIdAtPosition(mMovieSelectedIndex));
				break;
			default:
				break;
			}
			if (listAdapter instanceof GridAdapter) {
				gv_list.smoothScrollToPosition(mMovieSelectedIndex);
				((GridAdapter) listAdapter).setSelectedPosition(mMovieSelectedIndex);
				listAdapter.notifyDataSetChanged();
			}
		} else {
			if (mAirtouchSelectedIndex == -1) {
				mAirtouchSelectedIndex = 0;
			} else {
				int childCount = folders.size();
				switch (action) {
				case Constants.AIR_TOUCH_RIGHT:
					((VideoFileListActivity) getActivity()).navigationToRight();
					break;
				case Constants.AIR_TOUCH_LEFT:
					((VideoFileListActivity) getActivity()).navigationToLeft();
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
					lv_list.performItemClick(lv_list.getChildAt(mAirtouchSelectedIndex), mAirtouchSelectedIndex, lv_list.getItemIdAtPosition(mAirtouchSelectedIndex));
					break;
				default:
					break;
				}
			}
			if (listAdapter instanceof FolderListAdapter) {
				lv_list.smoothScrollToPosition(mAirtouchSelectedIndex);
				((FolderListAdapter) listAdapter).setSelectedPosition(mAirtouchSelectedIndex);
				listAdapter.notifyDataSetChanged();
			}
		}
	}
}
