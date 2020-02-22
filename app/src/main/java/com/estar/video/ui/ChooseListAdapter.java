package com.estar.video.ui;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.takee.video.R;

public class ChooseListAdapter extends BaseAdapter {
	private List<ChooseItem> mItems;
	private Activity mActivity;

	final class ViewHolder {
		TextView text;
	}

	public ChooseListAdapter(List<ChooseItem> items, Activity activity) {
		mItems = items;
		mActivity = activity;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mItems.get(position).getStringId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(R.layout.choose_list_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(mItems.get(position).getStringId());
		if (mItems.get(position).isChecked()) {
			holder.text.setTextColor(mActivity.getResources().getColor(R.color.main_color));
		} else {
			holder.text.setTextColor(Color.WHITE);
		}
		return convertView;
	}
	
	public static class ChooseItem {
		private Integer stringId;
		private boolean checked;

		public ChooseItem(Integer stringId, boolean checked) {
			this.stringId = stringId;
			this.checked = checked;
		}

		public Integer getStringId() {
			return stringId;
		}

		public void setStringId(Integer stringId) {
			this.stringId = stringId;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}
	}
}