package com.estar.Holography;

import android.opengl.GLES20;

import com.estar.video.utils.Utils;

public class FrameBufferOBJ {
	private int mMidFrameBuffer;
	private int mMidTexture;
	private int mMidRenderBuffer;
	
	public FrameBufferOBJ(int w,int h){
        int[] tmp = new int[1];
        GLES20.glGenTextures(1, tmp, 0);
        mMidTexture = tmp[0];
        GLES20.glGenFramebuffers(1, tmp, 0);	 
        mMidFrameBuffer = tmp[0];
        GLES20.glGenRenderbuffers(1, tmp, 0);
        mMidRenderBuffer = tmp[0];
        
        Utils.showLogDebug("init FrameBuffer:" + w +" " +h);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mMidTexture);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, w, h, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE); 
    	
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mMidRenderBuffer);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, w, h);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        	

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mMidFrameBuffer); 
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D, mMidTexture, 0);    	
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,GLES20. GL_DEPTH_ATTACHMENT,GLES20.GL_RENDERBUFFER, mMidRenderBuffer);
   
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); 
	}
	public void used(){
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mMidFrameBuffer); 
	}
	
	public void unused(){
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); 
	}
	
	public int getTexture() {
		// TODO Auto-generated method stub
		return mMidTexture;
	}
	public int getFBO() {
		// TODO Auto-generated method stub
		return mMidFrameBuffer;
	}
	public void release() {
		// TODO Auto-generated method stub
		 int[] tmp = new int[1];
		 
		 tmp[0] = mMidTexture;
		GLES20.glDeleteTextures(1, tmp,0); 
		 
		 tmp[0] = mMidRenderBuffer;
		GLES20.glDeleteRenderbuffers(1, tmp,0);
		
		 tmp[0] = mMidFrameBuffer;
		GLES20.glDeleteFramebuffers(1, tmp,0);

	}
}
