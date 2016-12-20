package com.oppous.anarko.camera;

public class VisageBridge {

    public VisageBridge() {

    }

    public static void loadLibrary() {
        System.loadLibrary("VisageVision");
        System.loadLibrary("VisageWrapper");
    }

    public static native void       TrackerInit(String configFileName);
    public static native void       TrackFromCam();
    public static native void       TrackFromImage();
    public static native void       GetFps();
    public static native void       GetDisplayFps();
    public static native String     GetStatus();
    public static native void       setParameters(int width, int height, int orientation, int flip);
    public static native void       TrackerStop();
    public static native boolean    IsAutoStopped();
    public static native void       WriteFrameCamera(byte[] frame, int cameraId);
    public static native boolean    displayTrackingStatus(int width, int height);
    public static native void       DisplayInstructions();
    public static native float[]    getFacialPoints();   //get facial points
    public static native boolean    getTrackingStatus();    // true: OK, false: Bad
    public static native int        getFaceStatus();     // 0: normal, 1: wink, 2: mouth

}
