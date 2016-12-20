package com.oppous.anarko.classes;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 11/1/2016.
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";

    public static final int CAMERA = 0;
    public static final int RECORD_AUDIO = 1;
    public static final int READ_EXTERNAL_STORAGE = 2;

    private static PermissionManager ourInstance = new PermissionManager();

    public static PermissionManager getInstance() {
        return ourInstance;
    }

    private PermissionManager() {

    }

    public static void requestPermissions(Activity activity, int[] permissions, int requestCode) {

        String[] permissionStringAry = new String[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i] == CAMERA) {
                int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    permissionStringAry[i] = Manifest.permission.CAMERA;
                }
            }

            if (permissions[i] == RECORD_AUDIO) {
                int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    permissionStringAry[i] = Manifest.permission.RECORD_AUDIO;
                }
            }

            if (permissions[i] == READ_EXTERNAL_STORAGE) {
                int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    permissionStringAry[i] = Manifest.permission.READ_EXTERNAL_STORAGE;
                }
            }
        }

        ActivityCompat.requestPermissions(activity, permissionStringAry, requestCode);
    }


}
