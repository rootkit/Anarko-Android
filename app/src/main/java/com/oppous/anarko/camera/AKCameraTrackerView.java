package com.oppous.anarko.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import com.oppous.anarko.classes.AppManager;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class AKCameraTrackerView extends AKTrackerView implements SurfaceHolder.Callback {
    private static final String TAG = "AKCameraPreview";

    private Context             mContext;
    private Camera              mCamera;
    private int                 mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private SurfaceTexture      mSurfaceTexture;
    private int                 mPreviewWidth;
    private int                 mPreviewHeight;

    public AKCameraTrackerView(Context context) {
        super(context);
        this.mContext = context;
        grabFromCamera();
        getHolder().addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        setKeepScreenOn(true);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        _width = width;
        _height = height;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera() {
        if (Camera.getNumberOfCameras() < 2)
            return;

        closeCamera();

        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        else
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

        grabFromCamera();
    }

    public void grabFromCamera() {
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Camera.Parameters   parameters = mCamera.getParameters();
        setPreviewSize(parameters, 300);
        mCamera.setParameters(parameters);

        final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();

        int dataBufferSize = (int) (previewSize.height * previewSize.width *
                (ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat()) / 8.0));
        for (int i = 0; i < 10; i++) {
            mCamera.addCallbackBuffer(new byte[dataBufferSize]);
        }

        mSurfaceTexture = new SurfaceTexture(0);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPreviewWidth = previewSize.width;
        mPreviewHeight = previewSize.height;

        mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
        mCamera.startPreview();

    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            camera.addCallbackBuffer(data);
            VisageBridge.WriteFrameCamera(data, mCameraId);
        }
    };

    public void setPreviewSize(Camera.Parameters parameters, int width) {
        int idx = 0, dist = 100000;
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < sizes.size(); i++) {
            if (Math.abs(sizes.get(i).width - width) < dist) {
                idx = i;
                dist = Math.abs(sizes.get(i).width - width);
            }
        }

        parameters.setPreviewSize(sizes.get(idx).width, sizes.get(idx).height);
    }

    public int getCameraId() {
        return mCameraId;
    }

    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    public int getPreviewHeight() {
        return mPreviewHeight;
    }

}
