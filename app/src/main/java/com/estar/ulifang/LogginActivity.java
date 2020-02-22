package com.estar.ulifang;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.takee.video.R;
import com.estar.video.utils.AccountCommunicateUtil;
import com.estar.video.utils.SettingUtils;
import com.estar.video.utils.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 云立方登录界面
 * 
 * @author zgl
 * 
 */
public class LogginActivity extends Activity {
	private Context mContext;
	private EditText et_account, et_password;
	private Button btn_login;
	private TextView tv_back,tv_register,tv_forget_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loggin);
		mContext = this;

		et_account = (EditText) findViewById(R.id.et_account);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);

		btn_login.setOnClickListener(mOnClickListener);
		tv_register.setOnClickListener(mOnClickListener);
		tv_back.setOnClickListener(mOnClickListener);
		tv_forget_password.setOnClickListener(mOnClickListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.btn_login:
				final String username = et_account.getText().toString().trim();
				final String mobileNo = et_account.getText().toString().trim();
				final String password = et_password.getText().toString().trim();
				if ("".equals(username) || "".equals(password)) {
					Utils.showToast(mContext, mContext.getString(R.string.please_enter_complete_information));
				} else {
					HttpClient.postLogin(mContext, username, mobileNo, password, new RequestCallBack<String>() {

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
									SettingUtils.saveUserInfo(mContext, username, password);
									
									AccountCommunicateUtil.saveAccount(username, password);
									finish();
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
					});
				}
				break;
			case R.id.tv_register:
				Utils.RunApp(mContext, "com.estarCool", "com.estarCool.RegisterActivity");
				break;
			case R.id.tv_forget_password:
				Utils.RunApp(mContext, "com.estarCool", "com.estarCool.ForgetPswdsActivity");
				break;
			default:
				break;
			}
		}
	};
}
