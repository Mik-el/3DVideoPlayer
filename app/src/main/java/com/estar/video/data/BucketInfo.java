package com.estar.video.data;

/**
 * 文件夹信息
 * @author zgl
 *
 */
public class BucketInfo {
	/** 文件夹ID */
	private long bucketId;
	/** 文件夹显示名称 */
	private String bucket_display_name;
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getBucketId() {
		return bucketId;
	}

	public void setBucketId(long bucketId) {
		this.bucketId = bucketId;
	}

	public String getBucket_display_name() {
		return bucket_display_name;
	}

	public void setBucket_display_name(String bucket_display_name) {
		this.bucket_display_name = bucket_display_name;
	}
}
