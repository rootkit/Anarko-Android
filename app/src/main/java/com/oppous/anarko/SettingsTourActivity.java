package com.oppous.anarko;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.oppous.anarko.classes.AKTextView;

public class SettingsTourActivity extends AppCompatActivity implements View.OnClickListener{

    private AKTextView btnClose;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_tour);

        videoView = (VideoView) findViewById(R.id.activity_tour_videoView);
        btnClose = (AKTextView) findViewById(R.id.activity_tour_btnClose);
        btnClose.setOnClickListener(this);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                SettingsTourActivity.this.finish();
            }
        });

        Uri url = Uri.parse("android.resource://com.oppous.anarko/" + R.raw.video);
        videoView.setVideoURI(url);
        videoView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_tour_btnClose:
            {
                this.finish();
            }
                break;
        }
    }
}
