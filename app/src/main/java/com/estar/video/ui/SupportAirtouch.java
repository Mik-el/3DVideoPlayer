package com.estar.video.ui;

public interface SupportAirtouch {
	/** 是否处于空中触控选择下 */
	public boolean isSelected();
	/** 空中触控更改选择项 */
	public void moveCursor(int action);
}
