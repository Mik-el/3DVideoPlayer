package com.estar.ulifang;

/**
 * http请求地址
 */
public class HttpUrls {
	/** 服务 ip */
	private static String server_ip = "http://api.ulifang.com/android";

	/** 获取列表 */
	public final static String ULIFANG_VIDEO__URL = server_ip + "/filmstore/film/list";
	/** 登录 */
	public final static String ULIFANG_LOGIN_URL = server_ip + "/user/login";
	/** 获取播放链接 */
	public final static String ULIFANG_GET_PLAY_URL = server_ip + "/filmstore/video/film/";
}
