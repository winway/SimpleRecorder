package com.example.simplerecorder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.simplerecorder.bean.AudioBean;
import com.example.simplerecorder.databinding.DialogAudioDetailBinding;

import java.text.DecimalFormat;

/**
 * @PackageName: com.example.simplerecorder.dialog
 * @ClassName: DetailDialog
 * @Author: winwa
 * @Date: 2023/3/11 10:18
 * @Description:
 **/
public class DetailDialog extends Dialog {
    private DialogAudioDetailBinding mBinding;

    public DetailDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogAudioDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.dialogAudioDetailCloseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    public void setContent(AudioBean audioBean) {
        mBinding.dialogAudioDetailNameContentTv.setText(audioBean.getTitle());
        mBinding.dialogAudioDetailTimeContentTv.setText(audioBean.getTime());
        mBinding.dialogAudioDetailSizeContentTv.setText(formatSizeString(audioBean.getSize()));
        mBinding.dialogAudioDetailPathContentTv.setText(audioBean.getPath());
    }

    private String formatSizeString(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (size >= 1024 * 1024) {
            return decimalFormat.format(size * 1.0 / (1024 * 1024)) + "MB";
        } else if (size >= 1024) {
            return decimalFormat.format(size * 1.0 / (1024)) + "KB";
        } else {
            return size + "B";
        }
    }

    public void setDialogSize() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        attributes.width = defaultDisplay.getWidth() - 30;
        attributes.gravity = Gravity.BOTTOM;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(attributes);
    }
}
