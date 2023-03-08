package com.example.simplerecorder.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @PackageName: com.example.simplerecorder.utils
 * @ClassName: SDCardUtils
 * @Author: winwa
 * @Date: 2023/3/6 8:34
 * @Description:
 **/
public class SDCardUtils {
    private static final String TAG = "SDCardUtils";

    public static String sAppRootDirPath;
    public static String sAppAudioDirPath;

    private static SDCardUtils sSDCardUtils;

    private SDCardUtils() {
    }

    public static SDCardUtils getInstance() {
        if (sSDCardUtils == null) {
            synchronized (SDCardUtils.class) {
                if (sSDCardUtils == null) {
                    sSDCardUtils = new SDCardUtils();
                }
            }
        }

        return sSDCardUtils;
    }

    public boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public File createAppRootDir() {
        if (hasSDCard()) {
            Log.i(TAG, "createAppRootDir: ");
            File sdDir = Environment.getExternalStorageDirectory();
            File appDir = new File(sdDir, "simple_recorder");
            if (!appDir.exists()) {
                appDir.mkdir();
            }

            sAppRootDirPath = appDir.getAbsolutePath();
            return appDir;
        }

        return null;
    }

    public File createAppChildDir(String dir) {
        Log.i(TAG, "createAppChildDir: " + dir);
        File rootDir = createAppRootDir();
        if (rootDir != null) {
            File childDir = new File(rootDir, dir);
            if (!childDir.exists()) {
                childDir.mkdir();
            }

            return childDir;
        }

        return null;
    }
}
