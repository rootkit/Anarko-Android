package com.oppous.anarko;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.oppous.anarko.classes.AKTextView;
import com.oppous.anarko.fragments.ChangeNumFragment;

public class SettingsChangeNumActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_changenum);

        ImageView btnBack = (ImageView) findViewById(R.id.activity_changeNum_btnBack);
        AKTextView btnNext = (AKTextView) findViewById(R.id.activity_changenum_btnNext);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_changeNum_btnBack:{
                this.finish();
            }
                break;
            case R.id.activity_changenum_btnNext:{
                FrameLayout container = (FrameLayout) findViewById(R.id.activity_changeNum_container);
                container.setVisibility(View.VISIBLE);
                ChangeNumFragment fragment = new ChangeNumFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.activity_changeNum_container, fragment).commit();
            }
                break;
        }
    }

}
