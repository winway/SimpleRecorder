package com.example.simplerecorder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplerecorder.databinding.ActivityRecordBinding;
import com.example.simplerecorder.utils.LaunchSystemActivityUtils;

public class RecordActivity extends AppCompatActivity {

    private ActivityRecordBinding mBinding;

    private RecordService mRecordService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RecordService.RecorderBinder binder = (RecordService.RecorderBinder) iBinder;
            mRecordService = binder.getService();
            mRecordService.setOnRefreshUIListener(new RecordService.OnRefreshUIListener() {
                @Override
                public void onRefresh(int db, String duration) {
                    mBinding.recordVoicelineVL.setVolume(db);
                    mBinding.recordDurationTv.setText(duration);
                }
            });
            mRecordService.startRecord();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityRecordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_back_iv:
                LaunchSystemActivityUtils.launchHome(this);
                break;
            case R.id.record_stop_iv:
                mRecordService.stopRecord();
                startActivity(new Intent(this, AudioListActivity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LaunchSystemActivityUtils.launchHome(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}