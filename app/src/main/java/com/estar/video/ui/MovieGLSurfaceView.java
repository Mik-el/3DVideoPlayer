/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.estar.video.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;

import com.estar.Holography.CameraOption;
import com.estar.Holography.FrameBufferOBJ;
import com.estar.Holography.Render2DTo3D;
import com.estar.Holography.Render3D;
import com.estar.Holography.RenderVideo;
import com.estar.video.CameraManagerService;
import com.estar.video.HarderPlayerActivity;
import com.estar.video.MoviePlayer;
import com.estar.video.data.VideoObject;
import com.estar.video.utils.Utils;

/**
 * OpenGL显示电影
 * 
 * @author zgl
 * 
 */
public class MovieGLSurfaceView extends GLSurfaceView {
	/** 渲染类 */
	private MyRenderer mRenderer;
	/** 父Activity */
	private HarderPlayerActivity mActivity;
	private MoviePlayer mPlayer;

	private RenderVideo mRenderVid;
	private Render2DTo3D mrender2DTo3D;
	private Render3D mRender3D;
	public Render3D mRender3DSX;
	private FrameBufferOBJ mFBO = null;

	public Surface surface;
	/** 屏幕的宽高 */
	public int mBufferWidth, mBufferHeight;
	/** 视频的宽高(设置半宽、半高或无收敛后) */
	private int mVideoWidth, mVideoHeight;
	/** 视频经计算（等比例缩放至高度或宽度撑满全屏时）的左下角坐标 */
	private int mStartX, mStartY;
	/** 视频经计算（等比例缩放至高度或宽度撑满全屏时）的宽高 */
	private int mRealWidth, mRealHeight;

	/** 原始视频的左下角坐标(忽略收敛性) */
	private int mStartOriginalX, mStartOriginalY;
	/** 视频经计算（等比例缩放至高度或宽度撑满全屏时）的宽高 */
	private int mRealOriginalWidth, mRealOriginalHeight;

	/** vr模式视频的左下角坐标(忽略收敛性) */
	private int mStartVrX, mStartVrY;
	/** 视频经计算（等比例缩放至高度或宽度撑满全屏时）的宽高 */
	private int mRealVrWidth, mRealVrHeight;

	private SurfaceTexture mSurface;
	private int mTextureID = 0;
	private final int GL_TEXTURE_EXTERNAL_OES = 0x8D65; // 用于从图像流中获取数据

	// 异步处理照相机打开关闭
	private HandlerThread cameraHandlerThread = new HandlerThread("cameraOperator");
	private Handler cameraHandler;

	public MovieGLSurfaceView(Context context) {
		this(context, null);
	}

