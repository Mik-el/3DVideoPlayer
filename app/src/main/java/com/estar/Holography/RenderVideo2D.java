package com.estar.Holography;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.estar.video.ui.MovieGLSurfaceView;
import com.estar.video.utils.Utils;

public class RenderVideo2D extends RenderBase{

	public final float[] mVerticesDataHalfT = {
            // X, Y, Z
            -1.0f, -1.0f, 0,
            1.0f, -1.0f, 0, 
            -1.0f,  1.0f, 0,
            1.0f,   1.0f, 0, 
    };
	public final float[] mVerticesDataHalfV = {
            // U, V
            0.f, 0.f,
            0.5f, 0.f,
            0.f, 1.f,
            0.5f, 1.f,
    };
	
    public RenderVideo2D(MovieGLSurfaceView mv,SurfaceTexture.OnFrameAvailableListener ol){
    	Utils.showLogDebug("RenderVideo2D");
//    	initVertexData();   
    	initShader(mv);
    }
	
    public void initVertexData()
    {              
        mVerticesHalfT = ByteBuffer.allocateDirect(mVerticesDataHalfT.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVerticesHalfT.put(mVerticesDataHalfT).position(0);
		mCurrentVerticesT = mVerticesHalfT;
		
		mVerticesHalfV = ByteBuffer.allocateDirect(mVerticesDataHalfV.length
				* FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVerticesHalfV.put(mVerticesDataHalfV).position(0);
		mCurrentVerticesV = mVerticesHalfV;
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

	@Override
    public void drawSelf()
    {        
        checkGlError("glFramebufferRenderbuffer");
        
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        
        RenderDrawByC.drawRender2D(maPositionHandle, maTextureHandle);

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
