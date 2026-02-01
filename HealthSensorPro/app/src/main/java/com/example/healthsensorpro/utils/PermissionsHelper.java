package com.example.healthsensorpro.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionsHelper {
    public static final int PERMISSION_REQUEST_CODE = 100;

    public static String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();

        // Body sensors permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        // High sampling rate sensors (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.HIGH_SAMPLING_RATE_SENSORS);
        }

        // Body sensors for heart rate
        permissions.add(Manifest.permission.BODY_SENSORS);

        return permissions.toArray(new String[0]);
    }

    public static boolean hasAllPermissions(Context context) {
        String[] permissions = getRequiredPermissions();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void requestPermissions(Activity activity) {
        String[] permissions = getRequiredPermissions();
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        String[] permissions = getRequiredPermissions();

        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }
}