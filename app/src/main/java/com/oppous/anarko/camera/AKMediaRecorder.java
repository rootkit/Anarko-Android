package com.oppous.anarko.camera;

import android.app.ProgressDialog;
import android.media.CamcorderProfile;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.oppous.anarko.CamCaptureActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import processing.ffmpeg.videokit.VideoKit;

public class AKMediaRecorder {
    private static final String TAG = "AKMediaRecorder";

    private CamCaptureActivity  activity;

    public AKMediaRecorder(CamCaptureActivity activity, AKCameraTrackerView cameraTrackerView, File outputFile) {
        this.activity = activity;
    }

    public void startRecord() {
        //Start Recording
    }

    public void stopRecord() {
        //Stop Recording
    }

}
