package com.estar.Holography;


public class RenderDrawByC {
	static {
		System.loadLibrary("DrawVideoC");
	}
	
	public static native int drawRender(int vertexPos, int texcoordPos);
	public static native int drawRender2D(int vertexPos, int texcoordPos);
	public static native int drawRender2DR(int vertexPos, int texcoordPos);
	public static native int setPercent(float wper, float hper);
	
	public static native int drawRender2DTop(int vertexPos, int texcoordPos);
	public static native int drawRender2DBottom(int vertexPos, int texcoordPos);
}
