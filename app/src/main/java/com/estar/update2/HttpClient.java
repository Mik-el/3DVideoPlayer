package com.estar.update2;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HttpClient {
	
	//线程池数量
	public final static int maxDownloadThread = 3;

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

	public static String initImeiAndMac(Context mContext) {
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
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
	public final static String Appkey = "17529a3da9364df8babe7a37bdacb15d";
	
	/**
	 * 添加头文件
	 * @param context
	 * @param http
	 * @param params
	 */
	private static void AddHeaderCookie(Context context, HttpUtils http, RequestParams params) {
		/*** header  联网超时时间****/
		http.configTimeout(3000);
		String model = Build.MODEL;// 设备型号
		@SuppressWarnings("deprecation")
		String version = android.os.Build.VERSION.SDK;
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String macAddress = wifi.getConnectionInfo().getMacAddress(); // 无线网卡地址
		params.addHeader("appKey", Appkey);//appKey ,必须的用当前应用对应的 appKey,可以从后台找到.到时发给你
		params.addHeader("deviceKey", "");
		params.addHeader("carrier", "");
		params.addHeader("access", "WIFI");
		params.addHeader("verCode", getVerCode(context) + "");//版本号
		params.addHeader("imei", initImeiAndMac(context));//ime号
		params.addHeader("dvc", model);// 设备型号
		params.addHeader("aver", version);// Android版本号
		params.addHeader("mac", macAddress);// 网卡
	}

	/**
	 * 
	 * @param context
	 * @param callBack 回调
	 */
	public static void checkUpdate(Context context,RequestCallBack<String> callBack){
		RequestParams params = new RequestParams();
		AddHeaderCookie(context, http, params);
		http.send(HttpMethod.POST, HttpUrls.takee_update, params, callBack);
	}
	
}
