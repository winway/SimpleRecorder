package com.example.simplerecorder.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * @PackageName: com.example.simplerecorder.dialog
 * @ClassName: PermissionDialog
 * @Author: winwa
 * @Date: 2023/3/5 17:44
 * @Description:
 **/
public class DialogUtils {

    public static void showPermissionGuideDialog(Context context, String title, String message
            , String positive, OnPositiveClickListener positiveClickListener
            , String negative, OnNegativeClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (negativeClickListener != null) {
                            negativeClickListener.onNegativeClick();
                        }
                    }
                })
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (positiveClickListener != null) {
                            positiveClickListener.onPositiveClick();
                        }
                    }
                }).create().show();
    }

    public interface OnPositiveClickListener {
        public void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        public void onNegativeClick();
    }
}
