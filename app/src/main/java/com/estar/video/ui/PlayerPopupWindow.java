package com.estar.video.ui;

import android.app.Activity;
import android.view.View;
import android.widget.PopupWindow;

import com.estar.video.HarderPlayerActivity;

/** 播放界面的弹出框 */
public class PlayerPopupWindow extends PopupWindow {
	protected Activity mActivity;
	public PlayerPopupWindow(Activity activity){
		super(activity);
		mActivity = activity;
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (mActivity != null && mActivity instanceof HarderPlayerActivity && ((HarderPlayerActivity) mActivity).getMovieControllerOverlay() != null) {
			((HarderPlayerActivity) mActivity).getMovieControllerOverlay().repostHideRunnable();
			((HarderPlayerActivity) mActivity).getMovieControllerOverlay().iv_lock.setVisibility(View.VISIBLE);
		}
	}
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		if (mActivity != null && mActivity instanceof HarderPlayerActivity && ((HarderPlayerActivity) mActivity).getMovieControllerOverlay() != null) {
			((HarderPlayerActivity) mActivity).getMovieControllerOverlay().cancelHide();
			((HarderPlayerActivity) mActivity).getMovieControllerOverlay().iv_lock.setVisibility(View.GONE);
		}
	}
}
