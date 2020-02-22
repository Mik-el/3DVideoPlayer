package com.estar.video.data;

import android.content.ContentUris;
import android.net.Uri;

import com.estar.video.utils.Constants;

/**
 * 视频对象
 * 
 * @author zgl
 * 
 */
public class VideoObject {
	/** 没有判断视频的类型 */
	public static final int VIDEO_TYPE_UN_CHECK = -2;
	/** 视频的类型为2D */
	public static final int VIDEO_TYPE_2D = 0;
	/** 视频的类型为左右3D */
	public static final int VIDEO_TYPE_3D_LR = 1;
	/** 视频的类型为上下3D */
	public static final int VIDEO_TYPE_3D_UD = 2;
	
	/** 未检测过 */
	public final static int CONVERGENCE_UN_CHECK = -1;
	/** 无收敛性 */
	public final static int CONVERGENCE_NONE = 0;
	/** 半宽或半高 */
	public final static int CONVERGENCE_HALF = 1;

	private long id;
	private long bucketId;
	private String bucket_display_name;
	private String resolution;
	private String path;
	private String title;
	private String mimeType;

	private long duration;
	private long dateTaken;
	private long dateModified;
	private long dateAdded;
	private long size;
	private long bookMark = 0;

	public long getBookMark() {
		return bookMark;
	}

	public void setBookMark(long bookMark) {
		this.bookMark = bookMark;
	}

	/** MediaStore保存是否3D字段，-2：未判断；0：2d；1：左右3d; 2上下3d */
	private int videoType = VIDEO_TYPE_UN_CHECK;
	/** 存储图片收敛性 0：无收敛性；1：半宽；2:半高 */
	private int convergence = CONVERGENCE_UN_CHECK;

	public long getDateLastPlay() {
		return dateLastPlay;
	}

	public void setDateLastPlay(long dateLastPlay) {
		this.dateLastPlay = dateLastPlay;
	}

	private long dateLastPlay = 0;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getVideoType() {
		return videoType;
	}

	public boolean is3dVideo() {
		boolean ret = false;
		switch (videoType) {
		case VIDEO_TYPE_3D_LR:
			ret = true;
			break;
		case VIDEO_TYPE_3D_UD:
			ret = true;
			break;
		default:
			break;
		}
		return ret;
	}
	
	/** 判断传入的VideoType是否是3D类型 */
	public static boolean isVideoType3d(int videoType){
		return videoType == VIDEO_TYPE_3D_LR || videoType == VIDEO_TYPE_3D_UD;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBucketId() {
		return bucketId;
	}

	public void setBucketId(long bucketId) {
		this.bucketId = bucketId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(long dateTaken) {
		this.dateTaken = dateTaken;
	}

	public long getDateModified() {
		return dateModified;
	}

	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(long dateAdded) {
		this.dateAdded = dateAdded;
	}

	public void setConvergence(int convergence) {
		this.convergence = convergence;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getConvergence() {
		return convergence;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getBucket_display_name() {
		return bucket_display_name;
	}

	public void setBucket_display_name(String bucket_display_name) {
		this.bucket_display_name = bucket_display_name;
	}

	/** 获取Content uri(MediaStore的Uri) */
	public Uri getContentUri() {
		return ContentUris.withAppendedId(Constants.VIDEO_URI, id);
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}
}
