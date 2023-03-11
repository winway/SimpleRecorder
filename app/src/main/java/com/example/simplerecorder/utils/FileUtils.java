package com.example.simplerecorder.utils;

import java.io.File;

/**
 * @PackageName: com.example.simplerecorder.utils
 * @ClassName: FileUtils
 * @Author: winwa
 * @Date: 2023/3/11 8:11
 * @Description:
 **/
public class FileUtils {
    public static void deleteFileByPath(String path) {
        File file = new File(path);
        file.getAbsoluteFile().delete();
    }

    public static void renameFileByPath(String srcPath, String newPath) {
        File srcFile = new File(srcPath);
        File newFile = new File(newPath);
        srcFile.renameTo(newFile);
    }
}
