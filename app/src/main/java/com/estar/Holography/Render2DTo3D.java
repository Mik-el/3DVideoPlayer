package com.estar.Holography;

import static com.estar.Holography.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.view.SurfaceView;

import com.estar.video.utils.Utils;

@TargetApi(Build.VERSION_CODES.FROYO)
public class Render2DTo3D {
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;   // 用于从图像流中获取数据
    
    private int mProgram;
    private int maPositionHandle;
    private int maTextureHandle;
    private int maScreenHeightHandle;
    private int mais2dto3dHandle;

	FloatBuffer   mVertexBuffer;
	FloatBuffer   mTexCoorBuffer;
	
    String mVertexShader;  	 
    String mFragmentShader;
    
    public int muSTMatrixHandle;
    
    public SurfaceTexture mSurface;
    
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTICES_DATA_STRIDE_BYTEST = 3 * FLOAT_SIZE_BYTES;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    
    private final float[] mVerticesDataTOrign = {
    		-1.0f, -1.0f, 0, 
    		1.0f,  -1.0f, 0,
    		1.0f,  1.0f, 0,
    		
    		1.0f, 1.0f, 0, 
    		-1.0f,   1.0f, 0,
    		-1.0f,   -1.0f, 0,
    };  
    
    private float[] mVerticesDataT = {
    		-1.0f, -1.0f, 0, 
    		1.0f,  -1.0f, 0,
    		1.0f,  1.0f, 0,
    		
    		1.0f, 1.0f, 0, 
    		-1.0f,   1.0f, 0,
    		-1.0f,   -1.0f, 0,
    };  
    
    private final float[] mVerticesDataV = {
    		0, 0,
    		1, 0,
    		1, 1,
    		
    		1, 1,
    		0, 1,
    		0, 0,
        
        };
    
    public float[] mSTMatrix = {
			1,0,0,0,
			0,1,0,0,
			0,0,1,0,
			0,0,0,1
	};
    
    private FloatBuffer mVerticesT;
    private FloatBuffer mVerticesV;
    
    float mwidth;
    float mheight;
    
    int vCount=0;   
    public Render2DTo3D(SurfaceView mv, SurfaceTexture.OnFrameAvailableListener ol, float wper, float hper) {
    	setPercent(wper, hper);
    	initVertexData();
    	initShader(mv);
 //   	initTexture();

//    	mSurface = new SurfaceTexture(mTextureID);
//        mSurface.setOnFrameAvailableListener(ol);
    }
    
    public void setPercent(float wper, float hper) {
    	for( int i = 0; i < 6; i++ ) {
    		mVerticesDataT[3 * i + 0] = mVerticesDataTOrign[3 * i + 0] * wper;
    		mVerticesDataT[3 * i + 1] = mVerticesDataTOrign[3 * i + 1] * hper;
		}
    }
    public void setTransformMatrix(float[] mtx){
    	mSTMatrix = mtx;
    }
    
    public void setSize(float w, float h) {
    	mwidth = w;
    	mheight = h;
    }
    
    public void initVertexData()
    {
        mVerticesT = ByteBuffer.allocateDirect(mVerticesDataT.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesT.put(mVerticesDataT).position(0);
        
        mVerticesV = ByteBuffer.allocateDirect(mVerticesDataV.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticesV.put(mVerticesDataV).position(0);
    }
    
    public int mTextureID;
    
    public void initTexture() {
       
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
  //      checkGlError("glBindTexture mTextureID");

        // Can't do mipmapping with camera source
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        // Clamp to edge is the only option
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
 //      checkGlError("glTexParameteri mTextureID");
	}
    
    public void updateSurface(){
        mSurface.updateTexImage();
        mSurface.getTransformMatrix(mSTMatrix);
    }
    public SurfaceTexture getSurfaceTexture() {
		return mSurface;
    }
    
    public void initShader(SurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex2dto3d.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag2dto3d.sh", mv.getResources());  
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
        	Utils.showLogError("create program error");
            return;
        }
        Utils.showLogDebug("mProgram = "+mProgram);
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
        
        maScreenHeightHandle = GLES20.glGetUniformLocation(mProgram, "screenHeight");
        mais2dto3dHandle = GLES20.glGetUniformLocation(mProgram, "is2dto3d");
  
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
    }

    public void drawSelf(float is2dto3d)
    {        
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

        mVerticesT.position(VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
        		VERTICES_DATA_STRIDE_BYTEST, mVerticesT);
        checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");
      
	    mVerticesV.position(0);
	    GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
	        		2 * FLOAT_SIZE_BYTES, mVerticesV);
        
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");
        
        GLES20.glUniform1f(maScreenHeightHandle, mheight);
        GLES20.glUniform1f(mais2dto3dHandle, is2dto3d);
        
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        checkGlError("glDrawArrays");
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Utils.showLogError(op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
