package com.estar.video;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.ulifang.LogginActivity;
import com.estar.ulifang.UlifangMovieListFragment;
import com.estar.update2.DownAPKServiceNotify;
import com.estar.update2.HttpClient;
import com.estar.update2.UpdateApkInfo;
import com.estar.video.data.LocalDataBaseOperator;
import com.estar.video.data.MediaStoreOperator;
import com.estar.video.data.SettingItem;
import com.estar.video.ui.FolderFragment;
import com.estar.video.ui.HistoryFragment;
import com.estar.video.ui.ListFragment;
import com.estar.video.ui.MenuPopupWindow;
import com.estar.video.ui.MenuPopupWindow.UpdateSettingCallback;
import com.estar.video.ui.PlayerUrlFragment;
import com.estar.video.ui.SupportAirtouch;
import com.estar.video.utils.Constants;
import com.estar.video.utils.SettingUtils;
import com.estar.video.utils.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 视频列表界面（主界面）
 * 
 * @author zgl
 * 
 */
public class VideoFileListActivity extends Activity {
	private Context mContext;
	private ViewPager mViewPagerLocal, mViewPagerOnline;
	private static final int DEFAULT_OFFSCREEN_PAGES = 2;
	private TabsAdapter mTabsAdapterLocal, mTabsAdapterOnline;
	/** 根布局 */
	private View rootView;
	// private MoviePagerAdapter moviePagerAdapter;

	/** 顶部切换选项卡(本地、云立方) */
	private TextView tv_locala, tv_internet;
	/** 设置按钮 */
	private ImageView iv_settings;

	/** 当前展示的fragment */
	public Fragment fragment;

	/** 右上角弹出的设置菜单 */
	private MenuPopupWindow mMenuPopupWindow;

	private enum State {
		LOCAL, ONLINE
	}

	/** 当前的显示状态 */
	private State mState = State.LOCAL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utils.showLogDebug(getClass().getName() + " onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		((PlayerApplication) getApplication()).serviceHelper.bindService(this);
		mContext = this;

		final ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.main_actionbar_overlay);
		bar.setDisplayShowCustomEnabled(true);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayShowTitleEnabled(false);
		View homeIcon = findViewById(android.R.id.home);
		if (homeIcon == null) {
			bar.setDisplayShowHomeEnabled(false);
		} else {
			// 找到后获得他的父控件，然后隐藏即可
			((View) homeIcon.getParent()).setVisibility(View.GONE);
			// bar.setBackgroundDrawable(new
			// ColorDrawable(android.R.color.transparent));
		}
		setContentView(R.layout.video_main);

		rootView = findViewById(R.id.root_view);

