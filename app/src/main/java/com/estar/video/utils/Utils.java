package com.estar.video.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.estar.lcm3djni;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.takee.video.R;
import com.estar.ulifang.UlifangMovieObject;
import com.estar.video.HarderPlayerActivity;
import com.estar.video.data.VideoObject;

/**
 * 工具类
 * 
 * @author zgl
 * 
 */
public class Utils {
	public static final String TAG = "TAKEE_VideoPlayer";

	/** 打印log Error */
	public static void showLogError(String text) {
		Log.e(TAG, text);
	}

	/** 打印log Debug */
	public static void showLogDebug(String text) {
		Log.d(TAG, text);
	}

	/** 弹Toast提示框 */
	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 创建弹出对话框
	 * 
	 * @return
	 */
	public static Dialog showDialog(Context context, View menuView) {
		Builder builder = new Builder(context);
		builder.setView(menuView);
		AlertDialog dialog = builder.create();
		dialog.show();
		// Dialog dialog = new Dialog(context);
		// dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.setContentView(menuView);
		Window dialogWindow = dialog.getWindow();
		// dialogWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.pop_background));
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.width = 800;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
		return dialog;
	}

	/**
	 * 启动其他应用activity（activity须注册非DEFAULT的intent-filter）
	 * 
	 * @param context
	 *            上下文
	 * @param packageName
	 *            目标应用的包名
	 * @param activityName
	 *            目标activity名
	 * @throws ActivityNotFoundException
	 */
	public static void RunApp(Context context, String packageName, String activityName) throws ActivityNotFoundException {
		ComponentName componentName = new ComponentName(packageName, activityName);
		Intent intent = new Intent();
		intent.setComponent(componentName);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			showLogError("Unable to find activity class.");
			showToast(context, "Unable to find activity class.");
			e.printStackTrace();
			// throw e;
		}
	}

	/** 根据intent的uri判断该视频是否存在于列表中 */
	public static boolean isMediaUri(Uri uri) {
		return "media".equals(uri.getAuthority());
	}

	/**
	 * 格式化视频时长显示
	 * 
	 * @param duration
	 *            毫秒
	 * @return Returns a (localized) string for the given duration (in seconds).
	 */
	public static String formatDuration(int duration) {
		duration = duration / 1000;
		int h = duration / 3600;
		int m = (duration - h * 3600) / 60;
		int s = duration - (h * 3600 + m * 60);
		String durationValue;
		if (h == 0) {
			durationValue = String.format("%1$02d:%2$02d", m, s);
		} else {
			durationValue = String.format("%1$d:%2$02d:%3$02d", h, m, s);
		}
		return durationValue;
	}

	/**
	 * 格式化视频时长显示(历史记录中显示)
	 * 
	 * @param duration
	 *            毫秒
	 * @return Returns a (localized) string for the given duration (in seconds).
	 */
	public static String formatDurationInHistory(Context context, int duration) {
		duration = duration / 1000;
		int h = duration / 3600;
		int m = (duration - h * 3600) / 60;
		int s = duration - (h * 3600 + m * 60);
		String durationValue;
		if (h == 0) {
			durationValue = context.getString(R.string.duration_h_0, m, s);
		} else {
			durationValue = context.getString(R.string.duration_hn_0, h, m, s);
		}
		return durationValue;
	}

	/**
	 * 格式化时间显示
	 * 
	 * @param date
	 *            数字类型日期
	 * @return
	 */
	public static String dateFormat(long date) {
		DateFormat formater = DateFormat.getDateTimeInstance();
		return formater.format(new Date(date));
	}

	/** 3D光栅屏开关控制操作 */
	public static void option3DGuanshan(Activity activity, boolean swih) {
		Utils.showLogDebug("option3DGuanshan " + swih);
		if (swih) {
			lcm3djni.EstarLcm3DOn(activity);
		} else {
			lcm3djni.EstarLcm3DOff(activity);
		}
	}

	/**
	 * 弹框提示需要照相机权限
	 * 
	 * @param activity
	 */
	public static void checkCameraPermission(final Activity activity) {
		final SharedPreferences setting = activity.getSharedPreferences("ESTSDlife.ini", 0);
		Boolean isAllowed = setting.getBoolean("camera_permission", false);
		if (!isAllowed) {

			final LinearLayout contentView = new LinearLayout(activity);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.height = LayoutParams.MATCH_PARENT;
			params.width = LayoutParams.MATCH_PARENT;
			contentView.setLayoutParams(params);
			contentView.setOrientation(LinearLayout.VERTICAL);

			ScrollView sv = new ScrollView(activity);
			LinearLayout.LayoutParams svParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			svParams.height = LayoutParams.WRAP_CONTENT;
			svParams.width = LayoutParams.MATCH_PARENT;
			svParams.weight = 1.0f;
			sv.setLayoutParams(svParams);

			params.height = LayoutParams.WRAP_CONTENT;
			params.width = LayoutParams.MATCH_PARENT;

			final TextView text = new TextView(activity);
			text.setLayoutParams(params);
			text.setTextSize(18);
			text.setTextColor(Color.WHITE);
			text.setText(R.string.need_camera_permission);
			sv.addView(text);
			contentView.addView(sv);

			final CheckBox cb = new CheckBox(activity);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params.height = LayoutParams.WRAP_CONTENT;
			params.width = LayoutParams.MATCH_PARENT;
			cb.setLayoutParams(params);
			cb.setTextSize(14);
			cb.setChecked(true);
			cb.setText(R.string.do_not_show);
			contentView.addView(cb);

			new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.prompt)).setView(contentView)
					.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.finish();
						}
					}).setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (cb.isChecked()) {
								setting.edit().putBoolean("camera_permission", true).commit();
							}
						}
					}).setCancelable(false).show();
		}
	}

	/**
	 * 获取某天零点时间
	 * 
	 * @param whichDay
	 *            0为当天 -1为昨天 1为明天，依次类推
	 * @return
	 */
	public static long getMillisZero(int whichDay) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, whichDay);
		return cal.getTimeInMillis();

		// Date date = new Date();
		// long l = 24*60*60*1000; //每天的毫秒数
		// //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（
		// 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
		// //减8个小时的毫秒值是为了解决时区的问题。
		// return (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
	}

	/** 获取控件相对屏幕的x坐标 */
	public static int getLocationX(View view) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		return x;
	}

	/**
	 * 根据bool值获取应该返回“打开”或关闭
	 * 
	 * @param val
	 *            true返回关闭，false返回打开
	 * @param context
	 * @return
	 */
	public static String getOpenOrCloseStr(boolean val, Context context) {
		return val ? context.getString(R.string.close) : context.getString(R.string.open);
	}

	/**
	 * 删除视频
	 * 
	 * @param videoObject
	 */
	public static void deleteFile(Context context, VideoObject videoObject) {
		File file = new File(videoObject.getPath());
		if (file.exists()) {
			boolean ret = file.delete();
			if (ret) {
				context.getContentResolver().delete(videoObject.getContentUri(), null, null);
				Utils.showLogDebug("delete file success,file name:" + videoObject.getTitle());
			} else {
				Utils.showToast(context, context.getString(R.string.delete_fail));
			}
		} else {
			Utils.showLogDebug("delete file failed,file name:" + videoObject.getTitle());
		}
	}

	/**
	 * 删除视频列表
	 * 
	 * @param videoObject
	 */
	public static void deleteFolder(Context context, List<VideoObject> videos) {
		for (VideoObject videoObject : videos) {
			File file = new File(videoObject.getPath());
			boolean ret = file.delete();
			if (ret) {
				context.getContentResolver().delete(videoObject.getContentUri(), null, null);
				Utils.showLogDebug("delete file success,file name:" + videoObject.getTitle());
			}else{
				Utils.showToast(context, context.getString(R.string.delete_fail));
			}
		}
	}

	/** 读取文本本件 */
	public static String read(File file) throws IOException {
		String res;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int length = fis.available();

			byte[] buffer = new byte[length];

			fis.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return res;
	}

	/** 保存json到文本 */
	public static void save(String text, File file) {
		String str = text;
		PrintWriter pfp;
		try {
			pfp = new PrintWriter(file);
			pfp.print(str);
			pfp.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/** 解析视频列表json数据，保存列表到数组中 */
	public static void readMoviesFromJson(List<UlifangMovieObject> movies, String json) {
		JSONObject obj;
		try {
			obj = new JSONObject(json);
			JSONObject jsonObject = obj.getJSONObject("obj");

			// long fileSize = jsonObject.getLong("pageSize");
			// long fileNo = jsonObject.getLong("pageNo");

			movies.clear();
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				long id = jsonObject.getLong("id");
				String filmName = jsonObject.getString("filmName");
				String snapErected = jsonObject.getString("snapErected");
				String snapTransverse = jsonObject.getString("snapTransverse");
				long time = jsonObject.getLong("time");
				String detail = jsonObject.getString("detail");

				UlifangMovieObject object = new UlifangMovieObject();
				object.setId(id);
				object.setFilmName(filmName);
				object.setSnapErected(snapErected);
				object.setSnapTransverse(snapTransverse);
				object.setTime(time);
				object.setDetail(detail);
				movies.add(object);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/** 从xml文件读取云立方视频数据 */
	public static void readMoviesFromFile(List<UlifangMovieObject> movies, File file) throws XmlPullParserException, IOException {
		FileInputStream fis = new FileInputStream(file);

		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(fis, "UTF-8");// 为破解器添加要解析的XML数据
		int eventType = pullParser.getEventType();// 开始读取，获取事件返回值
		UlifangMovieObject movie = null;
		movies.clear();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				if (pullParser.getName().equals("movie")) {
					movie = new UlifangMovieObject();
					movie.setId(Long.parseLong(pullParser.getAttributeValue(null, "id")));
				} else if (pullParser.getName().equals("filmName")) {
					movie.setFilmName(pullParser.nextText());
				} else if (pullParser.getName().equals("snapTransverse")) {
					movie.setSnapTransverse(pullParser.nextText());
				} else if (pullParser.getName().equals("snapErected")) {
					movie.setSnapErected(pullParser.nextText());
				} else if (pullParser.getName().equals("time")) {
					movie.setTime(Long.parseLong(pullParser.nextText()));
				} else if (pullParser.getName().equals("detail")) {
					movie.setDetail(pullParser.nextText());
				} else if (pullParser.getName().equals("subtitles")) {
					movie.setSubtitles(pullParser.nextText());
				} else if (pullParser.getName().equals("enSubtitles")) {
					movie.setEnSubtitles(pullParser.nextText());
				} else if (pullParser.getName().equals("url_1080p")) {
					movie.setUrl_1080p(pullParser.nextText());
				} else if (pullParser.getName().equals("url_720p")) {
					movie.setUrl_720p(pullParser.nextText());
				} else if (pullParser.getName().equals("url_480p")) {
					movie.setUrl_480p(pullParser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if (pullParser.getName().equals("movie")) {
					movies.add(movie);
				}
				break;
			default:
				break;
			}

			eventType = pullParser.next();
		}
	}

	/** 将云立方视频数据保存到文件 */
	public static void saveListToFile(List<UlifangMovieObject> movies) {
		File file = new File(Constants.ULIFANT_CACHE_LIST_FILE);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			// Utils.save(movies, fos);

			XmlSerializer serializer = Xml.newSerializer();// 获取XML写入信息的序列化对象
			serializer.setOutput(fos, "UTF-8");// 设置要写入的OutputStream
			serializer.startDocument("UTF-8", true);// 设置文档标签
			serializer.startTag(null, "movies");// 设置开始标签，第一个参数为namespace
			for (UlifangMovieObject movie : movies) {
				serializer.startTag(null, "movie");
				serializer.attribute(null, "id", String.valueOf(movie.getId()));
				serializer.startTag(null, "filmName");
				serializer.text(movie.getFilmName());
				serializer.endTag(null, "filmName");
				serializer.startTag(null, "snapTransverse");
				serializer.text(movie.getSnapTransverse());
				serializer.endTag(null, "snapTransverse");
				serializer.startTag(null, "snapErected");
				serializer.text(movie.getSnapErected());
				serializer.endTag(null, "snapErected");
				serializer.startTag(null, "time");
				serializer.text(String.valueOf(movie.getTime()));
				serializer.endTag(null, "time");
				serializer.startTag(null, "detail");
				serializer.text(movie.getDetail());
				serializer.endTag(null, "detail");
				serializer.startTag(null, "subtitles");
				serializer.text(movie.getSubtitles());
				serializer.endTag(null, "subtitles");
				serializer.startTag(null, "enSubtitles");
				serializer.text(movie.getEnSubtitles());
				serializer.endTag(null, "enSubtitles");

				serializer.startTag(null, "urls");
				serializer.startTag(null, "url_1080p");
				serializer.text(movie.getUrl_1080p());
				serializer.endTag(null, "url_1080p");
				serializer.startTag(null, "url_720p");
				serializer.text(movie.getUrl_720p());
				serializer.endTag(null, "url_720p");
				serializer.startTag(null, "url_480p");
				serializer.text(movie.getUrl_480p());
				serializer.endTag(null, "url_480p");
				serializer.endTag(null, "urls");

				serializer.endTag(null, "movie");
			}
			serializer.endTag(null, "movies");
			serializer.endDocument();
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** 播放云立方视频 */
	public static void playVideo(Context context, UlifangMovieObject object) {
		Intent intent = new Intent();
		intent.setClass(context, HarderPlayerActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(Intent.EXTRA_TITLE, object.getFilmName());
		intent.putExtra(Constants.EXTRA_VIDEO_TYPE, VideoObject.VIDEO_TYPE_3D_LR);
		if (object.getUrl_1080p() != null) {
			Utils.showLogDebug(object.getUrl_1080p());
			intent.setDataAndType(Uri.parse(object.getUrl_1080p()), "video/*");
		} else if (object.getUrl_720p() != null) {
			Utils.showLogDebug(object.getUrl_720p());
			intent.setDataAndType(Uri.parse(object.getUrl_720p()), "video/*");
		} else if (object.getUrl_480p() != null) {
			Utils.showLogDebug(object.getUrl_480p());
			intent.setDataAndType(Uri.parse(object.getUrl_480p()), "video/*");
		}
		context.startActivity(intent);
	}

	/** 根据url播放视频 */
	public static void playUrl(Context context, Uri uri) {
		Intent intent = new Intent();
		intent.setClass(context, HarderPlayerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "video/*");
		context.startActivity(intent);
	}

	/**
	 * 通过MD5加密生成MD5的数值
	 * 
	 * @param password
	 * @return
	 */
	public static String makeMD5(String password) {
		MessageDigest md;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(password.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String pwd = new BigInteger(1, md.digest()).toString(16);
			return pwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}

	/**
	 * 根据视频绝对路径获取content uri
	 * 
	 * @return content uri
	 */
	public static Uri getContentUri(Context context, Uri filUri) {
		String filePath = filUri.getPath();
		Uri contentUri = null;
		Cursor cursor = null;
		String column = MediaStore.Video.VideoColumns.DATA;
		try {
			cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, column + "=?", new String[] { filePath }, null);
			if (cursor != null && cursor.moveToFirst()) {
				contentUri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI + "/" + cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)));
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return contentUri;
	}

	/**
	 * 检查当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					showLogDebug(i + "===状态===" + networkInfo[i].getState());
					showLogDebug(i + "===类型===" + networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
