package com.estar.video;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.MoviePlayer.PlayMode;
import com.estar.video.data.DataLoadManager;
import com.estar.video.data.LocalDataBaseOperator;
import com.estar.video.data.SettingItem;
import com.estar.video.data.VideoObject;
import com.estar.video.ui.ChooseListAdapter;
import com.estar.video.ui.ChooseListAdapter.ChooseItem;
import com.estar.video.ui.MenuPopupWindow;
import com.estar.video.ui.MenuPopupWindow.UpdateSettingCallback;
import com.estar.video.ui.MovieControllerOverlay;
import com.estar.video.ui.PlayerPopupWindow;
import com.estar.video.utils.Constants;
import com.estar.video.utils.CrashHandler;
import com.estar.video.utils.LightManage;
import com.estar.video.utils.SettingUtils;
import com.estar.video.utils.Utils;
import com.estar.video.utils.VideoUtils;

/**
 * 播放界面
 * 
 * @author zgl
 * 
 */
public class HarderPlayerActivity extends Activity {
	private MoviePlayer mPlayer;
	private Context mContext;
	private boolean mFinishOnCompletion;
	public AudioManager mAudioManager = null;
	private GestureDetector mGestureDetector;
	private WindowManager mWindowManager;

	/** 显示调节亮度和进度 */
	private View pop_light_sound;
	private TextView tv_info;
	/** 显示调节进度 */
	private View pop_duration_info;
	private TextView tv_duration_info;
	private View rootView;
	private ProgressBar loading_bar;

	private ImageView iv_action_back, iv_action_option;
	private TextView iv_action_title, tv_format, tv_convergence;
	private View ll_convergence;

	/** 播放控制栏 */
	private MovieControllerOverlay movieControllerOverlay;
	/** 右上角弹出的设置菜单 */
	private MenuPopupWindow mMenuPopupWindow;

	/** 最大音量 */
	public int maxVolume;
	public final static int maxLight = 1;
	private int volume = 0;
	private float light = 0.80f;// 默认为0.80

	/** 当前的触摸状态 */
	private TouchState mTouchState = TouchState.IDLE;

	/** 触摸状态 */
	private enum TouchState {
		IDLE, ADJUST_LIGHT, ADJUST_SOUND, ADJUST_SEEK
	}

	/** 记录触摸屏手势调节播放进度的值 */
	private int seekToPosition;

	// private final double FLING_MIN_DISTANCE = 1;
	private final double FLING_MIN_VELOCITY = 0.5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 进入播放界面则停止列表界面的2D、3D判断，以免启动过慢
		VideoUtils.shutdownThreadPools();

		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		mContext = this;

		setContentView(R.layout.movie_view);
		rootView = findViewById(R.id.root);
		pop_light_sound = findViewById(R.id.pop_light_sound);
		tv_info = (TextView) findViewById(R.id.tv_info);
		pop_duration_info = findViewById(R.id.pop_duration_info);
		tv_duration_info = (TextView) findViewById(R.id.tv_duration_info);

		loading_bar = (ProgressBar) findViewById(R.id.loading_bar);

		// 声音 亮度
		mAudioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume;
		// light = LightManage.getScreenBrightness(this) * 100 / maxLight;
		// light = LightManage.getActivitLight(HarderPlayerActivity.this);
		LightManage.setActivityLight(HarderPlayerActivity.this, light);
		// 屏幕宽高
		Point size = new Point();
		mWindowManager.getDefaultDisplay().getSize(size);
		Constants.screenWidth = size.x;
		Constants.screenHeight = size.y;

		Intent intent = getIntent();
		Uri uri = intent.getData();
		if ("file".equals(uri.getScheme())) {// 将file的uri转换为content
			uri = DataLoadManager.getContentUri(uri.getPath());
			if (uri == null) {
				uri = Utils.getContentUri(this, intent.getData());
			}
			if (uri != null) {
				intent.setData(uri);
			} else {
				uri = intent.getData();
			}
		}
		initializeActionBar(intent);

		mFinishOnCompletion = intent.getBooleanExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
		mPlayer = new MoviePlayer(rootView, this, uri, savedInstanceState, !mFinishOnCompletion);

		if (intent.hasExtra(MediaStore.EXTRA_SCREEN_ORIENTATION)) {
			int orientation = intent.getIntExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			if (orientation != getRequestedOrientation()) {
				setRequestedOrientation(orientation);
			}
		}

