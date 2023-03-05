package com.example.simplerecorder.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * @PackageName: com.example.simplerecorder.utils
 * @ClassName: LanuchSystemActivityUtils
 * @Author: winwa
 * @Date: 2023/3/5 17:58
 * @Description:
 **/
public class LaunchSystemActivityUtils {
    public static void launchAppSetting(Activity context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
