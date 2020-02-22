package com.estar.Holography;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.estar.video.ui.MovieGLSurfaceView;

public class RenderVideo extends RenderBase{

	public float[] mVerticesDataFullT = {
            // X, Y, Z
            -1.0f, -1.0f, 0, 
            1.0f, -1.0f, 0, 
            -1.0f,  1.0f, 0,
            1.0f,   1.0f, 0,
        };
	
	public final float[] mVerticesDataFullV = {
            // U, V
            0.f, 0.f,
            1f, 0.f,
            0.f, 1.f,
            1f, 1.f,
        };

//	public final float[] mVerticesDataHalf = {
//            // X, Y, Z, U, V
//            -1.0f, -1.0f, 0, 0.f, 0.f,
//            1.0f, -1.0f, 0, 0.5f, 0.f,
//            -1.0f,  1.0f, 0, 0.f, 1.f,
//            1.0f,   1.0f, 0, 0.5f, 1.f,
//    };
	
	
	
    public RenderVideo(MovieGLSurfaceView mv,SurfaceTexture.OnFrameAvailableListener ol, float wper, float hper){
//    	initVertexData();   
    	initShader(mv);
//    	initTexture();
    	setPercent(wper,hper);
//    	mSurface = new SurfaceTexture(mTextureID);
//        mSurface.setOnFrameAvailableListener(ol);
        
    }
    
    public void setPercent(float wper, float hper) {
    	RenderDrawByC.setPercent(wper, hper);
    }
    
    boolean mis2d = false;
    
    public void setIs2D(boolean is2d) {
    	mis2d = is2d;
    }
	
    public void initVertexData()
    {
        mVerticesFullT = ByteBuffer.allocateDirect(mVerticesDataFullT.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesFullT.put(mVerticesDataFullT).position(0);
        mCurrentVerticesT = mVerticesFullT;
        
        mVerticesFullV = ByteBuffer.allocateDirect(mVerticesDataFullV.length
        		* FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesFullV.put(mVerticesDataFullV).position(0);
        mCurrentVerticesV = mVerticesFullV;
    }
    
    public void initShader(MovieGLSurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex2d.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag2d.sh", mv.getResources());  
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
    }

    public void drawSelfLeft()
    {  
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
		
        RenderDrawByC.drawRender2D(maPositionHandle, maTextureHandle);
        
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
        
    }
    
    
    public void drawSelfRight()
    {  
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
		
        RenderDrawByC.drawRender2DR(maPositionHandle, maTextureHandle);
        
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
        
    }
    
    public void drawSelfTop()
    {  
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
		
        RenderDrawByC.drawRender2DTop(maPositionHandle, maTextureHandle);
        
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
    }
    
    public void drawSelfBottom()
    {  
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
		
        RenderDrawByC.drawRender2DBottom(maPositionHandle, maTextureHandle);
        
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
    }
    
	@Override
    public void drawSelf()
    {        
        checkGlError("glFramebufferRenderbuffer");
        
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        
        if( mis2d ) {
        	RenderDrawByC.drawRender2D(maPositionHandle, maTextureHandle);
        }
        else {
        	RenderDrawByC.drawRender(maPositionHandle, maTextureHandle);
        }
        

        /*
        mCurrentVerticesT.position(0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_T_STRIDE_BYTES, mCurrentVerticesT);
        checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        
        mCurrentVerticesV.position(0);
        GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
	             VERTICES_DATA_V_STRIDE_BYTES, mCurrentVerticesV);

        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");
//		*/        
        
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");

    }
	@Override
    public void updateSurface(){
        mSurface.updateTexImage();
        mSurface.getTransformMatrix(mSTMatrix);
    }
	
	
}
