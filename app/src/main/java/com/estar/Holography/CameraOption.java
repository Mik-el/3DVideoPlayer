package com.estar.Holography;

import java.io.IOException;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.estar.video.utils.Utils;

public class CameraOption {
	SurfaceTexture mst;;
	private int previewWidth = 800; // 预览分辨率
	private int previewHeight = 600;
	Camera mCamera = null;

	private int cameraId = 0;

	int mcameraTextureId;

	public CameraOption() {
		mst = new SurfaceTexture(mcameraTextureId);
	}

	public void openCamera() throws IOException {
		Utils.showLogDebug("openCamera");
		if (mCamera == null) {
			int numCamera = Camera.getNumberOfCameras();
			if (numCamera == 2) {
				cameraId = 1;
			} else if (numCamera == 1) {
				cameraId = 0;
			} else {
				return;
			}
			mCamera = Camera.open(cameraId);
			mCamera.setPreviewTexture(mst);
		}
	}

	public void startPreview() throws IOException {
		Utils.showLogDebug("mCamera startPreview");
		if (mCamera == null) {
			openCamera();
		}
		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			// parameters.setPreviewFpsRange(15, 15);
			parameters.setPreviewSize(previewWidth, previewHeight);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}
	}

	public void stopPreview() {
		if (mCamera != null) {
			Utils.showLogDebug("mCamera stopPreview");
			mCamera.stopPreview();
		}
	}

	public void start() throws IOException {
		openCamera();
		startPreview();
	}

	public void stop() {
		Utils.showLogDebug("mCamera stop");
		if (mCamera != null) {
			Utils.showLogDebug("mCamera stopPreview");
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
}
