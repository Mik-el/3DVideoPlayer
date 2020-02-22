package com.estar.ulifang;

import java.io.File;

import org.apache.http.client.CookieStore;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.estar.video.utils.Utils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 封装xutils http请求
 * @author zgl
 *
 */
public class HttpClient {

	// 线程池数量
	public final static int maxDownloadThread = 3;
	
	public static CookieStore mCookieStore = null;

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

	public static String initImeiAndMac(Context mContext) {
		TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	public final static HttpUtils http = new HttpUtils();

	static {
		http.configRequestThreadPoolSize(maxDownloadThread);
	}

	/***
	 * 对应应用的 appkey
	 */
	public final static String Appkey = "c10cdd666ca148c6a9bd84946ecc38e3";

	/**
	 * 添加头文件
	 * 
	 * @param context
	 * @param http
	 * @param params
	 */
	private static void AddHeaderCookie(Context context, HttpUtils http, RequestParams params) {
		/*** header 联网超时时间 ****/
		http.configTimeout(2000);
		String model = Build.MODEL;// 设备型号
		@SuppressWarnings("deprecation")
		String version = android.os.Build.VERSION.SDK;
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String macAddress = wifi.getConnectionInfo().getMacAddress(); // 无线网卡地址
		params.addHeader("appKey", Appkey);// appKey ,必须的用当前应用对应的 appKey,可以从后台找到
		params.addHeader("deviceKey", "");
		params.addHeader("carrier", "");
		params.addHeader("access", "WIFI");
		params.addHeader("verCode", getVerCode(context) + "");// 版本号
		params.addHeader("imei", initImeiAndMac(context));// ime号
		params.addHeader("dvc", model);// 设备型号
		params.addHeader("aver", version);// Android版本号
		params.addHeader("mac", macAddress);// 网卡
	}

	/**
	 * 发送请求获取视频列表
	 * 
	 * @param context
	 * @param callBack
	 *            回调
	 */
	public static void getVideoList(Context context, RequestCallBack<String> callBack) {
		RequestParams params = new RequestParams();
		AddHeaderCookie(context, http, params);

		params.addBodyParameter("pageNo", "1");
		params.addBodyParameter("pageSize", "20");

		http.send(HttpMethod.POST, HttpUrls.ULIFANG_VIDEO__URL, params, callBack);
	}

	/**
	 * 发送登录请求
	 * 
	 * @param context
	 * @param callBack
	 *            回调
	 */
	public static void postLogin(Context context, String username, String mobileNo, String password, RequestCallBack<String> callBack) {
		RequestParams params = new RequestParams();
		AddHeaderCookie(context, http, params);

		params.addBodyParameter("username", username);
		params.addBodyParameter("mobileNo", mobileNo);
		params.addBodyParameter("password", password);

		http.send(HttpMethod.POST, HttpUrls.ULIFANG_LOGIN_URL, params, callBack);
	}
	
	/**
	 * 获取播放url
	 * 
	 * @param context
	 * @param callBack
	 *            回调
	 */
	public static void requestPlayUrl(Context context, long id, RequestCallBack<String> callBack) {
		RequestParams params = new RequestParams();
		AddHeaderCookie(context, http, params);

		String url = HttpUrls.ULIFANG_GET_PLAY_URL + id;
		Utils.showLogDebug("requestPlayUrl,request url : " + url);
		if(mCookieStore != null){
			http.configCookieStore(mCookieStore);
		}
		http.send(HttpMethod.POST, url, params, callBack);
	}
	
	/**
	 * 获取字幕文件
	 * @param context
	 * @param url
	 * @param callBack
	 */
	public static void getSubtitle(String url,String target,RequestCallBack<File> callBack){
//		RequestParams params = new RequestParams();
//		AddHeaderCookie(context, http, params);
//		http.send(HttpMethod.POST, url, params, callBack);
		http.download(url, target, callBack);
	}
}
