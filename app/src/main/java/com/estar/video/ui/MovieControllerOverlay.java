package com.estar.video.ui;

import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.HarderPlayerActivity;
import com.estar.video.MoviePlayer;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;

/**
 * 播放器控制栏
 * 
 * @author zgl
 * 
 */
public class MovieControllerOverlay {
	private LinearLayout ll_controller, ll_time;
	private TextView tv_play_time, tv_duration;
	private SeekBar seekbar;
	public ImageView iv_play_mode, iv_previous, iv_play, iv_next, iv_capture, iv_screen_size, iv_lock;

	private MoviePlayer mPlayer;
	private HarderPlayerActivity mActivity;
	private View rootView;

	private final Handler handler = new Handler();

	private boolean isLocked;
	private boolean isShowing;

	public final static int HIDE_DELAY = 5000;

	public MovieControllerOverlay(MoviePlayer mPlayer, HarderPlayerActivity mActivity, View rootView) {
		this.mPlayer = mPlayer;
		this.mActivity = mActivity;
		this.rootView = rootView;
		initControllerOverlay();
	}

	/** 初始化控件 */
	private void initControllerOverlay() {
		// ll_controller = (LinearLayout)
		// mActivity.getLayoutInflater().inflate(R.layout.controller_overlay,
		// null);
		ll_controller = (LinearLayout) mActivity.findViewById(R.id.movie_controller);
		ll_controller.setVisibility(View.GONE);

		ll_controller.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				repostHideRunnable();
				return true;
			}
		});

		// LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
		// lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
		// ((RelativeLayout) rootView).addView(ll_controller, lp);
		ll_time = (LinearLayout) mActivity.findViewById(R.id.ll_time);
		tv_play_time = (TextView) mActivity.findViewById(R.id.tv_play_time);
		tv_duration = (TextView) mActivity.findViewById(R.id.tv_duration);
		seekbar = (SeekBar) mActivity.findViewById(R.id.seekbar);
		iv_play_mode = (ImageView) mActivity.findViewById(R.id.iv_play_mode);
		iv_previous = (ImageView) mActivity.findViewById(R.id.iv_previous);
		iv_next = (ImageView) mActivity.findViewById(R.id.iv_next);
		iv_play = (ImageView) mActivity.findViewById(R.id.iv_play);
		iv_lock = (ImageView) mActivity.findViewById(R.id.iv_lock);
		iv_capture = (ImageView) mActivity.findViewById(R.id.iv_capture);
		iv_screen_size = (ImageView) mActivity.findViewById(R.id.iv_screen_size);
		iv_play_mode.setOnClickListener(controllerClickListener);
		iv_previous.setOnClickListener(controllerClickListener);
		iv_next.setOnClickListener(controllerClickListener);
		iv_play.setOnClickListener(controllerClickListener);
		iv_screen_size.setOnClickListener(controllerClickListener);
		iv_lock.setOnClickListener(controllerClickListener);
		iv_lock.setVisibility(View.GONE);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				tv_play_time.setText(Utils.formatDuration(seekbar.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seek) {
				handler.removeCallbacks(hideControllerRunnable);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				handler.postDelayed(hideControllerRunnable, HIDE_DELAY);
				// if(mPlayer.isPlaying()){
				mPlayer.seekTo(seekBar.getProgress());
				// }
			}
		});
		
		if("takee P1".equals(Build.MODEL)){
			mPlayer.movie_view.setIsFullScreen(true);
			iv_screen_size.setImageResource(R.drawable.icn_screen_original);
		}
	}

	private OnClickListener controllerClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			repostHideRunnable();
			switch (v.getId()) {
			case R.id.iv_play_mode:
				mActivity.showPlayModeDialog();
				break;
			case R.id.iv_screen_size:
				mPlayer.movie_view.setIsFullScreen(!mPlayer.movie_view.isFullScreen());
				if (mPlayer.movie_view.isFullScreen()) {
					iv_screen_size.setImageResource(R.drawable.icn_screen_original);
				} else {
					iv_screen_size.setImageResource(R.drawable.icn_screen_full);
				}
				break;
			case R.id.iv_previous:
				mPlayer.playPrevious();
				break;
			case R.id.iv_next:
				mPlayer.playNext();
				break;
			case R.id.iv_play:
				mPlayer.playPause();
				setPlayButton(mPlayer.isPlaying());
				break;
			case R.id.iv_lock:
				if (isLocked) {
					unLockView();
				} else {
					lockView();
				}
				break;
			default:
				break;
			}
		}
	};

	/** 设置播放按钮的图片 */
	public void setPlayButton(boolean isPlaying) {
		if (isPlaying) {
			iv_play.setImageResource(R.drawable.icn_controller_pause);
		} else {
			iv_play.setImageResource(R.drawable.icn_controller_play);
		}
	}

	/** 锁定控件 */
	private void lockView() {
		isLocked = true;
		ll_controller.setVisibility(View.GONE);
		iv_lock.setImageResource(R.drawable.lock);
		isShowing = false;
		mActivity.getActionBar().hide();
	}

	/** 解锁控件 */
	private void unLockView() {
		handler.removeCallbacks(hideControllerRunnable);

		isLocked = false;
		// ll_controller.setVisibility(View.VISIBLE);
		iv_lock.setImageResource(R.drawable.unlock);

		// mActivity.getActionBar().show();
	}

	/** 获取界面是否被锁定 */
	public boolean getIsLocked() {
		return isLocked;
	}

	private Runnable hideControllerRunnable = new Runnable() {
		@Override
		public void run() {
			hideControllerOverlay();
		}
	};

	/** 取消隐藏控制栏 */
	public void cancelHide() {
		handler.removeCallbacks(hideControllerRunnable);
	}

	/** 3s后再隐藏控制栏 */
	public void repostHideRunnable() {
		handler.removeCallbacks(hideControllerRunnable);
		handler.postDelayed(hideControllerRunnable, HIDE_DELAY);
	}

	/** 显示或隐藏控制栏 */
	public void updateControllerOverlay() {
		if (isShowing) {
			hideControllerOverlay();
		} else {
			showControllerOverlay();
		}
	}

	/** 显示控制栏 */
	public void showControllerOverlay() {
		handler.removeCallbacks(hideControllerRunnable);
		handler.postDelayed(hideControllerRunnable, HIDE_DELAY);
		iv_lock.setVisibility(View.VISIBLE);
		if (!getIsLocked()) {
			mActivity.getActionBar().show();
		} else {
			return;
		}

		int showAnimDuration = 100;
		ll_controller.setVisibility(View.VISIBLE);
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
		translateAnimation.setDuration(showAnimDuration);
		animationSet.addAnimation(translateAnimation);

		ll_controller.startAnimation(animationSet);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
		ll_controller.setLayoutParams(lp);
		isShowing = true;
	}

	/** 隐藏控制栏 */
	public void hideControllerOverlay() {
		handler.removeCallbacks(hideControllerRunnable);
		iv_lock.setVisibility(View.GONE);
		if (getIsLocked()) {
			return;
		}
		mActivity.getActionBar().hide();

		int hideAnimDuration = 100;
		ll_controller.setVisibility(View.GONE);
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
		translateAnimation.setDuration(hideAnimDuration);
		animationSet.addAnimation(translateAnimation);

		ll_controller.startAnimation(animationSet);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
		ll_controller.setLayoutParams(lp);
		isShowing = false;

		// 隐藏控制栏的同时隐藏菜单栏
		// if (mActivity.getMenuPopupWindow() != null) {
		// mActivity.getMenuPopupWindow().dismiss();
		// }
	}

	/** 初始化控制栏信息 */
	public void setVideoInfo() {
		seekbar.setMax(mPlayer.getDuration());
		if (!mPlayer.isPlaying()) {
			seekbar.setProgress(0);
		}
		tv_duration.setText(Utils.formatDuration(mPlayer.getDuration()));
		tv_play_time.setText("00:00");
	}

	private VideoObject videoObject;

	/** 开始循环更新播放进度 */
	public void startUpdateCurrentPosition() {
		videoObject = DataLoadManager.getVideoObject(mPlayer.getUri());
//		handler.postDelayed(updatePositionRunnable, 1000);
		handler.post(updatePositionRunnable);
	}

	/** 停止更新播放进度 */
	public void stopUpdateCurrentPosition() {
		handler.removeCallbacks(updatePositionRunnable);
	}

	/** 更新播放进度 */
	private Runnable updatePositionRunnable = new Runnable() {
		@Override
		public void run() {
			if (mPlayer.isPlaying()) {
				seekbar.setProgress(mPlayer.getCurrentPosition());
				tv_play_time.setText(Utils.formatDuration(mPlayer.getCurrentPosition()));

				if (videoObject != null) {
					videoObject.setBookMark(mPlayer.getCurrentPosition());
				}
			}
			if (mPlayer.isPlaying()) {
				handler.postDelayed(updatePositionRunnable, 1000);
			}
		}
	};

	/** 设置上一曲、下一曲是否可用 */
	public void enablePreviousAndNext(boolean enable) {
		// iv_next.setClickable(clickable);
		// iv_previous.setClickable(clickable);
		seekbar.setEnabled(enable);
		iv_next.setEnabled(enable);
		iv_previous.setEnabled(enable);
	}
}
