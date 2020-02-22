package com.estar.Holography;

import static com.estar.Holography.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.view.SurfaceView;

import com.estar.video.utils.Utils;

public class Render2DHalf {
    private int mProgram;
    private int maPositionHandle;
    private int maTextureHandle;
    
    String mVertexShader; 	 
    String mFragmentShader;
	FloatBuffer   mVertexBuffer;
	FloatBuffer   mTexCoorBuffer;
	
	float  mAlignY = 0.0f;
    private FloatBuffer mVertices;
    
    int mWidth ;
    int mHeight;
 
    private int muSapler0;
    
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    private static final int VERTICES_DATA_UV_OFFSET = 3;

    private final float[] mVertices3DDataOrign = {
            // X, Y, Z, U, V
    		-1.0f, -1.0f, 0,  0.f, 1.f,
            1.0f, -1.0f, 0,  0.5f, 1.f,
           -1.0f,  1.0f, 0,  0.f, 0.f,
       
           -1.0f,  1.0f, 0,  0.f, 0.f,
           1.0f,   -1.0f, 0,  0.5f, 1.f,
           1.0f,   1.0f, 0,  0.5f, 0.f,
        };
    
    private final float[] mVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0,  0.f, 1.f,
             1.0f, -1.0f, 0,  0.5f, 1.f,
            -1.0f,  1.0f, 0,  0.f, 0.f,
        
            -1.0f,  1.0f, 0,  0.f, 0.f,
            1.0f,   -1.0f, 0,  0.5f, 1.f,
            1.0f,   1.0f, 0,  0.5f, 0.f,
        };
   
    int vCount=6;   
    public Render2DHalf(SurfaceView mv, float wper, float hper) {
    	setPercent(wper, hper);
    	initVertexData();
    	initShader(mv);
    }
    
    public void setPercent(float wper, float hper) {
    	for( int i = 0; i < 6; i++ ) {
    		mVerticesData[5 * i + 0] = mVertices3DDataOrign[5 * i + 0] * wper;
    		mVerticesData[5 * i + 1] = mVertices3DDataOrign[5 * i + 1] * hper;
		}
    }
	
    public void initVertexData()
    {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }
    
    //��ʼ����ɫ��
    public void initShader(SurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex2d2.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag2d2.sh", mv.getResources());  
        mProgram = createProgram(mVertexShader, mFragmentShader);
        
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

    	muSapler0 = GLES20.glGetUniformLocation(mProgram, "Sampler0"); 
    }
    
    public void drawSelf(int texId)
    {        
    	if( mProgram <= 0 ) {
    		Utils.showLogError("no program to draw");
    		return;
    	}
    	GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); 
   	    GLES20.glClearColor(0.f, 0.f, 0.f, 1.0f);
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

       mVertices.position(VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
               VERTICES_DATA_STRIDE_BYTES, mVertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
       checkGlError("glEnableVertexAttribArray maPositionHandle2");
        
       mVertices.position(VERTICES_DATA_UV_OFFSET);
       GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
               VERTICES_DATA_STRIDE_BYTES, mVertices);
       checkGlError("glVertexAttribPointer maTextureHandle2");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
       checkGlError("glEnableVertexAttribArray maTextureHandle2");

       GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
       GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
       GLES20.glUniform1i(muSapler0, 0);
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
 //      GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
    }
    
    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Utils.showLogError(op + ": glError " + error);
        }
    }

}
