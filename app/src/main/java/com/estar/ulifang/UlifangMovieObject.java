package com.estar.ulifang;

/**
 * 云立方视频对象
 * @author zgl
 *
 */
public class UlifangMovieObject {
	private long id;
	/** 电影名 */
	private String filmName;

	/** 横向海报 */
	private String snapTransverse;
	/** 竖向海报 */
	private String snapErected;
	/** 时长 */
	private long time;

	/** 详细信息 */
	private String detail;
	
	/** 中文字幕 */
	private String subtitles = "";
	/** 英文字幕 */
	private String enSubtitles = "";
	
	private String url_1080p = "";
	private String url_720p = "";
	private String url_480p = "";
	

	public String getSubtitles() {
		return subtitles;
	}

	public void setSubtitles(String subtitles) {
		this.subtitles = subtitles;
	}

	public String getEnSubtitles() {
		return enSubtitles;
	}

	public void setEnSubtitles(String enSubtitles) {
		this.enSubtitles = enSubtitles;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilmName() {
		return filmName;
	}

	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}

	public String getSnapTransverse() {
		return snapTransverse;
	}

	public void setSnapTransverse(String snapTransverse) {
		this.snapTransverse = snapTransverse;
	}

	public String getSnapErected() {
		return snapErected;
	}

	public void setSnapErected(String snapErected) {
		this.snapErected = snapErected;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getUrl_1080p() {
		return url_1080p;
	}

	public void setUrl_1080p(String url_1080p) {
		this.url_1080p = url_1080p;
	}

	public String getUrl_720p() {
		return url_720p;
	}

	public void setUrl_720p(String url_720p) {
		this.url_720p = url_720p;
	}

	public String getUrl_480p() {
		return url_480p;
	}

	public void setUrl_480p(String url_480p) {
		this.url_480p = url_480p;
	}
}
