package com.estar.video;

import java.io.File;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.MediaPlayer.TrackInfo;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.Holography.Holography;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.LocalDataBaseOperator;
import com.estar.video.data.MediaStoreOperator;
import com.estar.video.data.VideoObject;
import com.estar.video.ui.MovieGLSurfaceView;
import com.estar.video.utils.CodeChangeUtils;
import com.estar.video.utils.Constants;
import com.estar.video.utils.SettingUtils;
import com.estar.video.utils.Utils;
import com.estar.video.utils.VideoUtils;

/**
 * 视频播放器
 * 
 * @author zgl
 * 
 */
public class MoviePlayer {
	/** 正在播放的视频 */
	// private VideoObject videoObject;

	private MediaPlayer mMediaPlayer;

	/** 解决快速点击两次锁屏仍在播放的bug */
	private boolean mResumed = false;
	private boolean mHasFocus = true;

	/** 播放器是否处于暂停状态，主要是解决暂停状态执行onWindowFoucus后会播放的问题 */
	private boolean mPaused = false;

	private HarderPlayerActivity movieActivity;
	public final MovieGLSurfaceView movie_view;
	private final ActionBar mActionBar;
	private Context mContext;
	/** 正在播放的视频文件uri */
	private Uri mUri;

	/** 获取当前正在播放的视频 */
	public Uri getUri() {
		return mUri;
	}

	/** 字幕文件 */
	private TextView tvSubtitle = null;
	/** 判断是否存在字幕 */
	private boolean IsExitSubtitle = false;

	/** 快进快退跨度为10s */
	public final static int SPEED_SPAN = 10000;

	/** 播放下一个视频 */
	public final static int PLAY_NEXT_FLAG = 0;
	/** 播放上一个视频 */
	public final static int PLAY_PREVIOUS_FLAG = 1;
	/** 播放暂停 */
	public final static int PLAY_OR_PAUSE_FLAG = 2;
	/** 增加音量 */
	public final static int ADD_VOLUME_FLAG = 3;
	/** 减少音量 */
	public final static int DEL_VOLUME_FLAG = 4;
	/** 快进 */
	public final static int FAST_FORWARD_FLAG = 5;
	/** 快退 */
	public final static int REWIND_FLAG = 6;
	/** 2D/3D模式切换 */
	public final static int CHANGE_SHOW_MODE_FLAG = 7;
	/** 空中触控返回键 */
	public final static int ON_BACK_ACTIVITY_FLAG = 8;
	/** GLSurfaceView初始化完毕 */
	public final static int ON_SURFACE_CREATED = 9;

	/** 是否正处于按频率自动刷新（使暂停时有人眼追踪） */
	public final static int AUTO_REFRESHING = 10;

	/** 2D/3D检测完毕 */
	public final static int CHECK_FINISHED = 11;

	/** 播放出现错误 */
	public final static int ERROR = 12;

	/** 截图保存成功 */
	public final static int SCREEN_CAPTURE_SUCCESS = 13;

	/** Holography初始化完毕 */
	public final static int ON_HOLOGRAPHY_INIT = 14;

	/** 自动刷新的频率 */
	public final static int AUTO_REFRESHING_FREQUENCY = 50;

	/** 播放界面是否已经准备完毕(准备完毕才可以有触摸事件) */
	public boolean isPlayerPrepared = false;

	public enum PlayMode {
		MODE_2D, MODE_3D, MODE_VR, MODE_LR
	}

	private PlayMode mPlayMode = PlayMode.MODE_3D;

	/** 获取当前播放模式 */
	public PlayMode getPlayMode() {
		return mPlayMode;
	}

	/** 设置当前播放模式 */
	public void setPlayMode(PlayMode playMode) {
		mPlayMode = playMode;
	}