		mGestureDetector = new GestureDetector(this, gestureListener);
		movieControllerOverlay = new MovieControllerOverlay(mPlayer, this, rootView);

		mMenuPopupWindow = new MenuPopupWindow(this, getLayoutInflater(), R.drawable.pop_background, getResources().getDrawable(R.drawable.pop_division), new UpdateSettingCallback() {
			@Override
			public void updateSettingItems(List<SettingItem> settingItems) {
				settingItems.clear();
				initSettingItems(settingItems);
			}
		});
	}

	/** 获取播放列表 */
	public List<VideoObject> getVideoList() {
		Intent intent = getIntent();
		Uri uri = intent.getData();
		List<VideoObject> videos = null;
		long[] list = intent.getLongArrayExtra(Constants.LIST_PARAMETER);
		if (list != null) {
			try {
				videos = DataLoadManager.getVideosByIdArray(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Utils.isMediaUri(uri)) {
			VideoObject videoObject = DataLoadManager.getVideoByUri(uri);
			videos = DataLoadManager.getBucketVideos(videoObject.getBucketId());
		}
		return videos;
	}

	/**
	 * 生成要显示的设置项
	 */
	public void initSettingItems(List<SettingItem> settingItems) {
		SettingItem item;
		if (Utils.isMediaUri(getIntent().getData())) {
			item = new SettingItem();
			item.setDrawableId(R.drawable.icn_setting_continue);
			item.setText(getString(R.string.play_continue, Utils.getOpenOrCloseStr(SettingUtils.getIsContinue(mContext), mContext)));
			item.setOnClickListener(new SettingItem.OnClickListener() {
				@Override
				public void onClick(Object arg) {
					SettingUtils.setIsContinue(mContext, !SettingUtils.getIsContinue(mContext));
				}
			});
			settingItems.add(item);

			item = new SettingItem();
			item.setDrawableId(R.drawable.icn_setting_remember);
			item.setText(getString(R.string.play_remember, Utils.getOpenOrCloseStr(SettingUtils.getIsRemember(mContext), mContext)));
			item.setOnClickListener(new SettingItem.OnClickListener() {
				@Override
				public void onClick(Object arg) {
					SettingUtils.setIsRemember(mContext, !SettingUtils.getIsRemember(mContext));
				}
			});
			settingItems.add(item);
		}

		item = new SettingItem();
		item.setDrawableId(R.drawable.icn_setting_subtitle);
		item.setText(getString(R.string.subtitle, Utils.getOpenOrCloseStr(SettingUtils.getShowSubtitle(mContext), mContext)));
		item.setOnClickListener(new SettingItem.OnClickListener() {
			@Override
			public void onClick(Object arg) {
				SettingUtils.setShowSubtitle(mContext, !SettingUtils.getShowSubtitle(mContext));
			}
		});
		settingItems.add(item);
	}

	/** 选择视频格式对话框 */
	private PlayerPopupWindow choosePopupWindow;

	/** 显示手动选择视频格式对话框 */
	private void showChooseVideoTypeDialog(final VideoObject object) {
		ListView lv_list = (ListView) getLayoutInflater().inflate(R.layout.menu_list, null);
		final List<ChooseListAdapter.ChooseItem> items = new ArrayList<ChooseItem>();
		ChooseItem item2D = new ChooseItem(R.string.type_2d, false);
		ChooseItem itemLR = new ChooseItem(R.string.type_3d_lr, false);
		ChooseItem itemUD = new ChooseItem(R.string.type_3d_ud, false);
		switch (mPlayer.movie_view.getVideoType()) {
		case VideoObject.VIDEO_TYPE_2D:
			item2D.setChecked(true);
			break;
		case VideoObject.VIDEO_TYPE_3D_LR:
			itemLR.setChecked(true);
			break;
		case VideoObject.VIDEO_TYPE_3D_UD:
			itemUD.setChecked(true);
			break;
		default:
			break;
		}
		items.add(item2D);
		items.add(itemLR);
		items.add(itemUD);
		lv_list.setAdapter(new ChooseListAdapter(items, this));
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int type = -1;
				switch (items.get(position).getStringId()) {
				case R.string.type_2d:
					type = VideoObject.VIDEO_TYPE_2D;
					break;
				case R.string.type_3d_lr:
					type = VideoObject.VIDEO_TYPE_3D_LR;
					break;
				case R.string.type_3d_ud:
					type = VideoObject.VIDEO_TYPE_3D_UD;
					break;
				default:
					break;
				}
				if (type != -1) {
					mPlayer.movie_view.setVideoType(type);
					if (object != null) {
						LocalDataBaseOperator.updateIs3d(mContext, object, type);
					}
				}
				choosePopupWindow.dismiss();
				setVideoInfo(object);
				if (VideoObject.VIDEO_TYPE_3D_LR != mPlayer.movie_view.getVideoType() && VideoObject.VIDEO_TYPE_3D_UD != mPlayer.movie_view.getVideoType()
						&& (PlayMode.MODE_LR.equals(mPlayer.getPlayMode()) || PlayMode.MODE_VR.equals(mPlayer.getPlayMode()))) {
					mPlayer.setPlayMode(PlayMode.MODE_3D);
					getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_3d);
				}
			}
		});

		choosePopupWindow = new PlayerPopupWindow(HarderPlayerActivity.this);
		choosePopupWindow.setContentView(lv_list);
		choosePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_transparent_background));
		choosePopupWindow.setOutsideTouchable(true);
		choosePopupWindow.setFocusable(true);
		choosePopupWindow.showAtLocation(rootView, Gravity.TOP | Gravity.RIGHT, 20, 135);
		choosePopupWindow.update(350, LayoutParams.WRAP_CONTENT);
	}

	/** 显示选择播放模式弹框 */
	public void showPlayModeDialog() {
		final VideoObject object = DataLoadManager.getVideoObject(mPlayer.getUri());
		ListView lv_list = (ListView) getLayoutInflater().inflate(R.layout.menu_list, null);
		final List<ChooseListAdapter.ChooseItem> items = new ArrayList<ChooseItem>();
		ChooseItem item2D = new ChooseItem(R.string.mode_2d, false);
		ChooseItem item3D = new ChooseItem(R.string.mode_3d, false);
		ChooseItem itemLR = new ChooseItem(R.string.mode_lr, false);
		ChooseItem itemVR = new ChooseItem(R.string.mode_vr, false);
		if (MoviePlayer.PlayMode.MODE_2D.equals(mPlayer.getPlayMode())) {
			item2D.setChecked(true);
		} else if (MoviePlayer.PlayMode.MODE_3D.equals(mPlayer.getPlayMode())) {
			item3D.setChecked(true);
		} else if (MoviePlayer.PlayMode.MODE_LR.equals(mPlayer.getPlayMode())) {
			itemLR.setChecked(true);
		} else if (MoviePlayer.PlayMode.MODE_VR.equals(mPlayer.getPlayMode())) {
			itemVR.setChecked(true);
		}
		items.add(item2D);
		items.add(item3D);
		if (object != null && object.is3dVideo()) {
			items.add(itemLR);
			items.add(itemVR);
		}
		lv_list.setAdapter(new ChooseListAdapter(items, this));
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (items.get(position).getStringId()) {
				case R.string.mode_2d:
					mPlayer.changePlayMode(PlayMode.MODE_2D);
					break;
				case R.string.mode_3d:
					mPlayer.changePlayMode(PlayMode.MODE_3D);
					break;
				case R.string.mode_lr:
					mPlayer.changePlayMode(PlayMode.MODE_LR);
					break;
				case R.string.mode_vr:
					mPlayer.changePlayMode(PlayMode.MODE_VR);
					break;
				default:
					break;
				}
				choosePopupWindow.dismiss();
				setVideoInfo(object);
			}
		});

		choosePopupWindow = new PlayerPopupWindow(HarderPlayerActivity.this);
		choosePopupWindow.setContentView(lv_list);
		choosePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_transparent_background));
		choosePopupWindow.setOutsideTouchable(true);
		choosePopupWindow.setFocusable(true);
		choosePopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.LEFT, 20, 250);
		choosePopupWindow.update(200, LayoutParams.WRAP_CONTENT);
	}

	/** 显示手动选择视频格式对话框 */
	private void showChooseVideoConvergenceDialog(final VideoObject object) {
		ListView lv_list = (ListView) getLayoutInflater().inflate(R.layout.menu_list, null);
		final List<ChooseListAdapter.ChooseItem> items = new ArrayList<ChooseItem>();
		ChooseItem itemHalf = new ChooseItem(R.string.convergence_half_width, false);
		ChooseItem itemFull = new ChooseItem(R.string.convergence_none, false);
		switch (mPlayer.movie_view.getConvergence()) {
		case VideoObject.CONVERGENCE_NONE:
			itemFull.setChecked(true);
			break;
		case VideoObject.CONVERGENCE_HALF:
			itemHalf.setChecked(true);
			break;
		default:
			break;
		}
		items.add(itemHalf);
		items.add(itemFull);
		lv_list.setAdapter(new ChooseListAdapter(items, this));
		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int convergence = -1;
				switch (items.get(position).getStringId()) {
				case R.string.convergence_half_width:
					convergence = VideoObject.CONVERGENCE_HALF;
					break;
				case R.string.convergence_none:
					convergence = VideoObject.CONVERGENCE_NONE;
					break;
				default:
					break;
				}
				if (object != null) {
					LocalDataBaseOperator.updateConvergence(mContext, object, convergence);
				}
				int width = mPlayer.getMediaPlayer().getVideoWidth();
				int height = mPlayer.getMediaPlayer().getVideoHeight();
				mPlayer.movie_view.setFixedSize(width, height, convergence);

				choosePopupWindow.dismiss();
				setVideoInfo(object);

				if (VideoObject.VIDEO_TYPE_3D_LR != mPlayer.movie_view.getVideoType() && VideoObject.VIDEO_TYPE_3D_UD != mPlayer.movie_view.getVideoType()
						&& (PlayMode.MODE_LR.equals(mPlayer.getPlayMode()) || PlayMode.MODE_VR.equals(mPlayer.getPlayMode()))) {
					mPlayer.setPlayMode(PlayMode.MODE_3D);
					getMovieControllerOverlay().iv_play_mode.setImageResource(R.drawable.icn_controller_3d);
				}
			}
		});

		choosePopupWindow = new PlayerPopupWindow(HarderPlayerActivity.this);
		choosePopupWindow.setContentView(lv_list);
		choosePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_transparent_background));
		choosePopupWindow.setOutsideTouchable(true);
		choosePopupWindow.setFocusable(true);
		choosePopupWindow.showAtLocation(rootView, Gravity.TOP | Gravity.RIGHT, 150, 135);
		choosePopupWindow.update(350, LayoutParams.WRAP_CONTENT);
	}

	/** 初始化ActionBar(title) */
	private void initializeActionBar(Intent intent) {
		ActionBar actionBar = getActionBar();

		actionBar.setCustomView(R.layout.player_actionbar_overlay);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		iv_action_back = (ImageView) findViewById(R.id.iv_action_back);
		iv_action_back.setOnClickListener(mOnClickLinstner);

		iv_action_title = (TextView) findViewById(R.id.iv_action_title);
		iv_action_option = (ImageView) findViewById(R.id.iv_action_option);
		iv_action_option.setOnClickListener(mOnClickLinstner);

		tv_format = (TextView) findViewById(R.id.tv_format);
		tv_format.setOnClickListener(mOnClickLinstner);
		tv_convergence = (TextView) findViewById(R.id.tv_convergence);
		tv_convergence.setOnClickListener(mOnClickLinstner);
		ll_convergence = findViewById(R.id.ll_convergence);

		// actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
		// ActionBar.DISPLAY_HOME_AS_UP);
		String title = intent.getStringExtra(Intent.EXTRA_TITLE);
		if (title == null) {
			Cursor cursor = null;
			try {
				cursor = getContentResolver().query(intent.getData(), new String[] { VideoColumns.TITLE }, null, null, null);
				if (cursor != null && cursor.moveToNext()) {
					title = cursor.getString(0);
				}
			} catch (Throwable t) {
				Utils.showLogError("cannot get title from: " + intent.getDataString());
				t.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
		}
		if (title != null) {
			iv_action_title.setText(title);
			// actionBar.setTitle(title);
		}
		actionBar.hide();
	}

	/** 初始化显示视频信息 */
	public void setVideoInfo(final VideoObject videoObject) {
		ll_convergence.setVisibility(View.GONE);
		if (videoObject != null) {
			// ActionBar actionBar = getActionBar();
			// actionBar.setTitle(videoObject.getTitle());
			iv_action_title.setText(videoObject.getTitle());
		}
		// 半宽、半高选项
		if (VideoObject.VIDEO_TYPE_3D_LR == mPlayer.movie_view.getVideoType() || VideoObject.VIDEO_TYPE_3D_UD == mPlayer.movie_view.getVideoType()) {
			ll_convergence.setVisibility(View.VISIBLE);
			switch (mPlayer.movie_view.getConvergence()) {
			case VideoObject.CONVERGENCE_NONE:
				tv_convergence.setText(getString(R.string.convergence_none));
				break;
			case VideoObject.CONVERGENCE_HALF:
				tv_convergence.setText(getString(R.string.convergence_half_width));
				break;
			default:
				break;
			}
		}
		switch (mPlayer.movie_view.getVideoType()) {
		case VideoObject.VIDEO_TYPE_2D:
			tv_format.setText(R.string.type_2d);
			break;
		case VideoObject.VIDEO_TYPE_3D_LR:
			tv_format.setText(R.string.type_3d_lr);
			break;
		case VideoObject.VIDEO_TYPE_3D_UD:
			tv_format.setText(R.string.type_3d_ud);
			break;
		default:
			break;
		}
		movieControllerOverlay.setVideoInfo();
	}

	/** 获取控制栏 */
	public MovieControllerOverlay getMovieControllerOverlay() {
		return movieControllerOverlay;
	}

	public MenuPopupWindow getMenuPopupWindow() {
		return mMenuPopupWindow;
	}

	/** 获取播放类实例 */
	public MoviePlayer getMoviePlayer() {
		return mPlayer;
	}

	/** 隐藏加载滚动条 */
	public void hideLoadingBar() {
		loading_bar.setVisibility(View.GONE);
	}

	/** 显示加载滚动条 */
	public void showLoadingBar() {
		loading_bar.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mPlayer.isPlayerPrepared) {
			return true;
		}

		if (movieControllerOverlay.getIsLocked()) {
			movieControllerOverlay.repostHideRunnable();
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (TouchState.ADJUST_SEEK.equals(mTouchState)) {
				mPlayer.seekTo(seekToPosition);
			}
			pop_light_sound.setVisibility(View.GONE);
			pop_duration_info.setVisibility(View.GONE);
			mTouchState = TouchState.IDLE;
			break;
		case MotionEvent.ACTION_DOWN:
			seekToPosition = mPlayer.getCurrentPosition();
			break;
		default:
			break;
		}
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		((AudioManager) getSystemService(AUDIO_SERVICE)).requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		super.onStart();
	}

	@Override
	protected void onStop() {
		((AudioManager) getSystemService(AUDIO_SERVICE)).abandonAudioFocus(null);
		super.onStop();
	}

	@Override
	public void onPause() {
		mPlayer.onPause();
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (mMenuPopupWindow != null && mMenuPopupWindow.isShowing()) {
			mMenuPopupWindow.dismiss();
		} else if (getMovieControllerOverlay().getIsLocked()) {
			Utils.showToast(mContext, getString(R.string.is_locked));
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onResume() {
		mPlayer.onResume();

		// 捕获崩溃异常，并关闭光栅
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mPlayer.onSaveInstanceState(outState);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.operation, menu);
	// return true;
	// }

	@Override
	public void onDestroy() {
		// setResult(RESULT_OK);
		mPlayer.onDestroy();
		Utils.option3DGuanshan(HarderPlayerActivity.this, false);
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mPlayer.onWindowFocusChanged(hasFocus);
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume;
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume;
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume;
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/** 触摸手势回调 */
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			movieControllerOverlay.updateControllerOverlay();
			return true;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (e1 == null || e2 == null) {
				return true;
			}

			// 锁定状态不可用
			if (!movieControllerOverlay.getIsLocked()) {
				final int x = (int) e1.getX();
				if (Math.abs(distanceX) < Math.abs(distanceY)) {
					if (x > Constants.screenWidth * 2 / 3 && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
						// 调节声音
						if (!TouchState.ADJUST_SOUND.equals(mTouchState) && !TouchState.IDLE.equals(mTouchState)) {
							return true;
						}
						pop_light_sound.setBackgroundResource(R.drawable.sound);
						pop_light_sound.setVisibility(View.VISIBLE);
						volume = (int) (volume + 100 * distanceY / Constants.screenHeight);
						if (volume < 0)
							volume = 0;
						else if (volume > 100)
							volume = 100;
						pop_light_sound.getBackground().setLevel(volume);
						tv_info.setText(String.valueOf(volume));

						int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
						currentVolume = maxVolume * volume / 100;
						mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);

						mTouchState = TouchState.ADJUST_SOUND;
					} else if (x < Constants.screenWidth / 3 && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
						// 调节亮度
						if (!TouchState.ADJUST_LIGHT.equals(mTouchState) && !TouchState.IDLE.equals(mTouchState)) {
							return true;
						}
						pop_light_sound.setBackgroundResource(R.drawable.light);
						pop_light_sound.getBackground().setLevel((int) (light * 100));
						pop_light_sound.setVisibility(View.VISIBLE);
						tv_info.setText(String.valueOf((int) (light * 100)));
						light = light + distanceY / Constants.screenHeight;
						if (light < 0.0f)
							light = 0.0f;
						else if (light > 1.0f)
							light = 1.0f;
						LightManage.setActivityLight(HarderPlayerActivity.this, light);
						mTouchState = TouchState.ADJUST_LIGHT;
					}
				} else {
					// 调节进度
					if (!TouchState.ADJUST_SEEK.equals(mTouchState) && !TouchState.IDLE.equals(mTouchState)) {
						return true;
					}

					pop_duration_info.setVisibility(View.VISIBLE);
					if (distanceX > 0) {
						seekToPosition -= 1000;
					} else {
						seekToPosition += 1000;
					}
					if (seekToPosition < 0) {
						seekToPosition = 0;
					} else if (seekToPosition > mPlayer.getDuration()) {
						seekToPosition = mPlayer.getDuration();
					}
					tv_duration_info.setText(Utils.formatDuration(seekToPosition) + "/" + Utils.formatDuration(mPlayer.getDuration()));

					mTouchState = TouchState.ADJUST_SEEK;
				}
			}
			return true;
		};
	};

	/** 空中触控调节声音时显示信息 */
	public void showVolumeInfo(int currentVolume) {
		mHandler.removeMessages(HIDE_VOLUME_INFO);
		pop_light_sound.setBackgroundResource(R.drawable.sound_popup_3);
		pop_light_sound.setVisibility(View.VISIBLE);
		volume = 100 * currentVolume / maxVolume;
		if (volume < 0)
			volume = 0;
		tv_info.setText(String.valueOf(volume));
		mHandler.sendEmptyMessageDelayed(HIDE_VOLUME_INFO, HIDE_POP_DELAY);
	}

	/** 空中触控调节进度时显示信息 */
	public void showDurationInfo(int currentDuration) {
		mHandler.removeMessages(HIDE_DURATION_INFO);
		pop_duration_info.setVisibility(View.VISIBLE);
		if (currentDuration < 0)
			currentDuration = 0;
		tv_duration_info.setText(Utils.formatDuration(currentDuration) + "/" + Utils.formatDuration(mPlayer.getDuration()));
		mHandler.sendEmptyMessageDelayed(HIDE_DURATION_INFO, HIDE_POP_DELAY);
	}

	public final static int HIDE_VOLUME_INFO = 0;
	public final static int HIDE_DURATION_INFO = 1;
	public final static int HIDE_POP_DELAY = 2000;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HIDE_VOLUME_INFO:
				pop_light_sound.setVisibility(View.GONE);
				break;
			case HIDE_DURATION_INFO:
				pop_duration_info.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		};
	};

	private OnClickListener mOnClickLinstner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			VideoObject videoObject = DataLoadManager.getVideoObject(mPlayer.getUri());
			switch (v.getId()) {
			case R.id.iv_action_back:
				onBackPressed();
				break;
			case R.id.tv_format:
				showChooseVideoTypeDialog(videoObject);
				break;
			case R.id.tv_convergence:
				showChooseVideoConvergenceDialog(videoObject);
				break;
			case R.id.iv_action_option:
				mMenuPopupWindow.show(rootView, 0, 125, 450, LayoutParams.WRAP_CONTENT);
				break;
			default:
				break;
			}
		}
	};

	/** 设置按钮是否可用 */
	public void setButtionEnable(boolean enabled) {
		iv_action_option.setEnabled(enabled);
		tv_format.setEnabled(enabled);
		tv_convergence.setEnabled(enabled);
	}
}
