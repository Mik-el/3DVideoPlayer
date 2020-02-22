package com.estar.ulifang;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.ui.SupportAirtouch;
import com.estar.video.utils.Constants;
import com.estar.video.utils.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 云立方视频界面
 * @author zgl
 *
 */
public class UlifangMovieListFragment extends Fragment implements SupportAirtouch {
	private ProgressBar loading_bar;
	private com.estar.video.ui.GridViewWithHeaderAndFooter gv_list;
	private LayoutInflater mInflater;
	private Context mContext;
	private final List<UlifangMovieObject> lists = new ArrayList<UlifangMovieObject>();
	private UlifangListAdapter mUlifangListAdapter;

	public final static int LOADING_FINISH = 0;
	// public final static int LOADING_FAILURE = 1;
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOADING_FINISH:
				loading_bar.setVisibility(View.GONE);
				mUlifangListAdapter = new UlifangListAdapter(mInflater, lists);
				gv_list.setAdapter(mUlifangListAdapter);
				break;
			// case LOADING_FAILURE:
			// Utils.showToast(mContext, "失败");
			// break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
		mInflater = inflater;
		mContext = getActivity();
		View contentView = inflater.inflate(R.layout.video_list, null);
		gv_list = (com.estar.video.ui.GridViewWithHeaderAndFooter) contentView.findViewById(R.id.gv_list);
		loading_bar = (ProgressBar) contentView.findViewById(R.id.loading_bar);

		View empty_view = contentView.findViewById(R.id.empty_view);
		gv_list.setEmptyView(empty_view);
		gv_list.setOnItemClickListener(new UlifangItemClickListener(lists, mContext, loading_bar));

		View footerView = inflater.inflate(R.layout.grid_footer, null);
		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.RunApp(mContext, "com.estarCool", "com.estarCool.HomeActivity");
			}
		});
		gv_list.addFooterView(footerView);

		File movieFile = new File(Constants.ULIFANT_CACHE_LIST_FILE);
		if (movieFile.exists() && movieFile.canRead()) {
			try {
				lists.clear();
				Utils.readMoviesFromFile(lists, movieFile);
				mHandler.sendEmptyMessage(LOADING_FINISH);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			HttpClient.getVideoList(getActivity(), callBack);
		}
		return contentView;
	}

	/** 处理加载列表 */
	private RequestCallBack<String> callBack = new RequestCallBack<String>() {
		/**
		 * 加载成功
		 */
		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			loading_bar.setVisibility(View.GONE);
			String json = responseInfo.result;
//			Utils.showToast(mContext, "success");

			Utils.readMoviesFromJson(lists, json);
			Utils.saveListToFile(lists);
			mHandler.sendEmptyMessage(LOADING_FINISH);
		}

		/**
		 * 加载失败
		 */
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			loading_bar.setVisibility(View.GONE);
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			loading_bar.setVisibility(View.GONE);
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			super.onLoading(total, current, isUploading);
			loading_bar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onStart() {
			super.onStart();
			loading_bar.setVisibility(View.VISIBLE);
		}
	};

	/** 更新列表 */
	public void updateList() {
		loading_bar.setVisibility(View.VISIBLE);
		HttpClient.getVideoList(getActivity(), callBack);
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			((VideoFileListActivity)getActivity()).fragment = this;
		}
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
			int childCount = mUlifangListAdapter.getCount();
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
		mUlifangListAdapter.setSelectedPosition(mAirtouchSelectedIndex);
		mUlifangListAdapter.notifyDataSetChanged();
	}
}
