package com.estar.video.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class AccountCommunicateUtil {

	public static final String ROOT_DIR = "yunlifang";
	public static final String COOKIE_DIR = "account";
	public static final String filePath = getDir(COOKIE_DIR) + "cookie.prop";
	// 私钥
	public static final String PRIVATEKEY = "yunlifang";
	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
	// 账号密码key
	public static final String KEY1 = "mobileNo";
	public static final String KEY2 = "password";
	public static final String comment = "Account";

	public static boolean saveAccount(String mobileNo, String pwd) {
		writeProperties(encode(PRIVATEKEY, mobileNo), encode(PRIVATEKEY, pwd));
		return true;
	}

	public static String getMobileNo() {
		return decode(PRIVATEKEY, getProperty(KEY1));
	}

	public static String getPwd() {
		return decode(PRIVATEKEY, getProperty(KEY2));
	}

	public static String getDir(String name) {
		StringBuilder sb = new StringBuilder();
		if (isSDCardAvailable()) {
			sb.append(getExternalStoragePath());
		}
		sb.append(name);
		sb.append(File.separator);
		String path = sb.toString();
		if (createDirs(path)) {
			return path;
		} else {
			return null;
		}
	}

	/** 创建文件夹 */
	public static boolean createDirs(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists() || !file.isDirectory()) {
			return file.mkdirs();
		}
		return true;
	}

	/** 获取SD下的应用目录 */
	public static String getExternalStoragePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
		sb.append(File.separator);
		sb.append(ROOT_DIR);
		sb.append(File.separator);
		return sb.toString();
	}

	public static boolean isSDCardAvailable() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}

	public static void writeProperties(String value1, String value2) {
		if (TextUtils.isEmpty(value1) || TextUtils.isEmpty(value2)) {
			return;
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File f = new File(filePath);
		Log.d("yangxing", "进入writeProperties1！！");
		try {
			if (!f.exists() || !f.isFile()) {
				f.createNewFile();
			}
			Log.d("yangxing", "进入writeProperties2！！");
			fis = new FileInputStream(f);
			Properties p = new Properties();
			p.load(fis);// 先读取文件，再把键值对追加到后面
			// if(p.containsKey(KEY)){
			// p.remove(KEY);
			// }
			Log.d("yangxing", "进入writeProperties3！！");
			p.setProperty(KEY1, value1);
			p.setProperty(KEY2, value2);
			fos = new FileOutputStream(f);
			p.store(fos, comment);
			Log.d("yangxing", "进入writeProperties4！！");
		} catch (Exception e) {
			Log.d("yangxing", "writeProperties出错！！");
		} finally {
			Log.d("yangxing", "进入writeProperties5！！");
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.d("yangxing", "进入writeProperties6！！");
		}
	}

	public static Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// 读取files目录下的config
			// fis = activity.openFileInput(APP_CONFIG);

			// 读取app_config目录下的config
			// File dirConf = UIUtils.getContext().getDir("config",
			// Context.MODE_PRIVATE);
			File Conf = new File(filePath);
			if (!Conf.exists()) {
				Conf.createNewFile();
			}
			fis = new FileInputStream(Conf);
			props.load(fis);
		} catch (Exception e) {
			Log.d("yangxing", "get()出错！！");
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				Log.d("yangxing", "get()出错！！");
			}
		}
		return props;
	}

	public static String getProperty(String key) {
		Properties props = get();
		return (props != null) ? props.getProperty(key) : null;
	}

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws InvalidAlgorithmParameterException
	 * @throws Exception
	 */
	public static String encode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] bytes = cipher.doFinal(data.getBytes());
			return byte2hex(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * DES算法，解密
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static String decode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return new String(cipher.doFinal(hex2byte(data.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}

	public static void clearAll(){
		File file=new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}
	/**
	 * 二行制转字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byte2hex(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

	private static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException();
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
}
