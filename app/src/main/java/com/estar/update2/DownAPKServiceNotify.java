package com.estar.update2;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.android.takee.video.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * @Title:DownAPKService.java
 * @Description:专用下载APK文件Service工具类,通知栏显示进度,下载完成震动提示,并自动打开安装界面(配合xUtils快速开发框架)
 * 
 *                                                                             需要添加权限
 *                                                                             ：
 *                                                                             <
 *                                                                             uses
 *                                                                             -
 *                                                                             permission
 *                                                                             android
 *                                                                             :
 *                                                                             name
 *                                                                             =
 *                                                                             "android.permission.INTERNET"
 *                                                                             /
 *                                                                             >
 *                                                                             <
 *                                                                             uses
 *                                                                             -
 *                                                                             permission
 *                                                                             android
 *                                                                             :
 *                                                                             name
 *                                                                             =
 *                                                                             "android.permission.WRITE_EXTERNAL_STORAGE"
 *                                                                             /
 *                                                                             >
 *                                                                             <
 *                                                                             uses
 *                                                                             -
 *                                                                             permission
 *                                                                             android
 *                                                                             :
 *                                                                             name
 *                                                                             =
 *                                                                             "android.permission.VIBRATE"
 *                                                                             /
 *                                                                             >
 * 
 *                                                                             需要在
 *                                                                             <
 *                                                                             application
 *                                                                             >
 *                                                                             <
 *                                                                             /
 *                                                                             application
 *                                                                             >
 *                                                                             标签下注册服务
 * 
 *                                                                             可以在142行代码
 *                                                                             ：
 *                                                                             builder
 *                                                                             .
 *                                                                             setSmallIcon
 *                                                                             (
 *                                                                             R
 *                                                                             .
 *                                                                             drawable
 *                                                                             .
 *                                                                             ic_launcher
 *                                                                             )
 *                                                                             ;
 *                                                                             中修改自己应用的图标
 * 
 */

public class DownAPKServiceNotify extends Service {
	/**
	 * 参数 通知下载 intent.putExtra("apk_url", loadUrl); intent.putExtra("id", 0);
	 */
	public int NotificationID = 0x10000;
	private NotificationManager mNotificationManager = null;
	private Notification.Builder builder;

	// 文件下载路径
	private String APK_url = "";
	// 文件保存路径(如果有SD卡就保存SD卡,如果没有SD卡就保存到手机包名下的路径)
	private String APK_dir = "";

	/**
	 * Title: onBind
	 * 
	 * @Description:
	 * @param intent
	 * @return [url=home.php?mod=space&uid=133757]@see[/url]
	 *         android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Title: onCreate
	 * 
	 * @Description:
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.d("yzh", "onCreate");
		super.onCreate();
		initAPKDir();// 创建保存路径
	}

	/**
	 * Title: onStartCommand
	 * 
	 * @Description:
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 接收Intent传来的参数:
		APK_url = intent.getStringExtra("APK_url");
		NotificationID = intent.getIntExtra("id", 1);
//		String targe_name = APK_dir + File.separator + Md5Util.getMD5ofStr11(APK_url) + ".apk";
		DownFile(APK_url, APK_dir + File.separator + Md5Util.getMD5ofStr11(APK_url) + ".apk", NotificationID);
		return super.onStartCommand(intent, flags, startId);
	}

	private void initAPKDir() {
		/**
		 * 创建路径的时候一定要用[/],不能使用[\],但是创建文件夹加文件的时候可以使用[\].
		 * [/]符号是Linux系统路径分隔符,而[\]是windows系统路径分隔符 Android内核是Linux.
		 */
		if (isHasSdcard())// 判断是否插入SD卡
			APK_dir = getApplicationContext().getFilesDir().getAbsolutePath() + "/apk/download/";// 保存到app的包名路径下
		else
			APK_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/apk/download/";// 保存到SD卡路径下
		File destDir = new File(APK_dir);
		if (!destDir.exists()) {// 判断文件夹是否存在
			destDir.mkdirs();
		}
	}

	/**
	 * @Description:判断是否插入SD卡
	 */
	private boolean isHasSdcard() {
		String status = Environment.getExternalStorageDirectory().getAbsolutePath();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	HttpHandler<File> mDownLoadHelper = null;

	private void DownFile(String file_url, String target_name, final int NotificationID) {
		mDownLoadHelper = new HttpUtils().download(file_url, target_name, true, true, new RequestCallBack<File>() {

			@Override
			public void onStart() {
				super.onStart();
				mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				builder = new Notification.Builder(getApplicationContext());
				builder.setSmallIcon(R.drawable.ic_launcher);
				builder.setContentTitle(getString(R.string.downLoad_notification_title));
				builder.setTicker(getString(R.string.downLoad_notification_ticker));
				builder.setContentText(getString(R.string.downLoad_notification_content1));
				builder.setNumber(0);
				builder.setAutoCancel(true);
				mNotificationManager.notify(NotificationID, builder.build());
				Log.d("yzh", "DownAPKServiceNotify onStart");

			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				int x = Long.valueOf(current).intValue();
				int totalS = Long.valueOf(total).intValue();
				builder.setProgress(totalS, x, false);
				builder.setContentInfo(getPercent(x, totalS));
				mNotificationManager.notify(NotificationID, builder.build());
				Log.d("yzh", "....");
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				System.out.println(responseInfo.result.getPath());
				Uri uri = Uri.fromFile(new File(responseInfo.result.getPath()));
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent mPendingIntent = PendingIntent.getActivity(DownAPKServiceNotify.this, 0, installIntent, 0);
				builder.setContentText(getString(R.string.downLoad_notification_content2));
				builder.setContentIntent(mPendingIntent);
				mNotificationManager.notify(NotificationID, builder.build());
				// 震动提示
				Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(1000L);// 参数是震动时间(long类型)
				stopSelf();
				startActivity(installIntent);// 下载完成之后自动弹出安装界面
				mNotificationManager.cancel(NotificationID);
				Log.d("yzh", "DownAPKServiceNotify onSuccess");
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.e("yzh", "DownAPKServiceNotify onFailure");
				mNotificationManager.cancel(NotificationID);
				Toast.makeText(getApplicationContext(), getString(R.string.downLoad_notification_content3), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled() {
				super.onCancelled();
				mDownLoadHelper.cancel();
			}

		});
	}

	/**
	 * @param x
	 *            当前值
	 * @param total
	 *            总值
	 * @return 当前百分比
	 */
	private String getPercent(int x, int total) {
		String result = "";// 接受百分比的值
		double x_double = x * 1.0;
		double tempresult = x_double / total;
		// 百分比格式，后面不足2位的用0补齐 ##.00%
		DecimalFormat df1 = new DecimalFormat("0.00%");
		result = df1.format(tempresult);
		return result;
	}

	/**
	 * @return
	 * @Description:获取当前应用的名称
	 */
	// private String getApplicationName() {
	// PackageManager packageManager = null;
	// ApplicationInfo applicationInfo = null;
	// try {
	// packageManager = getApplicationContext().getPackageManager();
	// applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
	// } catch (PackageManager.NameNotFoundException e) {
	// applicationInfo = null;
	// }
	// String applicationName = (String)
	// packageManager.getApplicationLabel(applicationInfo);
	// return applicationName;
	// }

	/**
	 * @Description:
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
	}

}
