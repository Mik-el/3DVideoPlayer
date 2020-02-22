package com.estar.update2;

/**获取云立方服务器上是否有更新的apk的相关信息*/

public class UpdateApkInfo {
	/**用户提示标题 */
	private String mAlertTitle;
	/**用户提示内容*/
	private String mAlertContent;
	/**是否有更新 1:有; 0:没有.*/
	private int mHasUpdate;
	/**是否强制更新  1:有; 0:没有.*/
	private int mNeedForcedUpdate;
	/**新APK Url地址*/
	private String mDownloadUrl;
	/**新APK 版本号*/
	private String mVerCode;
	/**新APK 版本名*/
	private String mVerName;
	/**新APK 文件大小*/
	private long  mFileSize;
	/**新APK 公共数据*/
	private String mPublishDate;
	
	
	public String getmAlertTitle() {
		return mAlertTitle;
	}
	public void setmAlertTitle(String mAlertTitle) {
		this.mAlertTitle = mAlertTitle;
	}
	public String getmAlertContent() {
		return mAlertContent;
	}
	public void setmAlertContent(String mAlertContent) {
		this.mAlertContent = mAlertContent;
	}
	public int getmHasUpdate() {
		return mHasUpdate;
	}
	public void setmHasUpdate(int mHasUpdate) {
		this.mHasUpdate = mHasUpdate;
	}
	public int getmNeedForcedUpdate() {
		return mNeedForcedUpdate;
	}
	public void setmNeedForcedUpdate(int mNeedForcedUpdate) {
		this.mNeedForcedUpdate = mNeedForcedUpdate;
	}
	public String getmDownloadUrl() {
		return mDownloadUrl;
	}
	public void setmDownloadUrl(String mDownloadUrl) {
		this.mDownloadUrl = mDownloadUrl;
	}
	public String getmVerCode() {
		return mVerCode;
	}
	public void setmVerCode(String mVerCode) {
		this.mVerCode = mVerCode;
	}
	public String getmVerName() {
		return mVerName;
	}
	public void setmVerName(String mVerName) {
		this.mVerName = mVerName;
	}
	public long getmFileSize() {
		return mFileSize;
	}
	public void setmFileSize(long mFileSize) {
		this.mFileSize = mFileSize;
	}
	public String getmPublishDate() {
		return mPublishDate;
	}
	public void setmPublishDate(String mPublishDate) {
		this.mPublishDate = mPublishDate;
	}
	
	
	
}
