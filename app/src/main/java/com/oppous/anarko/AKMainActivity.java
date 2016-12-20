package com.oppous.anarko;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.oppous.anarko.classes.APIManger;
import com.oppous.anarko.classes.AppManager;
import com.oppous.anarko.classes.PermissionManager;

public class AKMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        AppManager.getInstance().setContext(getApplicationContext());
        APIManger.getInstance().setContext(getApplicationContext());

        /*---------------Permissions-----------------------*/
        int[] permissions = {PermissionManager.CAMERA, PermissionManager.RECORD_AUDIO, PermissionManager.READ_EXTERNAL_STORAGE};
        PermissionManager.requestPermissions(this, permissions, 1);
        /*---------------FullScreen Mode------------------*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*------------------------------------------------*/


    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {

                    int i = 2;
                    Intent intent;

                    if (i == 1) {
                        intent = new Intent(this, SettingsMainActivity.class);
                    } else if (i == 2) {
                        intent = new Intent(this, CamCaptureActivity.class);
                    } else {
                        intent = new Intent(this, CamCaptureActivity.class);
                    }

                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
