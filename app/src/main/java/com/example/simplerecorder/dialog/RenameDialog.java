package com.example.simplerecorder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.example.simplerecorder.R;
import com.example.simplerecorder.databinding.DialogAudioRenameBinding;

/**
 * @PackageName: com.example.simplerecorder.dialog
 * @ClassName: RenameDialog
 * @Author: winwa
 * @Date: 2023/3/11 8:11
 * @Description:
 **/
public class RenameDialog extends Dialog implements View.OnClickListener {
    private DialogAudioRenameBinding mBinding;

    private String mContent;

    private OnOkClickListener mOnOkClickListener;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            InputMethodManager methodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            return false;
        }
    });

    public RenameDialog(@NonNull Context context, String content) {
        super(context);
        mContent = content;
    }

    public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
        mOnOkClickListener = onOkClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogAudioRenameBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.dialogAudioRenameEt.setText(mContent);

        mBinding.dialogAudioRenameOkBtn.setOnClickListener(this);
        mBinding.dialogAudioRenameCancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_audio_rename_ok_btn:
                if (mOnOkClickListener != null) {
                    String name = mBinding.dialogAudioRenameEt.getText().toString().trim();
                    mOnOkClickListener.onOkClick(name);
                }
                break;
            case R.id.dialog_audio_rename_cancel_btn:
                break;
        }
        cancel();
    }

    public void setDialogSize() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        attributes.width = defaultDisplay.getWidth();
        attributes.gravity = Gravity.BOTTOM;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(attributes);

        mHandler.sendEmptyMessageDelayed(1, 100);
    }

    public interface OnOkClickListener {
        public void onOkClick(String newName);
    }
}
