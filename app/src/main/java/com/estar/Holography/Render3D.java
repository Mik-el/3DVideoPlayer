package com.estar.Holography;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.util.Log;
import android.view.SurfaceView;

public class Render3D {
	public String  TAG = "Holography";
    private int mProgram;
    private int maPositionHandle;
    private int maTextureHandle;
    
    private int muSapler0;
    private int muSapler1;

    int    posTexture1;
    
    float delt_s = 40.0f/255.0f;
    
    String mVertexShader;
    String mFragmentShader;

    private FloatBuffer mVertices;
    
    
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    private static final int VERTICES_DATA_UV_OFFSET = 3;
    
    private final float[]  mVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
             1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f, 1.0f, 0, 0.f, 1.f,
            
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,   -1.0f, 0, 1.f, 0.f,
            1.0f,   1.0f, 0, 1.f, 1.f,};
    
    int vCount=6;   
    
    public Render3D(SurfaceView sv, int lrsx ){
    	
    	initVertexData();
    	
    	if( lrsx == 0 ) {
    		initShader(sv);
    	}
    	else {
    		initShaderSX(sv);
    	}
    	
    	posTexture1 = ShaderUtil.initTexture();
    	
   // 	Holography.setTexture(posTexture1);
    	
    	updateDelt();
    }
	

    public void initVertexData()
    {
    	Log.d(TAG,"initVertexData");
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
         
    }
    
    public void initShaderSX(SurfaceView sv)
    {
    	Log.d(TAG,"initShader");
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex3D.sh", sv.getResources() );
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag3Dsx.sh", sv.getResources() );  
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
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
        if (muSapler0 == -1) {
        	throw new RuntimeException("Could not get attrib location for muSapler0");
    	}
        
    	muSapler1 = GLES20.glGetUniformLocation(mProgram, "Sampler1");
        if (muSapler1 == -1) {
        	throw new RuntimeException("Could not get attrib location for muSapler1");
    	}

    }
    
    public void initShader(SurfaceView sv)
    {
    	Log.d(TAG,"initShader");
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex3D.sh", sv.getResources() );
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag3D.sh", sv.getResources() );  
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
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
        if (muSapler0 == -1) {
        	throw new RuntimeException("Could not get attrib location for muSapler0");
    	}
        
    	muSapler1 = GLES20.glGetUniformLocation(mProgram, "Sampler1");
        if (muSapler1 == -1) {
        	throw new RuntimeException("Could not get attrib location for muSapler1");
    	}
        
/*        muDelt_s = GLES20.glGetUniformLocation(mProgram, "muDelt_s");
        if (muDelt_s == -1) {
        	throw new RuntimeException("Could not get attrib location for muDelt_s");
    	}*/
        
    }
    public void drawSelf(int texId)
    {        
    	
   	 GLES20.glClearColor(0.f, 0.f, 0.f, 1.0f);
   	 GLES20.glUseProgram(mProgram);
       checkGlError("glUseProgram");
       GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
   	

       updateDelt();
       
       
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

       GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
       GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, posTexture1);
       GLES20.glUniform1i(muSapler1, 1);
       
   //    GLES20.glUniform1f(muDelt_s, delt_s);
       
       GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
       checkGlError("glDrawArrays");
    }
    
    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private void  updateDelt(){
    	GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, posTexture1);
   // 	Log.d(TAG,"updateDelt");
    	Holography.update(0,0);

    }

    
}
