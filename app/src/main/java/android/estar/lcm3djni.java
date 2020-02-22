package android.estar;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

public class lcm3djni {
	/** 是否加载了硬解码判断库 */
	private static boolean has_loaded_v3_flag = false;

	public static boolean hasLoadedV3() {
		return has_loaded_v3_flag;
	}

	static {
		try {
			System.loadLibrary("3djni");
			System.loadLibrary("estar_v1");
			System.loadLibrary("estar_v2");// 软解码

			System.loadLibrary("estar_vdpc_check");

			//暂时规避T2报错
			if(Build.VERSION.SDK_INT <= 20){
				try {
					System.loadLibrary("estar_v3"); // 硬解码
					native_hardwareDecode_init();
					has_loaded_v3_flag = true;
				} catch (UnsatisfiedLinkError e) {
					Log.e("lcm3djni", "estar_v3 doesn't exist.");
					e.printStackTrace();
				}
			}

		} catch (UnsatisfiedLinkError ule) {
			System.err.println("WARNING: Could not load lib3djni.so");
			ule.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static int EstarLcm3DOn() // synchronized
	{
		int result = nativeestarScreen3dOn();
		return result;

	}

	public synchronized static int EstarLcm3DOn(int mode) // synchronized
	{
		int result = nativeestarScreen3dOn();
		return result;

	}

	public synchronized static int EstarLcm3DOff() {
		int result = nativeestarScreen3dOff();
		return result;
	}

	public synchronized static int EstarLcm3DOn(Activity activity) // synchronized
	{
		int result = nativeestarScreen3dOn();
		// LightManage.setOption3FBrightness(activity, true);
		return result;

	}

	public synchronized static int EstarLcm3DOff(Activity activity) {
		int result = nativeestarScreen3dOff();
		// LightManage.setOption3FBrightness(activity, false);
		return result;
	}

	public static native int nativeestarCamera3D();

	public static native int nativeestarCamera2D();

	private static native int nativeestarScreen3dOn();

	private static native int nativeestarScreen3dOff();

	public static native int natvie_estarIsPic3D(int[] pBuffer, int width, int height);

	/**
	 * 软解码
	 * @param filen_name
	 *            传入文件绝对路径
	 * @return -1为出错 ; 0为2d; 1:3D视频左右格式 ; 2:3D视频上下格式;
	 */
	public static native int natvie_estarIsVideo3D_filename(String filen_name);

	/**
	 * 
	 * @param filen_name
	 *            传入文件绝对路径
	 * @return -1:出错 ; 1:为非4K2K视频: 2:是4K2K视频
	 */
	public static native int natvie_estarIsVideo4K2K(String filen_name);

	/**
	 * 视频硬解码初始化
	 * 
	 * @return -1: 出错; 1: 正确
	 * */
	private static native int native_hardwareDecode_init();

	/**
	 * 视频硬解码设置datasource
	 * 
	 * @param file_name
	 *            : 视频文件的绝对路径
	 * @return -1:出错; 1:正确
	 * */
	public static native int native_hardwareDecode_setDataSource(String file_name);

	/**
	 * 视频硬解码
	 * 
	 * @return -2:该视频不能播放; -1:出错; 0:2D视频 ; 1:3D视频左右格式 ; 2:3D视频上下格式;
	 * */
	public static native int native_hardwareDecode_Is3D();

	/**
	 * 视频硬解码release
	 * */
	private static native int native_hardwareDecode_release();

	@Override
	protected void finalize() throws Throwable {
		try {
			if (has_loaded_v3_flag) {
				native_hardwareDecode_release();
			}
		} finally {
			super.finalize();
		}
	}
}