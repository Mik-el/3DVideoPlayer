package com.estar.ulifang;

import java.io.File;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

import com.android.takee.video.R;
import com.estar.video.utils.AccountCommunicateUtil;
import com.estar.video.utils.Constants;
import com.estar.video.utils.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 云立方影片item点击事件
 * 
 * @author zgl
 * 
 */
public class UlifangItemClickListener implements OnItemClickListener {
	private List<UlifangMovieObject> mLists;
	private Context mContext;
	private ProgressBar loading_bar;

	public UlifangItemClickListener(List<UlifangMovieObject> lists, Context context, ProgressBar loading_bar) {
		mLists = lists;
		mContext = context;
		this.loading_bar = loading_bar;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position < 0 || position >= mLists.size()) {
			return;
		}
		// final String username = SettingUtils.getUsername(mContext);
		// final String password = SettingUtils.getPassword(mContext);
		final String username = AccountCommunicateUtil.getMobileNo();
		final String password = AccountCommunicateUtil.getPwd();
		final UlifangMovieObject uMovieObject = mLists.get(position);
		if (HttpClient.mCookieStore != null) {
			playObject(uMovieObject);
		} else if (username != null && password != null && !"null".equals(username) && !"null".equals(password)) {
			HttpClient.postLogin(mContext, username, username, password, new RequestLoginCallBack(uMovieObject));
		} else {
			// 如果为登录则跳转至登录界面
			Intent intent = new Intent();
			intent.setClass(mContext, LogginActivity.class);
			mContext.startActivity(intent);
		}
	}

	/** 处理登录请求的回调 */
	private class RequestLoginCallBack extends RequestCallBack<String> {
		private UlifangMovieObject uMovieObject;

		public RequestLoginCallBack(UlifangMovieObject movieObject) {
			this.uMovieObject = movieObject;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			String json = responseInfo.result;
			String ret = null;
			String retMsg = null;

			JSONObject obj;
			try {
				obj = new JSONObject(json);
				ret = obj.getString("ret");
				retMsg = obj.getString("retMsg");
				if ("0000".equals(ret)) {
					retMsg = mContext.getString(R.string.login_success);
					playObject(uMovieObject);
				}
				Utils.showToast(mContext, retMsg);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			DefaultHttpClient dh = (DefaultHttpClient) HttpClient.http.getHttpClient();
			HttpClient.mCookieStore = dh.getCookieStore();
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			Utils.showToast(mContext, mContext.getString(R.string.no_connectivity));
		}
	};

	/** 执行播放视频 */
	private void playObject(final UlifangMovieObject uMovieObject) {
		if (!"".equals(uMovieObject.getUrl_1080p())) {
			// 加载中英文字幕
			String subtitleFilePath = Constants.THUMBNAIL_PATH + uMovieObject.getFilmName() + ".srt";
			if (!"".equals(uMovieObject.getSubtitles()) && !new File(subtitleFilePath).exists()) {
				HttpClient.getSubtitle(uMovieObject.getSubtitles(), subtitleFilePath, new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						Utils.showLogDebug("load subtitle success." + uMovieObject.getFilmName());
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Utils.showLogError("load subtitle failed." + uMovieObject.getFilmName());
					}
				});
			}
			String enSubtitleFilePath = Constants.THUMBNAIL_PATH + "en" + uMovieObject.getFilmName() + ".srt";
			if (!"".equals(uMovieObject.getEnSubtitles()) && !new File(enSubtitleFilePath).exists()) {
				HttpClient.getSubtitle(uMovieObject.getEnSubtitles(), enSubtitleFilePath, new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						Utils.showLogDebug("load enSubtitle success." + uMovieObject.getFilmName());
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Utils.showLogError("load enSubtitle failed." + uMovieObject.getFilmName());
					}
				});
			}
			if (Utils.isNetworkAvailable(mContext)) {
				Utils.playVideo(mContext, uMovieObject);
			} else {
				Utils.showToast(mContext, mContext.getString(R.string.no_connectivity));
			}
		} else {
			HttpClient.requestPlayUrl(mContext, uMovieObject.getId(), new RequestUrlCallBack(uMovieObject));
		}
	}

	/** 获取播放url的请求回调 */
	private class RequestUrlCallBack extends RequestCallBack<String> {
		private final UlifangMovieObject uMovieObject;

		public RequestUrlCallBack(UlifangMovieObject movieObject) {
			this.uMovieObject = movieObject;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			loading_bar.setVisibility(View.GONE);
			String json = responseInfo.result;
			JSONObject obj;
			try {
				obj = new JSONObject(json);

				if ("0000".equals(obj.getString("ret"))) {
					JSONObject jsonObject = obj.getJSONObject("obj");

					JSONArray jsonArray = jsonObject.getJSONArray("cloudVideos");
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObject = jsonArray.getJSONObject(i);

						String subtitles = jsonObject.getString("subtitles");
						String enSubtitles = jsonObject.getString("enSubtitles");
						uMovieObject.setSubtitles(subtitles);
						uMovieObject.setEnSubtitles(enSubtitles);

						int videoType = jsonObject.getInt("videoType");
						if (videoType == 3) {
							JSONArray jsonVideoFormats = jsonObject.getJSONArray("cloudVideoFormats");
							for (int j = 0; j < jsonVideoFormats.length(); j++) {
								if ("480p".equals(jsonVideoFormats.getJSONObject(j).getString("definition"))) {
									uMovieObject.setUrl_480p(jsonVideoFormats.getJSONObject(j).getString("fileUrl"));
								} else if ("720p".equals(jsonVideoFormats.getJSONObject(j).getString("definition"))) {
									uMovieObject.setUrl_720p(jsonVideoFormats.getJSONObject(j).getString("fileUrl"));
								} else if ("1080p".equals(jsonVideoFormats.getJSONObject(j).getString("definition"))) {
									uMovieObject.setUrl_1080p(jsonVideoFormats.getJSONObject(j).getString("fileUrl"));
								}
							}
							playObject(uMovieObject);
							break;
						}
					}
					Utils.saveListToFile(mLists);
				} else {
					Utils.showToast(mContext, obj.getString("retMsg"));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			loading_bar.setVisibility(View.GONE);
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			loading_bar.setVisibility(View.GONE);
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			super.onLoading(total, current, isUploading);
			loading_bar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onStart() {
			super.onStart();
			loading_bar.setVisibility(View.VISIBLE);
		}
	};
}
