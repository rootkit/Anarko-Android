package com.oppous.anarko;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.FontManager;

import java.util.Map;

public class CamPreviewActivity extends AppCompatActivity {

    private FrameLayout container_addTitle;
    private FrameLayout container_description;
    private EditText    txt_title;
    private EditText    txt_description;
    private EditText    txt_hash;
    private EditText    txt_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        container_addTitle = (FrameLayout) findViewById(R.id.activity_preview_container_addtitle);
        container_addTitle.setVisibility(View.GONE);
        container_description = (FrameLayout) findViewById(R.id.activity_preview_container_description);
        container_description.setVisibility(View.GONE);

        /*-----------------Title---------------*/
        txt_title = (EditText) findViewById(R.id.activity_preview_txt_title);
        /*---------------Description---------------*/
        txt_description = (EditText) findViewById(R.id.activity_preview_txt_description);
        txt_hash = (EditText) findViewById(R.id.activity_preview_txt_hash);

        initFont();
    }

    // Button Events
    protected void onTitle(View v) {
        container_addTitle.setVisibility(View.VISIBLE);
    }

    protected void onAddTitle(View v) {
        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        container_addTitle.setVisibility(View.GONE);
        txt_title.setText("");
        // TODO: 9/15/2016, Add title on Captured Video

    }

    protected void onSave(View v) {
        if (container_description.getVisibility() == View.GONE) {
            container_description.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(this, ExplorerTimelineActivity.class);
            intent.putExtra("upload", true);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    protected void onExplorer(View v) {

    }

    protected void onCancel(View v) {
        Intent intent = new Intent(this, CamCaptureActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            Intent intent = new Intent(this, CamCaptureActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initFont() {
        EditText txt_title = (EditText) findViewById(R.id.activity_preview_txt_title);
        txt_title.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
        txt_title.setHintTextColor(getResources().getColor(R.color.colorAKTextGrey));

        TextView btn_addTitle = (TextView) findViewById(R.id.activity_preview_btn_addtitle);
        btn_addTitle.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_roboto)));

        EditText txt_describe = (EditText) findViewById(R.id.activity_preview_txt_description);
        txt_describe.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_roboto)));
        txt_describe.setHintTextColor(getResources().getColor(R.color.colorAKDarkGrey));

        EditText txt_hash = (EditText) findViewById(R.id.activity_preview_txt_hash);
        txt_hash.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_roboto)));
        txt_hash.setHintTextColor(getResources().getColor(R.color.colorAKDarkGrey));

        TextView lbl_location = (TextView) findViewById(R.id.activity_preview_label_location);
        lbl_location.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_roboto)));

        Button btn_save = (Button) findViewById(R.id.activity_preview_btn_save);
        btn_save.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
    }
}
