package com.oppous.anarko.target;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.oppous.anarko.CircleProgress.DonutProgress;
import com.oppous.anarko.classes.AKProgressBar;

import java.io.File;

/**
 * @author Wayne
 */
public class VideoProgressTarget extends ProgressTarget<String, File> {
    private final AKProgressBar progress;
    private FrameLayout layoutLoad;

    public VideoProgressTarget(VideoLoadTarget target, AKProgressBar progress, FrameLayout layoutLoad) {
        super(target);
        this.progress = progress;
        this.layoutLoad = layoutLoad;
    }

    @Override
    public float getGranualityPercentage() {
        return 0.1f; // this matches the format string for #text below
    }

    @Override
    protected void onConnecting() {
        //progress.setVisibility(View.VISIBLE);
        //progress.setProgress(0);
        layoutLoad.setVisibility(View.VISIBLE);
        progress.startSpinning();
    }

    @Override
    protected void onDownloading(long bytesRead, long expectedLength) {
        int percent = (int)(100 * bytesRead / expectedLength);
        Log.d("download", "progress: = " + percent);
        //progress.setProgress(percent);
    }

    @Override
    protected void onDownloaded() {
    }

    @Override
    protected void onDelivered() {
        //progress.setVisibility(View.GONE);
        layoutLoad.setVisibility(View.GONE);
        progress.stopSpinning();
    }
}
