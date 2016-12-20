package com.oppous.anarko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsNotiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_noti);
    }

    // Button Events
    protected void onBack(View view){
        //TODO :
        this.finish();
    }
}
