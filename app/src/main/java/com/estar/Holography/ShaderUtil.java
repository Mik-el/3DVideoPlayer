package com.estar.Holography;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

//加载顶点Shader与片元Shader的工具类
public class ShaderUtil 
{
	public static String TAG = "Holography";
   //加载制定shader的方法
   public static int loadShader
   (
		 int shaderType, //shader的类型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
		 String source   //shader的脚本字符串
   ) 
   {
	    //创建一个新shader
        int shader = GLES20.glCreateShader(shaderType);
        //若创建成功则加载shader
        if (shader != 0) 
        {
        	//加载shader的源代码
            GLES20.glShaderSource(shader, source);
            //编译shader
            GLES20.glCompileShader(shader);
            //存放编译成功shader数量的数组
            int[] compiled = new int[1];
            //获取Shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) 
            {//若编译失败则显示错误日志并删除此shader
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;      
            }  
        }
        return shader;
    }
    
   //创建shader程序的方法
   public static int createProgram(String vertexSource, String fragmentSource) 
   {
	    //加载顶点着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) 
        {
            return 0;
        }
        
        //加载片元着色器
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) 
        {
            return 0;
        }

        //创建程序
        int program = GLES20.glCreateProgram();
        //若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) 
        {
        	//向程序中加入顶点着色器
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //向程序中加入片元着色器
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //链接程序
            GLES20.glLinkProgram(program);
            //存放链接成功program数量的数组
            int[] linkStatus = new int[1];
            //获取program的链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            //若链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) 
            {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    
   //检查每一步操作是否有错误的方法 
   public static void checkGlError(String op) 
   {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) 
        {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
   }
   
   //从sh脚本中加载shader内容的方法
   public static String loadFromAssetsFile(String fname,Resources r)
   {
   	String result=null;    	
   	try
   	{
   		InputStream in=r.getAssets().open(fname);
			int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    byte[] buff=baos.toByteArray();
		    baos.close();
		    in.close();
   		result=new String(buff,"UTF-8"); 
   		result=result.replaceAll("\\r\\n","\n");
   	}
   	catch(Exception e)
   	{
   		e.printStackTrace();
   	}   
   	return result;
   }
   
   public static int initTexture(){
		int[] textures = new int[1];
		GLES10.glGenTextures
		(
				1,          
				textures,   
				0        
		);  
		int textureId=textures[0];    
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureId);
					//GL_NEAREST  GL_LINEAR
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER,GLES10.GL_NEAREST);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_MAG_FILTER,GLES10.GL_NEAREST);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,GLES10.GL_CLAMP_TO_EDGE);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,GLES10.GL_CLAMP_TO_EDGE);
        return textureId;
   }
   
   
   public static void LoadTextureFromRes(int textureId,int drawableId,Resources r){
	   GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureId);
       InputStream is = r.openRawResource(drawableId);
       Bitmap bitmapTmp;
       try {
       	bitmapTmp = BitmapFactory.decodeStream(is);
       } finally {
           try {
               is.close();
           } 
           catch(IOException e) {
               e.printStackTrace();
           }
       }
       GLUtils.texImage2D
       (
       		GLES10.GL_TEXTURE_2D,  
       		0, 					 
       		bitmapTmp, 	
       		0					 
       );
       bitmapTmp.recycle(); 
	   
   }
   
	public static int initTexture(int drawableId,Resources r){

		int[] textures = new int[1];
		GLES10.glGenTextures
		(
				1,          
				textures,   
				0        
		);    
		int textureId=textures[0];    
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureId);
					
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER,GLES10.GL_LINEAR);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D,GLES10.GL_TEXTURE_MAG_FILTER,GLES10.GL_LINEAR);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,GLES10.GL_CLAMP_TO_EDGE);
		GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,GLES10.GL_CLAMP_TO_EDGE);
        
        InputStream is = r.openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D
        (
        		GLES10.GL_TEXTURE_2D,  
        		0, 					 
        		bitmapTmp, 	
        		0					 
        );
        bitmapTmp.recycle(); 		
        
        return textureId;
	}
   
	
	public static int InitTextureFromBitmap(Bitmap bitmap) {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		int textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	//	bitmap.recycle(); //

		int errno = GLES20.glGetError();
		if (errno != GLES20.GL_NO_ERROR) {
			Log.e(TAG, "" + errno);
			return -1;
		}
		return textureId;
	}
	
	public static void LoadTextureFromBitmap(int tid,Bitmap bitmap) {
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tid);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		
	}

	public static Bitmap decodePic(int n){
		Bitmap tmp = null;
		File f = new File("/sdcard/pic/test"+n+".png");  
        InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}      
        
        try {
        	tmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
        }
		return tmp;
	}

}
