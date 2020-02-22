package com.estar.Holography;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import com.estar.video.ui.MovieGLSurfaceView;
import com.estar.video.utils.Utils;

public class RenderCamera extends RenderBase{



    
    
	public final float[] mVerticesDataFull = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1f, 0.f,
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,   1.0f, 0, 1f, 1.f,
        };
	

	
    public RenderCamera(MovieGLSurfaceView mv,SurfaceTexture.OnFrameAvailableListener ol){
    	Utils.showLogDebug("RenderCamera");
    	initVertexData();   
    	initShader(mv);
    	initTexture();

    	mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(ol);
        
    }
	
    public void initVertexData()
    {
        mVerticesFull = ByteBuffer.allocateDirect(mVerticesDataFull.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesFull.put(mVerticesDataFull).position(0);

        mCurrentVertices = mVerticesFull;
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

        mCurrentVertices.position(VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_STRIDE_BYTES, mCurrentVertices);
        checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
        
        mCurrentVertices.position(VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
	             VERTICES_DATA_STRIDE_BYTES, mCurrentVertices);

        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");
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
