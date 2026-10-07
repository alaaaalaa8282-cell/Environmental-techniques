package com.abumohamed.ecotools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {

    public static final int REQUEST_CODE = 100;

    public static String[] required() {
        List<String> list = new ArrayList<>();
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT >= 33) {
            list.add(Manifest.permission.READ_MEDIA_IMAGES);
            list.add(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT <= 32) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        return list.toArray(new String[0]);
    }

    public static boolean hasLocation(Activity a) {
        return a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || a.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestMissing(Activity a) {
        if (Build.VERSION.SDK_INT < 23) return;
        List<String> missing = new ArrayList<>();
        for (String p : required()) {
            if (a.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) missing.add(p);
        }
        if (!missing.isEmpty()) {
            a.requestPermissions(missing.toArray(new String[0]), REQUEST_CODE);
        }
    }
}
