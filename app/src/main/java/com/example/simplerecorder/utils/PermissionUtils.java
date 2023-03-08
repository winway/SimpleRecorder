package com.example.simplerecorder.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName: com.example.simplerecorder.utils
 * @ClassName: PermissionUtils
 * @Author: winwa
 * @Date: 2023/3/5 12:12
 * @Description:
 **/
public class PermissionUtils {
    private final static int REQUEST_CODE_PERMISSION = 100;

    private static PermissionUtils sPermissionUtils;

    private OnPermissionResultListener mOnPermissionResultListener;

    private PermissionUtils() {
    }

    public static PermissionUtils getInstance() {
        if (sPermissionUtils == null) {
            synchronized (PermissionUtils.class) {
                if (sPermissionUtils == null) {
                    sPermissionUtils = new PermissionUtils();
                }
            }
        }

        return sPermissionUtils;
    }

    public void requestPermissions(Activity context, String[] permissions, OnPermissionResultListener listener) {
        mOnPermissionResultListener = listener;

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> pendingPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    pendingPermissionList.add(permissions[i]);
                }
            }

            if (pendingPermissionList.size() > 0) {
                String[] pendingPermissions = pendingPermissionList.toArray(new String[pendingPermissionList.size()]);
                ActivityCompat.requestPermissions(context, pendingPermissions, REQUEST_CODE_PERMISSION);
            } else {
                mOnPermissionResultListener.onGranted();
            }
        }
    }

    public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            List<String> deniedPermissions = new ArrayList<>();

            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
            }

            if (deniedPermissions.size() == 0) {
                mOnPermissionResultListener.onGranted();
            } else {
                mOnPermissionResultListener.onDenied(deniedPermissions);
            }
        } else {
            mOnPermissionResultListener.onGranted();
        }
    }

    public void showPermissionGuideDialog(Activity context) {
        DialogUtils.showDialog(context, "提示信息", "权限缺失，请手动开启所需权限"
                , "确定", new DialogUtils.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClick() {
                        LaunchSystemActivityUtils.launchAppSetting(context);
                        context.finish();
                    }
                }, "取消", new DialogUtils.OnNegativeClickListener() {
                    @Override
                    public void onNegativeClick() {
                        context.finish();
                    }
                });
    }

    public interface OnPermissionResultListener {
        void onGranted();

        void onDenied(List<String> deniedPermissions);
    }
}