	public MovieGLSurfaceView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.mActivity = (HarderPlayerActivity) context;
		init();
	}

	/** 初始化GLSurfaceView */
	public void init() {
		setEGLContextClientVersion(2);
		mRenderer = new MyRenderer();
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		cameraHandlerThread.start();
		cameraHandler = new Handler(cameraHandlerThread.getLooper(), new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				return false;
			}
		});
	}

	/** 当前播放视频格式 */
	private int mVideoType = VideoObject.VIDEO_TYPE_2D;

	/** 获取当前播放视频格式 */
	public int getVideoType() {
		return mVideoType;
	}

	/** 设置当前播放视频格式 */
	public void setVideoType(int mVideoType) {
		Utils.showLogDebug("MovieGLSurfaceView setVideoType " + mVideoType);
		this.mVideoType = mVideoType;
	}

	/** 设置是否3D效果显示 */
	public void show3D(boolean isShow3D) {
		if (isShow3D) {
			CameraManagerService.mcoStartPreview(mActivity);
		} else {
			CameraManagerService.mcoStopPreview(mActivity);
		}
	}

	private float mwper = 1, mhper = 1;

	/** 设置视频相对于屏幕的宽度比和高度比 */
	public void setPercent(float wper, float hper) {
		if (mwper != wper || mhper != hper) {
			mwper = wper;
			mhper = hper;
		}
	}

	/** 视频全宽、半宽 */
	private int mConvergence = VideoObject.CONVERGENCE_HALF;

	public int getConvergence() {
		return mConvergence;
	}

	/** 设置宽高 */
	public void setFixedSize(int width, int height, int convergence) {
		this.mConvergence = convergence;
		if (mVideoWidth != width || mVideoHeight != height) {
			mVideoWidth = width;
			mVideoHeight = height;

			float ratioW = (float) mBufferWidth / width;
			float ratioH = (float) mBufferHeight / height;
			float ratio = (ratioW < ratioH) ? ratioW : ratioH;

			mRealOriginalWidth = (int) (width * ratio);
			mRealOriginalHeight = (int) (height * ratio);

			mStartOriginalX = (mBufferWidth - mRealOriginalWidth) / 2;
			mStartOriginalY = (mBufferHeight - mRealOriginalHeight) / 2;

			ratioW = (float) mBufferWidth / width;
			ratioH = (float) mBufferHeight / (height / 2);
			ratio = (ratioW < ratioH) ? ratioW : ratioH;

			mRealVrWidth = (int) (width * ratio);
			mRealVrHeight = (int) ((height / 2) * ratio);
			mStartVrX = (mBufferWidth - mRealVrWidth) / 2;
			mStartVrY = (mBufferHeight - mRealVrHeight) / 2;
		}

		switch (convergence) {
		case VideoObject.CONVERGENCE_NONE:
			switch (mVideoType) {
			case VideoObject.VIDEO_TYPE_3D_LR:
				height = height * 2;
				break;
			case VideoObject.VIDEO_TYPE_3D_UD:
				width = width * 2;
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		float ratioW = (float) mBufferWidth / width;
		float ratioH = (float) mBufferHeight / height;
		float ratio = (ratioW < ratioH) ? ratioW : ratioH;
		mRealWidth = (int) (width * ratio);
		mRealHeight = (int) (height * ratio);
		mStartX = (mBufferWidth - mRealWidth) / 2;
		mStartY = (mBufferHeight - mRealHeight) / 2;
	}

	private boolean mIsFullScreen = false;
	
	/** 设置是否全屏显示 */
	public void setIsFullScreen(boolean isFullScreen){
		mIsFullScreen = isFullScreen;
	}
	
	/** 获取是否全屏显示 */
	public boolean isFullScreen(){
		return mIsFullScreen;
	}

	private class MyRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
		private int mMidTexture;
		private boolean updateSurfaceVid = false;

		/** 标志在下一次onDrawFrame中将执行截图 */
		public boolean toSaveBitmap = false;
		public File dir_image;

		/** 截图 */
		private void saveBitmap() {
			int b[] = new int[(int) (mBufferWidth * mBufferHeight)];
			int bt[] = new int[(int) (mBufferWidth * mBufferHeight)];
			IntBuffer buffer = IntBuffer.wrap(b);
			buffer.position(0);
			GLES20.glReadPixels(0, 0, mBufferWidth, mBufferHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);

			for (int i = 0; i < mBufferHeight; i++) {
				// remember, that OpenGL bitmap is incompatible with Android
				// bitmap and so, some correction need.
				for (int j = 0; j < mBufferWidth; j++) {
					int pix = b[i * mBufferWidth + j];
					int pb = (pix >> 16) & 0xff;
					int pr = (pix << 16) & 0x00ff0000;
					int pix1 = (pix & 0xff00ff00) | pr | pb;
					bt[(mBufferHeight - i - 1) * mBufferWidth + j] = pix1;
				}
			}
			Bitmap inBitmap = Bitmap.createBitmap(mBufferWidth, mBufferHeight, Bitmap.Config.ARGB_8888);
			inBitmap.copyPixelsFromBuffer(buffer);
			inBitmap = Bitmap.createBitmap(bt, mBufferWidth, mBufferHeight, Bitmap.Config.ARGB_8888);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			inBitmap.compress(CompressFormat.JPEG, 90, bos);
			byte[] bitmapdata = bos.toByteArray();
			ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

			final Calendar c = Calendar.getInstance();
			long mytimestamp = c.getTimeInMillis();
			String timeStamp = String.valueOf(mytimestamp);
			String myfile = "3dvideo截图" + timeStamp + ".jpg";

			try {
				dir_image = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + myfile);
				FileOutputStream fos = new FileOutputStream(dir_image);

				byte[] buf = new byte[1024];
				int len;
				while ((len = fis.read(buf)) > 0) {
					fos.write(buf, 0, len);
				}
				fis.close();
				fos.close();

				Message msg = new Message();
				msg.what = MoviePlayer.SCREEN_CAPTURE_SUCCESS;
				msg.obj = dir_image.getAbsolutePath();
				if (mPlayer.controlHanlder != null) {
					synchronized (mPlayer.controlHanlder) {
						mPlayer.controlHanlder.sendMessage(msg);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			toSaveBitmap = false;
		}
		
		float[] mSTMatrix = {
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,0,0,1
		};

		public void onDrawFrame(GL10 glUnused) {
			if (toSaveBitmap) {
				saveBitmap();
			}

			// xuwanliang modify 可能导致死锁的问题
			synchronized (this) {
				if (updateSurfaceVid) {
					mSurface.updateTexImage();
					mSurface.getTransformMatrix(mSTMatrix);
					mRenderVid.setTransformMatrix(mSTMatrix);
					mrender2DTo3D.setTransformMatrix(mSTMatrix);
					
					if (MoviePlayer.PlayMode.MODE_3D.equals(mPlayer.getPlayMode())) {
						mRenderVid.setIs2D(false);
					} else if (MoviePlayer.PlayMode.MODE_2D.equals(mPlayer.getPlayMode())) {
						mRenderVid.setIs2D(true);
					}
					updateSurfaceVid = false;
				}
			}
			// end xuwanliang

			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
			if (MoviePlayer.PlayMode.MODE_3D.equals(mPlayer.getPlayMode())) {
				mFBO.used();
				GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
				if (mVideoType == VideoObject.VIDEO_TYPE_3D_LR) {
					if (mIsFullScreen) {
						GLES20.glViewport(0, 0, mBufferWidth, mBufferHeight);
						mRenderVid.drawSelf();
					} else {
						if (mRealWidth == mBufferWidth) {
							// 与屏幕等宽视频直接绘制
							GLES20.glViewport(0, 0, mRealWidth, mRealHeight);
							mRenderVid.drawSelf();
						} else {
							// 与屏幕不等比例，则先将左画面绘制到FrameBuffer左边，右画面绘制到右边，供交织
							GLES20.glViewport(mStartX / 2, mStartY, mRealWidth / 2, mRealHeight);
							mRenderVid.drawSelfLeft();
							GLES20.glViewport(mBufferWidth / 2 + mStartX / 2, mStartY, mRealWidth / 2, mRealHeight);
							mRenderVid.drawSelfRight();
						}
					}

				} else if (mVideoType == VideoObject.VIDEO_TYPE_3D_UD) {
					if (mIsFullScreen) {
						GLES20.glViewport(0, 0, mBufferWidth / 2, mBufferHeight);
						mRenderVid.drawSelfBottom();

						GLES20.glViewport(mBufferWidth / 2, 0, mBufferWidth / 2, mBufferHeight);
						mRenderVid.drawSelfTop();
					} else {
						GLES20.glViewport(mStartX / 2, mStartY, mRealWidth / 2, mRealHeight);
						mRenderVid.drawSelfBottom();

						GLES20.glViewport(mBufferWidth / 2 + mStartX / 2, mStartY, mRealWidth / 2, mRealHeight);
						mRenderVid.drawSelfTop();
					}
				} else {
					if (mIsFullScreen) {
						GLES20.glViewport(mBufferWidth / 2, 0, mBufferWidth / 2, mBufferHeight);
						mrender2DTo3D.drawSelf(2.0f);

						GLES20.glViewport(0, 0, mBufferWidth / 2, mBufferHeight);
						mrender2DTo3D.drawSelf(0);
					} else {
						GLES20.glViewport(mBufferWidth / 2 + mStartX / 2, mStartY, mRealWidth / 2, mRealHeight);
						mrender2DTo3D.drawSelf(2.0f);

						GLES20.glViewport(mStartX / 2, mStartY, mRealWidth / 2, mRealHeight);
						mrender2DTo3D.drawSelf(0);
					}
				}
				mFBO.unused();
				GLES20.glViewport(0, 0, mBufferWidth, mBufferHeight);
				mRender3D.drawSelf(mMidTexture);
			} else if (MoviePlayer.PlayMode.MODE_2D.equals(mPlayer.getPlayMode())) {
				if (mIsFullScreen) {
					GLES20.glViewport(0, 0, mBufferWidth, mBufferHeight);
				} else {
					GLES20.glViewport(mStartX, mStartY, mRealWidth, mRealHeight);
				}

				if (mVideoType == VideoObject.VIDEO_TYPE_3D_LR) {
					mRenderVid.drawSelfLeft();
				} else if (mVideoType == VideoObject.VIDEO_TYPE_3D_UD) {
					mRenderVid.drawSelfTop();
				} else {
					mrender2DTo3D.drawSelf(0);
				}
			} else if (MoviePlayer.PlayMode.MODE_LR.equals(mPlayer.getPlayMode())) {
				if (VideoObject.VIDEO_TYPE_3D_LR == mVideoType) {
					if (mIsFullScreen) {
						GLES20.glViewport(0, 0, mBufferWidth, mBufferHeight);
					} else {
						GLES20.glViewport(mStartOriginalX, mStartOriginalY, mRealOriginalWidth, mRealOriginalHeight);
					}
					mrender2DTo3D.drawSelf(0);
				} else if (VideoObject.VIDEO_TYPE_3D_UD == mVideoType) {
					if (mIsFullScreen) {
						GLES20.glViewport(0, 0, mBufferWidth / 2, mBufferHeight);
						mRenderVid.drawSelfBottom();

						GLES20.glViewport(mBufferWidth / 2, 0, mBufferWidth / 2, mBufferHeight);
						mRenderVid.drawSelfTop();
					} else {
						GLES20.glViewport(mStartOriginalX / 2, mStartOriginalY, mRealOriginalWidth / 2, mRealOriginalHeight);
						mRenderVid.drawSelfBottom();

						GLES20.glViewport(mBufferWidth / 2 + mStartOriginalX / 2, mStartOriginalY, mRealOriginalWidth / 2, mRealOriginalHeight);
						mRenderVid.drawSelfTop();
					}
				}
			} else if (MoviePlayer.PlayMode.MODE_VR.equals(mPlayer.getPlayMode())) {
				if (VideoObject.VIDEO_TYPE_3D_LR == mVideoType) {
					if (mIsFullScreen) {
						GLES20.glViewport(0, 0, mBufferWidth, mBufferHeight);
					} else {
						GLES20.glViewport(mStartVrX, mStartVrY, mRealVrWidth, mRealVrHeight);
					}
					mrender2DTo3D.drawSelf(0);
				} else if (VideoObject.VIDEO_TYPE_3D_UD == mVideoType) {
					if (mIsFullScreen) {
						GLES20.glViewport(0, 0, mBufferWidth / 2, mBufferHeight);
						mRenderVid.drawSelfBottom();

						GLES20.glViewport(mBufferWidth / 2, 0, mBufferWidth / 2, mBufferHeight);
						mRenderVid.drawSelfTop();
					} else {
						GLES20.glViewport(mStartOriginalX / 2, (mRealOriginalHeight - mBufferHeight / 2) / 2, mRealOriginalWidth / 2, mRealOriginalHeight / 2);
						mRenderVid.drawSelfBottom();

						GLES20.glViewport(mBufferWidth / 2 + mStartOriginalX / 2, (mRealOriginalHeight - mBufferHeight / 2) / 2, mRealOriginalWidth / 2, mRealOriginalHeight / 2);
						mRenderVid.drawSelfTop();
					}
				}
			}
		}

		@Override
		public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
			Utils.showLogDebug("onSurfaceCreated");
			// 不能放在构造方法中，因为只有在那里会报空
			mPlayer = mActivity.getMoviePlayer();

			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			GLES20.glClearColor(0, 0, 0, 1.0f);
			if (mTextureID == 0) {
				initTexture();
			}
			if (mSurface == null) {
				mSurface = new SurfaceTexture(mTextureID);

			}
			mSurface.setOnFrameAvailableListener(this);

			if (mRenderVid == null) {
				mRenderVid = new RenderVideo(MovieGLSurfaceView.this, this, mwper, mhper);
				mRenderVid.mSurface = mSurface;
				mRenderVid.mTextureID = mTextureID;
			}

			if (mrender2DTo3D == null) {
				mrender2DTo3D = new Render2DTo3D(MovieGLSurfaceView.this, this, mwper, mhper);
				mrender2DTo3D.mSurface = mSurface;
				mrender2DTo3D.mTextureID = mTextureID;
			}

			if (mRender3D == null) {
				mRender3D = new Render3D(MovieGLSurfaceView.this, 0);
			}
			if (mRender3DSX == null) {
				mRender3DSX = new Render3D(MovieGLSurfaceView.this, 1);
			}

			if (CameraManagerService.mCameraOption == null && MoviePlayer.PlayMode.MODE_3D.equals(mPlayer.getPlayMode())) {
				CameraManagerService.mCameraOption = new CameraOption();
			}

			if (surface == null) {
				surface = new Surface(mSurface);
			}

			Handler controlHandler = (Handler) getTag();
			controlHandler.sendEmptyMessage(MoviePlayer.ON_SURFACE_CREATED);

			MovieControllerOverlay controller = mActivity.getMovieControllerOverlay();
			controller.iv_capture.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toSaveBitmap = true;
				}
			});
		}

		@Override
		public void onSurfaceChanged(GL10 glUnused, int w, int h) {
			Utils.showLogDebug("onSurfaceChanged");
			if (w < h) {
				int tmp = w;
				w = h;
				h = tmp;
			}
			mBufferWidth = w;
			mBufferHeight = h;
			// mRealWidth = mBufferWidth;
			// mRealHeight = mBufferHeight;
			GLES20.glViewport(0, 0, w, h);
			// mRender3D = new Render3D(MyGLSurfaceView.this, w, h);
			if (mFBO == null) {
				// 防止framebuffer被重复创建
				mFBO = new FrameBufferOBJ(mBufferWidth, mBufferHeight);
				mMidTexture = mFBO.getTexture();
			}
			mrender2DTo3D.setSize(w, h);
		}

		@Override
		synchronized public void onFrameAvailable(SurfaceTexture surface) {
			updateSurfaceVid = true;
			requestRender();
		}
	}

	private void initTexture() {
		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);

		mTextureID = textures[0];
		GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

		// Can't do mipmapping with camera source
		GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		// Clamp to edge is the only option
		GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		// checkGlError("glTexParameteri mTextureID");
	}

	// /** 打开照相机动作 */
	// private Runnable startCameraRunnable = new Runnable() {
	// @Override
	// public void run() {
	// if (mco == null && mSurface != null) {
	// mco = new CameraOption();
	// Utils.showLogDebug("mco.mcoStart()");
	// try {
	// mco.start();
	// } catch (Exception e) {
	// // 需提示打开照相机失败
	// // mactivity.handler.sendEmptyMessage(PictureShowOpenGL.CAMERA_ERROR);
	// e.printStackTrace();
	// }
	// }
	// }
	// };
	//
	// /** 关闭照相机动作 */
	// private Runnable stopCameraRunnable = new Runnable() {
	// @Override
	// public void run() {
	// if (mco != null) {
	// Utils.showLogDebug("mco.mcoStop()");
	// try {
	// mco.stop();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// mco = null;
	// }
	// }
	// };
	//
	// /** 延迟打开照相机 */
	// public void startCameraDelayed(int delayMillis) {
	// cameraHandler.removeCallbacks(startCameraRunnable);
	// cameraHandler.removeCallbacks(stopCameraRunnable);
	// cameraHandler.postDelayed(startCameraRunnable, delayMillis);
	// }
	//
	// /** 延迟关闭照相机 */
	// public void stopCameraDelayed(int delayMillis) {
	// cameraHandler.removeCallbacks(startCameraRunnable);
	// cameraHandler.removeCallbacks(stopCameraRunnable);
	// cameraHandler.postDelayed(stopCameraRunnable, delayMillis);
	// }
}
