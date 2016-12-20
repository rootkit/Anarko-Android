package com.oppous.anarko;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oppous.anarko.adapters.MaskListAdapter;
import com.oppous.anarko.camera.AKCameraManager;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.CircleProgressView;
import com.oppous.anarko.classes.FontManager;
import com.oppous.anarko.classes.HorizontalListView;
import com.oppous.anarko.listeners.OnSwipeTouchListener;


import java.util.Map;

public class CamCaptureActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "CamCaptureActivity";

    private HorizontalListView  listMask;
    private ProgressBar         progressBarRecord;
    private CircleProgressView  btn_progress;
    private LinearLayout        container_mask;
    private FrameLayout         container_buttons;
    private ImageView           btn_record;
    private ImageView           btn_switch;
    private ImageView           btn_explorer;
    private ImageView           btn_flash;
    private ImageView           btn_store;
    private TextView            btn_buy;

    /*Visage CameraTrack------------------------------------------*/
    private AKCameraManager         mCameraManager;

    /*------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        listMask = (HorizontalListView)findViewById(R.id.activity_camera_listview_mask);
        String[] maskListValues = new String[] {
                "mask1", "mask2", "mask3", "mask4", "mask5", "mask6", "mask7"
        };
        int[] uriAry = new int[] {
            R.drawable.mask_clown_normal, R.drawable.mask_clown_talk, R.drawable.mask_clown_wink, R.drawable.mask_moneky_normal,
                R.drawable.mask_moneky_wink, R.drawable.mask_skimask_normal, R.drawable.mask_skimask_wink
        };

        MaskListAdapter adapter = new MaskListAdapter(this, maskListValues, uriAry);
        listMask.setAdapter(adapter);
        listMask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //mCameraManager.setMask(); // apply AKMask when clicked the mask item on listview
            }
        });

        btn_progress = (CircleProgressView) findViewById(R.id.activity_camera_btn_progress);
        btn_progress.setmCallback(new CircleProgressView.CircleProgressCallback() {
            @Override
            public void onError(int progress1) {

            }

            @Override
            public void onFinish(int progress1) {
                gotoPreviewActivity();
            }

            @Override
            public void onProgress(int progress1) {

            }
        });

        btn_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_progress.isStarted()) {
                    btn_progress.stop();
                    gotoPreviewActivity();
                }
            }
        });

        container_mask = (LinearLayout) findViewById(R.id.activity_camera_container_mask);
        container_buttons = (FrameLayout) findViewById(R.id.activity_camera_container_buttons);
        btn_record      = (ImageView) findViewById(R.id.activity_camera_btn_record);
        btn_switch      = (ImageView) findViewById(R.id.activity_camera_btn_switchcamera);
        btn_explorer    = (ImageView) findViewById(R.id.activity_camera_btn_explorer);
        btn_flash       = (ImageView) findViewById(R.id.activity_camera_btn_flash);
        btn_store       = (ImageView) findViewById(R.id.activity_camera_btn_break);
        btn_buy         = (TextView) findViewById(R.id.activity_camera_btn_buy);

        btn_record.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
        btn_explorer.setOnClickListener(this);
        btn_flash.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        btn_buy.setOnClickListener(this);



        FrameLayout cameraPriviewContainer = (FrameLayout) findViewById(R.id.activity_camera_preview_container);
        cameraPriviewContainer.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                Button btn_buy = (Button) findViewById(R.id.activity_camera_btn_buy);
                btn_buy.setVisibility(View.VISIBLE);
            }

            public void onSwipeRight() {
                Button btn_buy = (Button) findViewById(R.id.activity_camera_btn_buy);
                btn_buy.setVisibility(View.VISIBLE);
            }
        });

        /*Visage CameraTrack----------------------------------------------------*/
        mCameraManager = new AKCameraManager(this);

        mCameraManager.setCameraPreviewContainer(cameraPriviewContainer);
        mCameraManager.removeCameraView();
        mCameraManager.setOnCameraActionListener(new AKCameraManager.OnCameraActionListener() {

            @Override
            public void onRecordStoped() {
                Intent intent = new Intent(CamCaptureActivity.this, CamPreviewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        /*----------------------------------------------------------*/
        if (mCameraManager.getNumberOfCameras() <= 1) {
            btn_switch.setVisibility(View.GONE);
        } else {
            btn_switch.setVisibility(View.VISIBLE);
        }

        initFont();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            Map<String,String> map = AppManager.getInstance().getSession();
            String clientID = map.get("clientID");
            String token = map.get("token");

            if (clientID != null && token != null){
                return false;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Map<String,String> map = AppManager.getInstance().getSession();
//        String clientID = map.get("clientID");
//        String token = map.get("token");

//        if (clientID == null || token == null){
//            //show tour video & register phone.
//            Intent intent = new Intent(this, RegTourActivity.class);
//            startActivity(intent);
//        }

        if (btn_progress != null) {
            btn_progress.setVisibility(View.GONE);
        }

        if (btn_record != null) {
            btn_record.setVisibility(View.VISIBLE);
        }

        if (container_mask != null) {
            container_mask.setVisibility(View.VISIBLE);
        }

        if (container_buttons != null) {
            container_buttons.setVisibility(View.VISIBLE);
        }

        if (mCameraManager != null) {
            mCameraManager.createCameraView();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraManager != null)
            mCameraManager.removeCameraView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraManager != null)
            mCameraManager.removeCameraView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_camera_btn_record:
                onRecord();
                break;
            case R.id.activity_camera_btn_switchcamera:
                onSwitchCamera();
                break;
            case R.id.activity_camera_btn_explorer:
                onExplorer();
                break;
            case R.id.activity_camera_btn_flash:
                onFlash();
                break;
            case R.id.activity_camera_btn_break:
                onStore();
                break;
            case R.id.activity_camera_btn_buy:
                onBuy();
                break;
            default:
                break;
        }
    }

    // Button Events
    protected void onRecord() {
        container_buttons.setVisibility(View.GONE);
        container_mask.setVisibility(View.GONE);
        /*RecordButtonNormal*/
        Animation fade_out_center = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_center_for_record);
        btn_record.setVisibility(View.GONE);
        btn_record.startAnimation(fade_out_center);
        /*RecordButtonProgress*/
        Animation fade_in_center = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_center_for_record);
        btn_progress.setVisibility(View.VISIBLE);
        btn_progress.startAnimation(fade_in_center);

        fade_in_center.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn_progress.start();
                mCameraManager.startRecord();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    protected void onBuy() {
        Intent intent = new Intent(this, PayMethodActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    protected void onSwitchCamera() {
        mCameraManager.switchCamera();
    }


    protected void onExplorer() {
        Intent intent = new Intent(this, ExplorerTimelineActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    protected void onFlash() {

    }

    protected void onStore(){
        Intent intent = new Intent(this, PayStoreActivity.class);
        startActivity(intent);
    }

    private void initFont() {
        TextView txt_break = (TextView) findViewById(R.id.activity_camera_btn_buy);
        txt_break.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_roboto)));

        Button btn_buy = (Button) findViewById(R.id.activity_camera_btn_buy);
        btn_buy.setTypeface(FontManager.getCustomFont(this, this.getResources().getString(R.string.font_gunplay)));
        btn_buy.setVisibility(View.GONE);
    }

    public void gotoPreviewActivity() {

        Animation fade_out_center = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_center_for_record);
        btn_progress.startAnimation(fade_out_center);
        fade_out_center.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn_progress.setProgress(0);
                btn_progress.setVisibility(View.GONE);
                mCameraManager.stopRecord();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
    }

}