		mViewPagerLocal = (ViewPager) findViewById(R.id.pager_local);
		mViewPagerLocal.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);

		mTabsAdapterLocal = new TabsAdapter(VideoFileListActivity.this, mViewPagerLocal);
		mTabsAdapterLocal.addTab(bar.newTab().setText(R.string.history), HistoryFragment.class, null);
		mTabsAdapterLocal.addTab(bar.newTab().setText(R.string.all), ListFragment.class, null);
		mTabsAdapterLocal.addTab(bar.newTab().setText(R.string.folder), FolderFragment.class, null);
		mTabsAdapterLocal.updateActionBarFromAdapter();

		mViewPagerOnline = (ViewPager) findViewById(R.id.pager_online);
		mTabsAdapterOnline = new TabsAdapter(VideoFileListActivity.this, mViewPagerOnline);
		mTabsAdapterOnline.addTab(bar.newTab().setText(R.string.yunlifang), UlifangMovieListFragment.class, null);
		mTabsAdapterOnline.addTab(bar.newTab().setText(R.string.play_url), PlayerUrlFragment.class, null);

		tv_locala = (TextView) findViewById(R.id.tv_local);
		tv_internet = (TextView) findViewById(R.id.tv_internet);
		tv_internet.setOnClickListener(mOnClickListener);
		tv_locala.setOnClickListener(mOnClickListener);
		tv_locala.setSelected(true);

		iv_settings = (ImageView) findViewById(R.id.iv_settings);
		iv_settings.setOnClickListener(mOnClickListener);
		Utils.checkCameraPermission(VideoFileListActivity.this);
		mMenuPopupWindow = new MenuPopupWindow(this, getLayoutInflater(), R.drawable.pop_background, getResources().getDrawable(R.drawable.pop_division), new UpdateSettingCallback() {
			@Override
			public void updateSettingItems(List<SettingItem> settingItems) {
				settingItems.clear();
				initSettingItems(settingItems);
			}
		});
		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}

		UpdateApk(); // 查看apk更新
	}

	@Override
	protected void onDestroy() {
		((PlayerApplication) getApplication()).serviceHelper.unBindService();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		((PlayerApplication) getApplication()).serviceHelper.onResume(airtouchGestureHandler);
	}

	@Override
	protected void onPause() {
		((PlayerApplication) getApplication()).serviceHelper.onPause();
		super.onPause();
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		((PlayerApplication) getApplication()).serviceHelper.onWindowFocusChanged(hasFocus, airtouchGestureHandler);// air
		if (hasFocus) {
			if (!((PlayerApplication) getApplication()).serviceHelper.isUseAirtouch()) {
				mIsAirtouchEnable = false;
			} else {
				mIsAirtouchEnable = true;
			}
			if (fragment != null) {
				fragment.setUserVisibleHint(true);
			}
		}
	};

	private static boolean mIsAirtouchEnable = false;

	public static boolean isAirtouchEnable() {
		return mIsAirtouchEnable;
	}

	private Handler airtouchGestureHandler = new Handler() {// air
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.AIR_TOUCH_RIGHT:
				if (((SupportAirtouch) fragment).isSelected()) {
					((SupportAirtouch) fragment).moveCursor(msg.what);
				} else {
					navigationToRight();
				}
				break;
			case Constants.AIR_TOUCH_LEFT:
				if (((SupportAirtouch) fragment).isSelected()) {
					((SupportAirtouch) fragment).moveCursor(msg.what);
				} else {
					navigationToLeft();
				}
				break;
			case Constants.AIR_TOUCH_UP:
				if (((SupportAirtouch) fragment).isSelected()) {
					((SupportAirtouch) fragment).moveCursor(msg.what);
				}
				break;
			case Constants.AIR_TOUCH_DOWN:
				((SupportAirtouch) fragment).moveCursor(msg.what);
				break;
			case Constants.AIR_TOUCH_CLOSE:
				// 靠近
				if (((SupportAirtouch) fragment).isSelected()) {
					((SupportAirtouch) fragment).moveCursor(msg.what);
				}
				break;
			case Constants.AIR_TOUCH_CLOCKWISE:
				if (State.LOCAL.equals(mState)) {
					tv_internet.callOnClick();
				} else if (State.ONLINE.equals(mState)) {
					tv_locala.callOnClick();
				}
				break;
			case Constants.AIR_TOUCH_ANTI_CLOCKWISE:
				if (State.LOCAL.equals(mState)) {
					tv_internet.callOnClick();
				} else if (State.ONLINE.equals(mState)) {
					tv_locala.callOnClick();
				}
				break;
			case Constants.AIR_TOUCH_BYE:
				// 挥三次手
				onBackPressed();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onBackPressed() {
		if (fragment instanceof FolderFragment && ((FolderFragment) fragment).isMovieState()) {
			((FolderFragment) fragment).onBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	/** 切换到下一个ActionBar的选项卡 */
	public void navigationToRight() {
		if (getActionBar().getSelectedNavigationIndex() >= getActionBar().getNavigationItemCount() - 1) {
			getActionBar().setSelectedNavigationItem(0);
		} else {
			getActionBar().setSelectedNavigationItem(getActionBar().getSelectedNavigationIndex() + 1);
		}
	}

	/** 切换到上一个ActionBar的选项卡 */
	public void navigationToLeft() {
		if (getActionBar().getSelectedNavigationIndex() <= 0) {
			getActionBar().setSelectedNavigationItem(getActionBar().getNavigationItemCount() - 1);
		} else {
			getActionBar().setSelectedNavigationItem(getActionBar().getSelectedNavigationIndex() - 1);
		}
	}

	/**
	 * 生成要显示的设置项
	 */
	public void initSettingItems(List<SettingItem> settingItems) {
		SettingItem item = new SettingItem();
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

		item = new SettingItem();
		item.setDrawableId(R.drawable.icn_setting_clear);
		item.setText(getString(R.string.clear_history));
		item.setOnClickListener(new SettingItem.OnClickListener() {
			@Override
			public void onClick(Object arg) {
				MediaStoreOperator.clearBookMark(mContext);
				LocalDataBaseOperator.clearDateLastPlay(mContext);
				Utils.showToast(mContext, mContext.getString(R.string.clear_success));
			}
		});
		settingItems.add(item);

		/** 控制刷新按钮点击事件 */
		if (State.LOCAL.equals(mState) || (fragment != null && fragment instanceof UlifangMovieListFragment)) {
			item = new SettingItem();
			item.setDrawableId(R.drawable.icn_setting_refresh);
			item.setText(getString(R.string.refresh));
			item.setOnClickListener(new SettingItem.OnClickListener() {
				@Override
				public void onClick(Object arg) {
					if (State.LOCAL.equals(mState)) {
						MediaScannerConnection.scanFile(mContext, new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, null, null);
						Utils.showToast(mContext, mContext.getString(R.string.refreshing));
					} else if (State.ONLINE.equals(mState) && fragment != null && fragment instanceof UlifangMovieListFragment) {
						((UlifangMovieListFragment) fragment).updateList();
					}
				}
			});
			settingItems.add(item);
		}

		if (fragment != null && fragment instanceof UlifangMovieListFragment) {
			item = new SettingItem();
			item.setDrawableId(R.drawable.icn_setting_login);
			item.setText(getString(R.string.ulifang_login));
			item.setOnClickListener(new SettingItem.OnClickListener() {
				@Override
				public void onClick(Object arg) {
					Intent intent = new Intent();
					intent.setClass(mContext, LogginActivity.class);
					startActivity(intent);
				}
			});
			settingItems.add(item);
		}
	}

	public OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_local:
				if (State.LOCAL.equals(mState)) {
					return;
				}
				tv_locala.setSelected(true);
				tv_internet.setSelected(false);

				mViewPagerLocal.setVisibility(View.VISIBLE);
				mViewPagerOnline.setVisibility(View.GONE);

				mTabsAdapterLocal.updateActionBarFromAdapter();

				if (mViewPagerLocal.getTag() != null && mViewPagerLocal.getTag() instanceof Integer) {
					mViewPagerLocal.setCurrentItem((Integer) mViewPagerLocal.getTag());
				}

				mState = State.LOCAL;
				break;
			case R.id.tv_internet:
				if (State.ONLINE.equals(mState)) {
					return;
				}
				tv_locala.setSelected(false);
				tv_internet.setSelected(true);

				mViewPagerLocal.setVisibility(View.GONE);
				mViewPagerOnline.setVisibility(View.VISIBLE);

				mTabsAdapterOnline.updateActionBarFromAdapter();

				if (mViewPagerOnline.getTag() != null && mViewPagerOnline.getTag() instanceof Integer) {
					mViewPagerOnline.setCurrentItem((Integer) mViewPagerOnline.getTag());
				}

				mState = State.ONLINE;
				break;
			case R.id.iv_settings:
				mMenuPopupWindow.show(rootView, 0, 220, 450, LayoutParams.WRAP_CONTENT);
				// Intent intent = new Intent();
				// intent.setClass(mContext, SettingsActivity.class);
				// startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private Context mContext;
		private ActionBar mActionBar;
		private ViewPager mViewPager;
		private ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
			private final Tab tab;

			TabInfo(Class<?> _class, Bundle _args, Tab _tab) {
				clss = _class;
				args = _args;
				tab = _tab;
			}
		}

		public TabsAdapter(Activity activity, ViewPager pager) {
			super(activity.getFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args, tab);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			// mActionBar.addTab(tab);
			// notifyDataSetChanged();
		}

		public void updateActionBarFromAdapter() {
			mActionBar.removeAllTabs();
			for (TabInfo info : mTabs) {
				mActionBar.addTab(info.tab);
			}
		}

		// ---------------------------PagerAdapter-----------------
		@Override
		public int getCount() {
			return mTabs.size();
		}

		// ---------------------------FragmentPagerAdapter-------------------------------------
		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		// ---------------------------ViewPager.OnPageChangeListener-------------------------------------
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
			mViewPager.setTag(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		// ---------------------------ActionBar.TabListener-------------------------------------
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
		// ----------------------------------------------------------------
	}

	/** 更新当前的apk */
	private void UpdateApk() {
		new Thread() {
			@Override
			public void run() {
				HttpClient.checkUpdate(VideoFileListActivity.this, callBack);
			}
		}.start();
	}

	/** 让用户选择是否更新应用 */
	private void showUpdateApkDialog(String url) {
		final String apk_url = url;
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.update_title)).setMessage(getString(R.string.update_content))
				.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(VideoFileListActivity.this, DownAPKServiceNotify.class);
						intent.putExtra("APK_url", apk_url);
						intent.putExtra("id", 1);// 序列id,随便填一个数字
						startService(intent);

						dialog.dismiss();
					}
				}).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setCancelable(false).create();

		alertDialog.show();
	}

	/** Http回调接口 */
	private RequestCallBack<String> callBack = new RequestCallBack<String>() {
		/** 连接 */
		@Override
		public void onStart() {
			super.onStart();
			Log.d("yzh", "onStart");
		}

		/** 加载中 */
		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			super.onLoading(total, current, isUploading);
			Log.d("yzh", "onLoading");
		}

		/** 加载成功 */
		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.d("yzh", "onSuccess");
			/** 成功返回字符串 ,json */
			String success = responseInfo.result;// 一般为json
			try {
				JSONObject obj = new JSONObject(success);
				JSONObject jsonObject = obj.getJSONObject("obj");

				UpdateApkInfo apkInfo = new UpdateApkInfo();
				apkInfo.setmAlertTitle(jsonObject.getString("alertTitle"));
				apkInfo.setmAlertContent(jsonObject.getString("alertContent"));
				apkInfo.setmFileSize(jsonObject.getLong("fileSize"));
				apkInfo.setmDownloadUrl(jsonObject.getString("downloadUrl"));
				apkInfo.setmHasUpdate(jsonObject.getInt("hasUpdate"));
				apkInfo.setmNeedForcedUpdate(jsonObject.getInt("needForcedUpdate"));
				apkInfo.setmPublishDate(jsonObject.getString("publishDate"));
				apkInfo.setmVerCode(jsonObject.getString("verCode"));
				apkInfo.setmVerName(jsonObject.getString("verName"));

				/** 处理获取的json数据 */
				if (apkInfo.getmHasUpdate() == 1) {
					Message msg = handler.obtainMessage(UPDATE_APK, apkInfo.getmDownloadUrl());
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		/** 加载失败 */
		@Override
		public void onFailure(HttpException arg0, String arg1) {
		}
	};

	public final static int UPDATE_APK = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_APK:
				String url = (String) msg.obj;
				showUpdateApkDialog(url);
				break;
			default:
				break;
			}
		};
	};
}
