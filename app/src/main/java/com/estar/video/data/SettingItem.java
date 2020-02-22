package com.estar.video.data;

/**
 * 封装设置项
 * 
 * @author zgl
 * 
 */
public class SettingItem {
	private OnClickListener mOnClickListener;
	private int drawableId;
	private String text;

	/** 获取图标 */
	public int getDrawableId() {
		return drawableId;
	}

	/** 设置图标 */
	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

	/** 获取设置项文本  */
	public String getText() {
		return text;
	}

	/** 设置项文本 */
	public void setText(String text) {
		this.text = text;
	}
	
	/** 设置点击回调方法 */
	public void setOnClickListener(OnClickListener onClickListener){
		this.mOnClickListener = onClickListener;
	}
	
	/** 设置项被点击 */
	public void click(Object arg){
		mOnClickListener.onClick(arg);
	}

	/** 设置点击回调 */
	public interface OnClickListener {
		public void onClick(Object arg);
	}
}
