package com.estar.video;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.data.LocalDataBaseOperator;
import com.estar.video.data.MediaStoreOperator;
import com.estar.video.utils.SettingUtils;
import com.estar.video.utils.Utils;
/**
 * 设置界面
 * @author zgl
 *
 */
public class SettingsActivity extends Activity {
	private Context mContext;
	private Switch switch_isContinue, switch_isRemember, switch_subtitle;
	private TextView tv_clear, tv_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		switch_isContinue = (Switch) findViewById(R.id.switch_isContinue);
		switch_isContinue.setChecked(SettingUtils.getIsContinue(this));
		switch_isContinue.setOnCheckedChangeListener(mOnCheckedChangeListener);

		switch_isRemember = (Switch) findViewById(R.id.switch_isRemember);
		switch_isRemember.setChecked(SettingUtils.getIsRemember(this));
		switch_isRemember.setOnCheckedChangeListener(mOnCheckedChangeListener);

		switch_subtitle = (Switch) findViewById(R.id.switch_subtitle);
		switch_subtitle.setChecked(SettingUtils.getShowSubtitle(this));
		switch_subtitle.setOnCheckedChangeListener(mOnCheckedChangeListener);

		tv_clear = (TextView) findViewById(R.id.tv_clear);
		tv_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaStoreOperator.clearBookMark(mContext);
				LocalDataBaseOperator.clearDateLastPlay(mContext);
				Utils.showToast(mContext, mContext.getString(R.string.clear_success));
			}
		});

		tv_refresh = (TextView) findViewById(R.id.tv_refresh);
		tv_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MediaScannerConnection.scanFile(mContext, new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, null, null);
				Utils.showToast(mContext, mContext.getString(R.string.refreshing));
			}
		});
	}

	public OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.switch_isContinue:
				SettingUtils.setIsContinue(mContext, isChecked);
				break;
			case R.id.switch_isRemember:
				SettingUtils.setIsRemember(mContext, isChecked);
				break;
			case R.id.switch_subtitle:
				SettingUtils.setShowSubtitle(mContext, isChecked);
				break;
			default:
				break;
			}
		}
	};
}
