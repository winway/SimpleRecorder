package com.example.simplerecorder;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simplerecorder.databinding.ActivityMainBinding;
import com.example.simplerecorder.utils.PermissionUtils;
import com.example.simplerecorder.utils.SDCardUtils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_COUNT_DOWN = 1;

    private ActivityMainBinding mBinding;

    private int mCounter = 3;
    private String[] mPermissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.mainCounterTv.setText(mCounter + "");

        PermissionUtils.getInstance().requestPermissions(this, mPermissions, mOnPermissionResultListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private PermissionUtils.OnPermissionResultListener mOnPermissionResultListener = new PermissionUtils.OnPermissionResultListener() {
        @Override
        public void onGranted() {
            File audioDir = SDCardUtils.getInstance().createAppChildDir("audio");
            SDCardUtils.sAppAudioDirPath = audioDir.getAbsolutePath();

            mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
        }

        @Override
        public void onDenied(List<String> deniedPermissions) {
            PermissionUtils.getInstance().showPermissionGuideDialog(MainActivity.this);
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == MSG_COUNT_DOWN) {
                mCounter -= 1;
                if (mCounter == 0) {
                    startActivity(new Intent(MainActivity.this, AudioListActivity.class));
                    finish();
                } else {
                    mBinding.mainCounterTv.setText(mCounter + "");
                    mHandler.sendEmptyMessageDelayed(MSG_COUNT_DOWN, 1000);
                }
            }
            return false;
        }
    });
}