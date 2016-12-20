package com.oppous.anarko.camera;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.oppous.anarko.CamCaptureActivity;
import com.oppous.anarko.classes.AppManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AKCameraManager {
    private static final String TAG = "AKCameraManager";
    private CamCaptureActivity          activity;
    private OnCameraActionListener      mOnCameraActionListener;
    private FrameLayout                 mCameraPreviewContainer;
    private AKCameraTrackerView         mCameraTrackerView;
    private AKTrackerGLSurfaceView      mTrackerGLSurfaceView;
    private AKMediaRecorder             mMediaRecorder;
    private CameraThread                mCameraThread;

    public AKCameraManager(CamCaptureActivity activity) {
        this.activity = activity;

        copyAssets(activity.getFilesDir().getAbsolutePath());
        VisageBridge.loadLibrary();
    }

    public void createCameraView() {
        if (mCameraPreviewContainer == null || getNumberOfCameras() == 0)
            return;

        VisageBridge.TrackerInit(activity.getFilesDir().getAbsolutePath() + "/Facial Features Tracker - High.cfg");

        mCameraTrackerView = new AKCameraTrackerView(activity);
        mCameraPreviewContainer.addView(mCameraTrackerView);

        mTrackerGLSurfaceView = new AKTrackerGLSurfaceView(activity);
        mCameraTrackerView.setGLView(mTrackerGLSurfaceView);
        mCameraPreviewContainer.addView(mTrackerGLSurfaceView);

        /*-----------------Set Mask-----------------------*/
        AKMask mask = new AKMask();
        mask.id = "test";
        mask.name = "test";
        mask.thumbnail = "test thumbnail uri string";
        mask.distance = 144;
        mask.leftPointX = 264;
        mask.leftPointY = 321;
        mask.purchased = true;

        setMask(mask);
        /*--------------------------------------------------*/

        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenOrientation = display.getRotation();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(display.getHeight() * 0.75), display.getHeight());
        mCameraPreviewContainer.setLayoutParams(params);

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraTrackerView.getCameraId(), cameraInfo);
        int orientation = cameraInfo.orientation;

        if (mCameraTrackerView.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraThread = new CameraThread(mCameraTrackerView.getPreviewWidth(), mCameraTrackerView.getPreviewHeight(), 1,
                    (screenOrientation * 90 +orientation) % 360); //270
        } else {
            mCameraThread = new CameraThread(mCameraTrackerView.getPreviewWidth(), mCameraTrackerView.getPreviewHeight(), 0,
                    (orientation - screenOrientation * 90 + 360) % 360); //90
        }

        mCameraThread.start();
    }

    public void removeCameraView() {
        if (mCameraTrackerView != null)
            mCameraTrackerView.closeCamera();
    }

    public void startRecord() {
        if (mCameraTrackerView == null)
            return;

        mMediaRecorder = new AKMediaRecorder(activity, mCameraTrackerView, getOutputFile());
        mMediaRecorder.startRecord();
    }

    public void stopRecord() {
        if (mMediaRecorder == null)
            return;

        mMediaRecorder.stopRecord();
    }

    public void setMask(AKMask mask) { //Set mask
        //Should implement function for deployment mask.
    }

    public void switchCamera() {
        if (mCameraTrackerView !=  null)
            mCameraTrackerView.switchCamera();
    }

    public void setCameraPreviewContainer(FrameLayout container) {
        if (container != null) {
            mCameraPreviewContainer = container;
        }
    }

    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public void setOnCameraActionListener(OnCameraActionListener listener) {
        if (listener != null)
            mOnCameraActionListener = listener;
    }

    public interface OnCameraActionListener {
        public void onRecordStoped();
    }

    public void copyFile(String rootDir, String filename) {
        AssetManager assetManager = activity.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            String newFileName = rootDir + File.separator + filename;
            File file = new File(newFileName);

            if(!file.exists()) {
                in = assetManager.open("trackerdata/" + filename);
                out = new FileOutputStream(newFileName);

                byte[] buffer = new byte[4*1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
                out = null;

                in.close();
                in = null;
            }
        } catch (Exception e) {
            Log.e("VisageTrackerDemo", e.getMessage());
        }
    }

    public void copyAssets(String rootDir) {

        AssetManager assetManager = activity.getAssets();

        String assets[] = null;
        try {
            assets = assetManager.list("trackerdata");

            for (String asset : assets) {
                Log.i("VisageTrackerDemo", rootDir + File.separator + asset);
                try {
                    if (!asset.contains("bdtsdata")) copyFile(rootDir, asset);
                } catch (Exception e) {
                    Log.e("VisageTrackerDemo", e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("VisageTrackerDemo", e.getMessage());
        }

        // create dirs
        final String[] dirs = {
                "bdtsdata",
                "bdtsdata/FF",
                "bdtsdata/LBF",
                "bdtsdata/NN",
                "bdtsdata/LBF/pe",
                "bdtsdata/LBF/vfadata",
                "bdtsdata/LBF/ye",
                "bdtsdata/LBF/vfadata/ad",
                "bdtsdata/LBF/vfadata/ed",
                "bdtsdata/LBF/vfadata/gd"
        };

        for (String dirname : dirs) {
            try {
                File dir = new File(rootDir + File.separator + dirname);
                if (!dir.exists()) dir.mkdir();
            } catch (Exception e) {
                Log.e("VisageTrackerDemo", e.getMessage());
            }
        }

        // copy files
        final String[] files = {
                "bdtsdata/FF/ff.dat",
                "bdtsdata/LBF/pe/landmarks.txt",
                "bdtsdata/LBF/pe/lp11.bdf",
                "bdtsdata/LBF/pe/W",
                "bdtsdata/LBF/vfadata/ad/ad.lbf",
                "bdtsdata/LBF/vfadata/ed/ed0.lbf",
                "bdtsdata/LBF/vfadata/ed/ed1.lbf",
                "bdtsdata/LBF/vfadata/ed/ed2.lbf",
                "bdtsdata/LBF/vfadata/ed/ed3.lbf",
                "bdtsdata/LBF/vfadata/ed/ed4.lbf",
                "bdtsdata/LBF/vfadata/ed/ed5.lbf",
                "bdtsdata/LBF/vfadata/gd/gd.lbf",
                "bdtsdata/LBF/ye/landmarks.txt",
                "bdtsdata/LBF/ye/lp11.bdf",
                "bdtsdata/LBF/ye/W",
                "bdtsdata/NN/fa.lbf",
                "bdtsdata/NN/fc.lbf"
        };

        for (String filename : files) {
            try {
                Log.i("VisageTrackerDemo", rootDir + File.separator + filename);
                copyFile(rootDir, filename);
            } catch(Exception e) {
                Log.e("VisageTrackerDemo", e.getMessage());
            }
        }
    }

    public int getOrientation(CamCaptureActivity activity, AKCameraTrackerView cameraPreview) {
        int ret = 0;

        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenOrientation = display.getRotation();

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraPreview.getCameraId(), cameraInfo);
        int orientation = cameraInfo.orientation;

        if (cameraPreview.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            ret = (screenOrientation * 90 +orientation) % 360;
        } else {
            ret = (orientation - screenOrientation * 90 + 360) % 360;
        }

        return ret;
    }

    public int getFlip(AKCameraTrackerView cameraPreview) {
        int ret = 0;

        if (cameraPreview.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            ret = 1;
        } else {
            ret = 0;
        }

        return ret;
    }

    public File getOutputFile() {
        File file = new File(AppManager.getBasePath(), "video.avi");
        if (file.exists())
            file.delete();

        return file;
    }

    private final class CameraThread extends Thread {
        private int Cwidth;
        private int Cheight;
        private int Corientation;
        private int Cflip;

        protected CameraThread(int width, int height, int flip, int orientation) {

            Cwidth = width;
            Cheight = height;
            Corientation = orientation;
            Cflip = flip;
        }

        @Override
        public void run() {
            VisageBridge.setParameters(Cwidth, Cheight, Corientation, Cflip);
            VisageBridge.TrackFromCam();
            return;
        }
    }
}
