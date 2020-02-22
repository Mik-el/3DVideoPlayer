package com.estar.video.ui;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;
import com.estar.video.utils.VideoUtils;
import com.estar.video.utils.VideoUtils.ImageCallback;

/**
 * 历史记录列表的数据适配器
 * @author zgl
 *
 */
public class HistoryExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Map<Integer, List<VideoObject>> historys;
	private LayoutInflater mInflater;
	private Context mContext;
	
	/** 存放group的键值 */
	private final List<Integer> groups = new ArrayList<Integer>();

	public HistoryExpandableListAdapter(Context context,Map<Integer, List<VideoObject>> historys,LayoutInflater inflater) {
		this.mContext = context;
		this.mInflater = inflater;
		updateData(historys);
	}
	
	/** 更新列表数据 */
	public void updateData(Map<Integer, List<VideoObject>> historys){
		this.historys = historys;
		groups.clear();
		Iterator<Integer> iterator = historys.keySet().iterator();
		while (iterator.hasNext()) {
			groups.add(iterator.next());
		}
	}

	// 使用ViewHolder提高listview效率
	final class ViewHolderGroup {
		TextView history_title;
	}

	public final class ViewHolderChild {
		ImageView iv_thumbnail;
		ImageView iv_flag_3d;
		TextView tv_title;
		TextView tv_bookmark;
		/** 为了支持ExpandableListView的长按事件 */
		VideoObject videoObject;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewHolderGroup holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.history_titile_item, null);
			holder = new ViewHolderGroup();
			holder.history_title = (TextView) convertView.findViewById(R.id.history_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderGroup) convertView.getTag();
		}

		if (getGroupId(groupPosition) == HistoryFragment.HISTORY_TODAY) {
			holder.history_title.setText(R.string.today);
		} else if (getGroupId(groupPosition) == HistoryFragment.HISTORY_YESTERDAY) {
			holder.history_title.setText(R.string.yesterday);
		} else if (getGroupId(groupPosition) == HistoryFragment.HISTORY_BEFORE_YESTERDAY) {
			holder.history_title.setText(R.string.the_day_before_yesterday);
		}

		convertView.setClickable(true);
		return convertView;
	}

	public long getGroupId(int groupPosition) {
		return groups.get(groupPosition);
	}

	public Object getGroup(int groupPosition) {
		return historys.get(groups.get(groupPosition));
	}

	public int getGroupCount() {
		return groups.size();

	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolderChild holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.history_list_item, null);
			holder = new ViewHolderChild();
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_bookmark = (TextView) convertView.findViewById(R.id.tv_bookmark);
			holder.iv_thumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
			holder.iv_flag_3d = (ImageView) convertView.findViewById(R.id.iv_flag_3d);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderChild) convertView.getTag();
		}

		final VideoObject videoObject = historys.get(groups.get(groupPosition)).get(childPosition);
		holder.tv_title.setText(videoObject.getTitle());
		holder.tv_bookmark.setText(mContext.getString(R.string.history_time, Utils.formatDurationInHistory(mContext,(int) videoObject.getBookMark())));

		if (videoObject.is3dVideo()) {
			holder.iv_flag_3d.setVisibility(View.VISIBLE);
		} else {
			holder.iv_flag_3d.setVisibility(View.GONE);
		}
		holder.iv_thumbnail.setTag(videoObject);
		Bitmap bitmap = new VideoUtils().loadThumbImg(holder.iv_thumbnail, holder.iv_flag_3d, new ImageCallback() {
			@Override
			public void imageLoadedNotify(ImageView imageView, Bitmap imgBitmap, ImageView iv_flag_3d, boolean is3d) {
				if (videoObject != null && videoObject.equals(imageView.getTag())) {
					imageView.setImageBitmap(imgBitmap);
					if (is3d) {
						iv_flag_3d.setVisibility(View.VISIBLE);
					} else {
						iv_flag_3d.setVisibility(View.GONE);
					}
				}
			}
		}, mContext);
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				holder.iv_thumbnail.setImageBitmap(bitmap);
			}
		} else {
			holder.iv_thumbnail.setImageResource(R.drawable.thumbnail_default);
		}
		holder.videoObject = videoObject;
		
		int position = childPosition;
		for(int i = 0; i < groupPosition; i++){
			position = position + getChildrenCount(i);
		}
		if (mSelectedPosition == position && VideoFileListActivity.isAirtouchEnable()) {
			convertView.setBackgroundResource(R.drawable.airtouch_focused);
		} else {
			convertView.setBackgroundResource(R.drawable.airtouch_not_focused);
		}
		
		return convertView;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return historys.get(groups.get(groupPosition)).get(childPosition).getId();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return historys.get(groups.get(groupPosition)).get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return historys.get(groups.get(groupPosition)).size();
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private int mSelectedPosition = -1;

	public void setSelectedPosition(int selectedPosition) {
		mSelectedPosition = selectedPosition;
	}
}