package com.estar.video.ui;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.takee.video.R;
import com.estar.video.VideoFileListActivity;
import com.estar.video.utils.Utils;

/**
 * 播放url界面
 * @author zgl
 *
 */
public class PlayerUrlFragment extends Fragment implements SupportAirtouch{
	private EditText et_url;
	private Button btn_play;
	public int selectIndex = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.play_url, null);
		et_url = (EditText) contentView.findViewById(R.id.et_url);
		btn_play = (Button) contentView.findViewById(R.id.btn_play);
		btn_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = et_url.getText().toString().trim();
				if (!"".equals(url) && url.startsWith("http")) {
					Utils.playUrl(getActivity(), Uri.parse(url));
				} else {
					Utils.showToast(getActivity(), getActivity().getString(R.string.bad_url));
				}
			}
		});
		return contentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			setTextByClipboard();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			setTextByClipboard();
			((VideoFileListActivity)getActivity()).fragment = this;
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void setTextByClipboard() {
		ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = clipboard.getPrimaryClip();
		if(clipData != null){
			for (int i = 0; i < clipData.getItemCount(); i++) {
				Item item = clipData.getItemAt(i);
				
				CharSequence cs = item.getText();
				if (cs != null && cs instanceof String) {
					String text = (String) cs;
					if(text.startsWith("http")){
						// Utils.showToast(context, text)
					}
				}
			}
		}
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void moveCursor(int action) {
		
	}
}