	public MoviePlayer(View rootView, final HarderPlayerActivity movieActivity, Uri videoUri, Bundle savedInstance, boolean canReplay) {
		Utils.showLogDebug("new MoviePlayer,Uri=" + videoUri.toString());
		mContext = movieActivity.getApplicationContext();
		this.movieActivity = movieActivity;
		movie_view = (MovieGLSurfaceView) rootView.findViewById(R.id.movie_view);
		movie_view.setTag(controlHanlder);
		movie_view.requestFocus();// 获取焦点
		movie_view.setFocusableInTouchMode(true);// 设置为可触控

		mActionBar = movieActivity.getActionBar();
		mUri = videoUri;

		// When the user touches the screen or uses some hard key, the framework
		// will change system ui visibility from invisible to visible. We show
		// the media control at this point.
		movie_view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			public void onSystemUiVisibilityChange(int visibility) {
				if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
					// mController.show();
				}
			}
		});

		// if (Utils.isInList(mUri)) {
		// videoObject = DataLoadManager.getVideoObject(mUri);
		// }

		initSubtitle();
	}

	/** 初始化字幕控件 */
	private void initSubtitle() {
		// tvSubtitle = new TextView(movieActivity);
		// tvSubtitle.setTextColor(Color.RED);
		// tvSubtitle.setGravity(Gravity.CENTER);
		// LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
		// lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
		// lp.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
		// ((RelativeLayout)
		// movieActivity.findViewById(R.id.root)).addView(tvSubtitle, lp);
		tvSubtitle = (TextView) movieActivity.findViewById(R.id.tv_subtitle);
	}

	/** 控制播放的消息类 */
	public Handler controlHanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FAST_FORWARD_FLAG:
				fastForward();
				break;
			case REWIND_FLAG:
				rewind();
				break;
			case PLAY_OR_PAUSE_FLAG:
				playPause();
				break;
			case ADD_VOLUME_FLAG:
				addVolume();
				break;
			case DEL_VOLUME_FLAG:
				delVolume();
				break;
			case CHANGE_SHOW_MODE_FLAG:
				if (PlayMode.MODE_3D.equals(mPlayMode)) {
					changePlayMode(PlayMode.MODE_2D);
				} else if (PlayMode.MODE_2D.equals(mPlayMode)) {
					if (VideoObject.isVideoType3d(movie_view.getVideoType())) {
						changePlayMode(PlayMode.MODE_LR);
					} else {
						changePlayMode(PlayMode.MODE_3D);
					}
				} else if (PlayMode.MODE_LR.equals(mPlayMode)) {
					changePlayMode(PlayMode.MODE_LR);
				} else if (PlayMode.MODE_VR.equals(mPlayMode)) {
					changePlayMode(PlayMode.MODE_VR);
				}
				break;
			case ON_BACK_ACTIVITY_FLAG:
				movieActivity.onBackPressed();
				break;
			case ON_SURFACE_CREATED:
				new Thread() {
					@Override
					public void run() {
						Holography.HolographyInit();
						sendEmptyMessage(ON_HOLOGRAPHY_INIT);
					}
				}.start();
				break;
			case ON_HOLOGRAPHY_INIT:
				playVideo(mUri);
				break;
			case AUTO_REFRESHING:
				if (movie_view != null) {
					movie_view.requestRender();
				}
				if (!isPlaying()) {
					sendEmptyMessageDelayed(AUTO_REFRESHING, AUTO_REFRESHING_FREQUENCY);
				}
				break;
			case CHECK_FINISHED:
				if (!movieActivity.isDestroyed()) {
					startPlay();
				}
				break;
			case ERROR:
				// boolean fatalError = true;
				// String errorText = "未知错误";
				// // msg.arg1 = what;
				// // msg.arg2 = extra;
				//
				// switch (msg.arg1) {
				// case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				// errorText = "未知错误";
				// break;
				// case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				// errorText = "media server died.";
				// break;
				// case -38:
				// fatalError = false;
				// errorText = "请操作慢一点";
				// break;
				// default:
				// break;
				// }
				// if(fatalError){
				if (msg.arg1 != -38) {
					new AlertDialog.Builder(movieActivity).setTitle(R.string.prompt).setMessage(android.R.string.VideoView_error_text_unknown)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									movieActivity.finish();
								}
							}).setCancelable(false).create().show();
				}

				// new
				// AlertDialog.Builder(movieActivity).setTitle(R.string.prompt).setMessage(errorText)
				// .setPositiveButton(android.R.string.ok, new
				// DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int whichButton)
				// {
				// movieActivity.finish();
				// }
				// }).setCancelable(false).create().show();
				// }
				break;
			case SCREEN_CAPTURE_SUCCESS:
				String filePath = String.valueOf(msg.obj);
				Utils.showToast(movieActivity, movieActivity.getString(R.string.screen_capture_success, filePath));
				break;
			default:
				break;
			}
		};
	};

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	/** 空中触控的消息处理类 */
	public Handler airtouchGestureHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (movieActivity.getMovieControllerOverlay().getIsLocked())
				return;
			switch (msg.what) {
			case 1:
				controlHanlder.sendEmptyMessage(FAST_FORWARD_FLAG);
				break;
			case 2:
				controlHanlder.sendEmptyMessage(REWIND_FLAG);
				break;
			case 3:
				controlHanlder.sendEmptyMessage(ADD_VOLUME_FLAG);
				break;
			case 4:
				controlHanlder.sendEmptyMessage(DEL_VOLUME_FLAG);
				break;
			case 5:
				controlHanlder.sendEmptyMessage(PLAY_OR_PAUSE_FLAG);
				break;
			case 8:
				controlHanlder.sendEmptyMessage(CHANGE_SHOW_MODE_FLAG);
				break;
			case 9:
				controlHanlder.sendEmptyMessage(ON_BACK_ACTIVITY_FLAG);
				break;
			default:
				break;
			}
		};
	};

	/** 初始化MediaPlayer */
	private void initMediaPlayer() {
		Utils.showLogDebug("initMediaPlayer");
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(mPreparedListener);
		mMediaPlayer.setOnCompletionListener(mVideoCompleteListener);
		mMediaPlayer.setOnErrorListener(errorListener);
		mMediaPlayer.setSurface(movie_view.surface);
	}

	/** 根据Uri播放视频 */
	private synchronized void playVideo(Uri uri) {
		Utils.showLogDebug("MoviePlayer playVideo,Uri=" + uri);
		isPlayerPrepared = false;
		movieActivity.getMovieControllerOverlay().stopUpdateCurrentPosition();
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
		} else {
			initMediaPlayer();
		}
		try {
			Utils.showLogDebug("MoviePlayer setDataSource");
			mMediaPlayer.setDataSource(mContext, uri);
			Utils.showLogDebug("MoviePlayer prepareAsync");
			mMediaPlayer.prepareAsync();
			movieActivity.showLoadingBar();
		} catch (Exception e) {
			Utils.showLogError("MoviePlayer error...");
			e.printStackTrace();
			controlHanlder.sendEmptyMessage(ERROR);
		}
	}

	/** MediaPlayer解析视频完成的回调 */
	private OnPreparedListener mPreparedListener = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			Utils.showLogDebug("MediaPlayer onPrepared");
			VideoObject videoObject = DataLoadManager.getVideoObject(mUri);
			isPlayerPrepared = true;
			if (Utils.isMediaUri(mUri) && videoObject.getVideoType() == VideoObject.VIDEO_TYPE_UN_CHECK) {
				VideoUtils.naviveCheckVideoWithHandler(movieActivity, videoObject, controlHanlder);
			} else {
				startPlay();
			}
		}
	};

	/** 设置屏幕缩放比例等参数 */
	public void setFixedSizeByConvergence() {
		int width = mMediaPlayer.getVideoWidth();
		int height = mMediaPlayer.getVideoHeight();
		if (Utils.isMediaUri(mUri)) {
			movie_view.setFixedSize(width, height, DataLoadManager.getVideoObject(mUri).getConvergence());
		} else {
			movie_view.setFixedSize(width, height, movie_view.getConvergence());
		}
	}

	/** 2D/3D检测完毕，开始执行播放 */
	private void startPlay() {
		Utils.showLogDebug("startPlay()");

		final VideoObject videoObject = DataLoadManager.getVideoObject(mUri);
		if (Utils.isMediaUri(mUri)) {
			// 更新该视频上次播放的日期
			LocalDataBaseOperator.updateDateLastPlay(mContext, videoObject, System.currentTimeMillis());
		}
		tvSubtitle.setVisibility(View.GONE);

		movieActivity.hideLoadingBar();
		if (Utils.isMediaUri(mUri)) {
			Utils.showLogDebug("video in list");
			movie_view.setVideoType(videoObject.getVideoType());

			if (VideoObject.CONVERGENCE_UN_CHECK == videoObject.getConvergence()) {
				// 根据视频分辨率判断是全宽视频还是半宽视频
				switch (videoObject.getVideoType()) {
				case VideoObject.VIDEO_TYPE_3D_LR:
					if ((mMediaPlayer.getVideoWidth() / 2) / mMediaPlayer.getVideoHeight() > Constants.screenWidth / Constants.screenHeight) {
						// 全宽视频
						LocalDataBaseOperator.updateConvergence(mContext, videoObject, VideoObject.CONVERGENCE_NONE);
					} else {
						// 半宽视频
						LocalDataBaseOperator.updateConvergence(mContext, videoObject, VideoObject.CONVERGENCE_HALF);
					}
					break;
				case VideoObject.VIDEO_TYPE_3D_UD:
					if (mMediaPlayer.getVideoWidth() / (mMediaPlayer.getVideoHeight() / 2) >= Constants.screenWidth / Constants.screenHeight) {
						// 半高视频
						LocalDataBaseOperator.updateConvergence(mContext, videoObject, VideoObject.CONVERGENCE_HALF);
					} else {
						// 全高视频
						LocalDataBaseOperator.updateConvergence(mContext, videoObject, VideoObject.CONVERGENCE_NONE);
					}
					break;
				default:
					break;
				}
			}

			// 如果播放2D视频前，播放模式为LR或者VR，则切换成3D模式
			if (!videoObject.is3dVideo() && (PlayMode.MODE_LR.equals(mPlayMode) || PlayMode.MODE_VR.equals(mPlayMode))) {
				mPlayMode = PlayMode.MODE_3D;
				movieActivity.getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_3d);
			}
			if (SettingUtils.getShowSubtitle(mContext)) {
				File subtitleFile = new File(videoObject.getPath().substring(0, videoObject.getPath().lastIndexOf(".")) + ".srt");
				if (subtitleFile.exists()) {
					tvSubtitle.setVisibility(View.VISIBLE);
					showSubTitle(videoObject.getPath().substring(0, videoObject.getPath().lastIndexOf(".")) + ".srt");
				}
			}
			if (SettingUtils.getIsRemember(mContext) && videoObject.getBookMark() > 30000 && videoObject.getBookMark() < mMediaPlayer.getDuration() - 10000) {
				new AlertDialog.Builder(movieActivity).setCancelable(false).setTitle(R.string.resume_playing_title)
						.setMessage(mContext.getString(R.string.resume_playing_message, Utils.formatDuration((int) videoObject.getBookMark())))
						.setNegativeButton(R.string.resume_playing_resume, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mMediaPlayer.seekTo((int) videoObject.getBookMark());
								mediaPlayerStart();
								movieActivity.setVideoInfo(videoObject);
							}
						}).setPositiveButton(R.string.resume_playing_restart, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mediaPlayerStart();
								movieActivity.setVideoInfo(videoObject);
							}
						}).show();
				return;
			}
		} else {
			int videoType = movieActivity.getIntent().getIntExtra(Constants.EXTRA_VIDEO_TYPE, VideoObject.VIDEO_TYPE_2D);
			controlHanlder.post(rSubtitle);
			movie_view.setVideoType(videoType);
		}
		mediaPlayerStart();
		movieActivity.setVideoInfo(videoObject);
	}

	private Runnable rSubtitle = new Runnable() {
		@Override
		public void run() {
			String title = movieActivity.getIntent().getStringExtra(Intent.EXTRA_TITLE);
			if (!"".equals(title)) {
				String subtitleFilePath = Constants.THUMBNAIL_PATH + title + ".srt";
				String enSubtitleFilePath = Constants.THUMBNAIL_PATH + "en" + title + ".srt";
				if (new File(subtitleFilePath).exists()) {
					tvSubtitle.setVisibility(View.VISIBLE);
					showSubTitle(subtitleFilePath);
				} else if (new File(enSubtitleFilePath).exists()) {
					tvSubtitle.setVisibility(View.VISIBLE);
					showSubTitle(enSubtitleFilePath);
				} else {
					/** 重复加载(因为有可能开始播放的时候，云立方的字幕可能未加载完成) */
					controlHanlder.postDelayed(rSubtitle, 3000);
				}
			}
		}
	};

	/** 真正执行MediaPlayer的start播放 */
	private void mediaPlayerStart() {
		Utils.showLogDebug("mediaPlayerStart");
		if (mHasFocus && mResumed) {
			if (movie_view.getVideoType() == VideoObject.VIDEO_TYPE_3D_LR || movie_view.getVideoType() == VideoObject.VIDEO_TYPE_3D_UD) {
				if (!CameraManagerService.isCameraOpened) {
					CameraManagerService.mcoStart(mContext);
				}
			}

			mPaused = false;
			mMediaPlayer.start();
			movieActivity.getMovieControllerOverlay().startUpdateCurrentPosition();
			setFixedSizeByConvergence();
			movieActivity.getMovieControllerOverlay().setPlayButton(true);
			movieActivity.getMovieControllerOverlay().enablePreviousAndNext(true);
			movieActivity.setButtionEnable(true);
		}
	}

	/** MediaPlayer播放完成的回调 */
	private OnCompletionListener mVideoCompleteListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			Utils.showLogDebug("MediaPlayer onCompletion");
			List<VideoObject> playList = movieActivity.getVideoList();
			movieActivity.getMovieControllerOverlay().stopUpdateCurrentPosition();
			if (SettingUtils.getIsContinue(mContext) && playList != null && playList.size() > 1 && playList.contains(DataLoadManager.getVideoObject(mUri))) {
				playNext();
			} else {
				movieActivity.finish();
			}
		}
	};

	/** MediaPlayer发生错误的回调 */
	private OnErrorListener errorListener = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			movieActivity.getMovieControllerOverlay().stopUpdateCurrentPosition();
			Utils.showLogDebug("error when playing,cause by what " + what + ",extra " + extra);
			Message msg = new Message();
			msg.what = ERROR;
			msg.arg1 = what;
			msg.arg2 = extra;
			controlHanlder.sendMessage(msg);
			return true;
		}
	};

	public void onResume() {
		Utils.showLogDebug("MoviePlayer onResume");
		mResumed = true;
		if (mMediaPlayer != null && !mHasFocus) {
			mediaPlayerStart();
			movieActivity.getMovieControllerOverlay().iv_play.setImageResource(R.drawable.icn_controller_pause);
		}
		if (PlayMode.MODE_3D.equals(mPlayMode)) {
			Utils.option3DGuanshan(movieActivity, true);
			CameraManagerService.mcoStart(movieActivity);
		}
		((PlayerApplication) movieActivity.getApplication()).serviceHelper.onResume(airtouchGestureHandler);
	}

	public void onPause() {
		Utils.option3DGuanshan(movieActivity, false);
		Utils.showLogDebug("MoviePlayer onPause");
		mResumed = false;
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			movieActivity.getMovieControllerOverlay().stopUpdateCurrentPosition();
		}
		CameraManagerService.mcoStop(movieActivity);
		((PlayerApplication) movieActivity.getApplication()).serviceHelper.onWindowFocusChanged(movieActivity, false, airtouchGestureHandler);// air
		((PlayerApplication) movieActivity.getApplication()).serviceHelper.onPause();
	}

	public void onSaveInstanceState(Bundle outState) {
	}

	public void onDestroy() {
		Utils.showLogDebug("MoviePlayer onDestroy");
		if (mMediaPlayer != null) {
			new Thread() {
				@Override
				public void run() {
					Holography.deinitHolography();
				}
			}.start();
			VideoObject videoObject = DataLoadManager.getVideoObject(mUri);
			if (videoObject != null) {
				MediaStoreOperator.updateBookMark(mContext, videoObject, mMediaPlayer.getCurrentPosition());
			}
			controlHanlder.removeMessages(AUTO_REFRESHING);
			mMediaPlayer.stop();
			mMediaPlayer.release();
			movieActivity.getMovieControllerOverlay().stopUpdateCurrentPosition();
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		Utils.showLogDebug("MoviePlayer onWindowFocusChanged " + hasFocus);
		mHasFocus = hasFocus;
		if (hasFocus) {
			if (mResumed && mMediaPlayer != null && !mPaused) {
				mediaPlayerStart();
			}
			((PlayerApplication) movieActivity.getApplication()).serviceHelper.onWindowFocusChanged(movieActivity, hasFocus, airtouchGestureHandler);
		}
		if (PlayMode.MODE_3D.equals(mPlayMode)) {
			Utils.option3DGuanshan(movieActivity, hasFocus);
		}
	}

	/** 播放或暂停 */
	public void playPause() {
		Utils.showLogDebug("MoviePlayer playPause");
		mPaused = isPlaying();
		if (isPlaying()) {
			mMediaPlayer.pause();
			movieActivity.getMovieControllerOverlay().stopUpdateCurrentPosition();
			// 开启按频率刷新，使暂停时有人眼追踪
			controlHanlder.sendEmptyMessage(AUTO_REFRESHING);
		} else {
			movieActivity.getMovieControllerOverlay().startUpdateCurrentPosition();
			mMediaPlayer.start();
		}
	}

	/** 播放下一个视频 */
	public void playNext() {
		Utils.showLogDebug("MoviePlayer playNext");
		List<VideoObject> playList = movieActivity.getVideoList();
		VideoObject videoObject = DataLoadManager.getVideoObject(mUri);
		if (playList != null && playList.size() > 1 && playList.contains(videoObject)) {
			movieActivity.getMovieControllerOverlay().enablePreviousAndNext(false);
			movieActivity.setButtionEnable(false);
			isPlayerPrepared = false;
			int index = DataLoadManager.getIndexInList(videoObject, playList);
			index++;
			if (index >= playList.size()) {
				index = 0;
			}
			if (videoObject != null) {
				// 保存上一个视频的播放进度
				MediaStoreOperator.updateBookMark(mContext, videoObject, mMediaPlayer.getCurrentPosition());
			}
			videoObject = playList.get(index);
			mUri = videoObject.getContentUri();
			playVideo(videoObject.getContentUri());
		} else {
			Utils.showToast(movieActivity, movieActivity.getString(R.string.lookfor_more_resource));
		}
	}

	/** 播放上一个视频 */
	public void playPrevious() {
		Utils.showLogDebug("MoviePlayer playPrevious");
		VideoObject videoObject = DataLoadManager.getVideoObject(mUri);
		List<VideoObject> playList = movieActivity.getVideoList();
		if (playList != null && playList.size() > 1 && playList.contains(videoObject)) {
			movieActivity.getMovieControllerOverlay().enablePreviousAndNext(false);
			movieActivity.setButtionEnable(false);
			isPlayerPrepared = false;
			int index = DataLoadManager.getIndexInList(videoObject, playList);
			index--;
			if (index < 0) {
				index = playList.size() - 1;
			}
			if (videoObject != null) {
				// 保存上一个视频的播放进度
				MediaStoreOperator.updateBookMark(mContext, videoObject, mMediaPlayer.getCurrentPosition());
			}
			videoObject = playList.get(index);
			mUri = videoObject.getContentUri();
			playVideo(videoObject.getContentUri());
		} else {
			Utils.showToast(movieActivity, movieActivity.getString(R.string.lookfor_more_resource));
		}
	}

	/** 快进 */
	public void fastForward() {
		if (isPlaying()) {
			int position = mMediaPlayer.getCurrentPosition();
			position += 5000;
			mMediaPlayer.seekTo(position);
			movieActivity.showDurationInfo(position);
		}
	}

	/** 快退 */
	public void rewind() {
		if (isPlaying()) {
			int position = mMediaPlayer.getCurrentPosition();
			position -= 5000;
			mMediaPlayer.seekTo(position);
			movieActivity.showDurationInfo(position);
		}
	}

	/** 增加音量 */
	public void addVolume() {
		int currentVolume = movieActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		currentVolume++;
		if (currentVolume > movieActivity.maxVolume) {
			currentVolume = movieActivity.maxVolume;
		}
		movieActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
		movieActivity.showVolumeInfo(currentVolume);
	}

	/** 降低音量 */
	public void delVolume() {
		int currentVolume = movieActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		currentVolume--;
		movieActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
		movieActivity.showVolumeInfo(currentVolume);
	}

	/** 是否正在播放 */
	public boolean isPlaying() {
		return mMediaPlayer != null && mMediaPlayer.isPlaying() && isPlayerPrepared;
	}

	/** 获取视频长度 */
	public int getDuration() {
		int ret = 0;
		if (mMediaPlayer != null && isPlayerPrepared) {
			ret = mMediaPlayer.getDuration();
		}
		return ret;
	}

	/** 获取当前播放进度 */
	public int getCurrentPosition() {
		int ret = 0;
		if (mMediaPlayer != null && isPlayerPrepared) {
			ret = mMediaPlayer.getCurrentPosition();
		}
		return ret;
	}

	/** 跳转到指定进度 */
	public void seekTo(int position) {
		if (isPlayerPrepared) {
			mMediaPlayer.seekTo(position);
		}
	}

	/** 播放模式切换 */
	public void changePlayMode(PlayMode playMode) {
		mPlayMode = playMode;
		Utils.showLogDebug("MoviePlayer changePlayMode");
		if (PlayMode.MODE_3D.equals(playMode)) {
			Utils.option3DGuanshan(movieActivity, true);
			movie_view.show3D(true);
			movieActivity.getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_3d);
		} else if (PlayMode.MODE_2D.equals(playMode)) {
			Utils.option3DGuanshan(movieActivity, false);
			movieActivity.getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_2d);
			movie_view.show3D(false);
		} else if (PlayMode.MODE_LR.equals(playMode)) {
			Utils.option3DGuanshan(movieActivity, false);
			movieActivity.getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_lr);
			movie_view.show3D(false);
		} else if (PlayMode.MODE_VR.equals(playMode)) {
			Utils.option3DGuanshan(movieActivity, false);
			movieActivity.getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_vr);
			movie_view.show3D(false);
		}
	}

	/** 显示字幕文件 */
	private void showSubTitle(String mPathString) {
		Utils.showLogDebug("MoviePlayer showSubTitle,path = " + mPathString);
		mPathString = mPathString.substring(0, mPathString.lastIndexOf(".")) + ".srt";
		File mFile = null;
		mFile = new File(mPathString.substring(0, mPathString.lastIndexOf(".")) + ".srt");
		tvSubtitle.setText("");
		if (mFile != null && !mFile.exists()) {
			this.IsExitSubtitle = false;
			return;
		} else {
			this.IsExitSubtitle = true;
		}

		File mSubTitleFile = new File(Environment.getExternalStorageDirectory() + "/Subtitle.srt");
		if (mSubTitleFile != null && mSubTitleFile.exists()) {
			mSubTitleFile.delete();
		}
		CodeChangeUtils.convertCodeAndGetText(mPathString);
		try {
			Uri uirs = Uri.parse(Environment.getExternalStorageDirectory() + "/Subtitle.srt");
			mMediaPlayer.addTimedTextSource(movieActivity, uirs, MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
			String scheme = uirs.getScheme();
			TrackInfo[] trackInfos = mMediaPlayer.getTrackInfo();
			if (trackInfos != null && trackInfos.length > 0) {
				for (int i = 0; i < trackInfos.length; i++) {
					final TrackInfo info = trackInfos[i];
					if (info.getTrackType() == TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
					} else if (info.getTrackType() == TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT) {
						mMediaPlayer.selectTrack(i);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mMediaPlayer.setOnTimedTextListener(mTimeTextListener);
	}

	/** 当字幕文件有新文本内容返回 */
	private OnTimedTextListener mTimeTextListener = new OnTimedTextListener() {
		@Override
		public void onTimedText(MediaPlayer mp, TimedText text) {
			if (text != null && text.getText() != null && IsExitSubtitle) {
				try {
					String mString = new String(text.getText().getBytes(CodeChangeUtils.JudgeString(text.getText())), "UTF-8");
					tvSubtitle.setText(text.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				tvSubtitle.setText("");
			}
		}
	};
}
