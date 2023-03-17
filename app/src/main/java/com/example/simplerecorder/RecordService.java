package com.example.simplerecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.simplerecorder.utils.SDCardUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordService extends Service {
    private static final String TAG = "RecordService";

    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startRecord() {
        Log.i(TAG, "startRecord: ");

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        isRecording = true;

        mMediaRecorder.reset();

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mMediaRecorder.setOutputFile(getRecordFilePath());
        mMediaRecorder.setMaxDuration(10 * 60 * 1000);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder = null;
        }

        isRecording = false;
    }

    private String getRecordFilePath() {
        Log.i(TAG, "getRecordFilePath: " + SDCardUtils.sAppAudioDirPath);

        File dir = new File(SDCardUtils.sAppAudioDirPath);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File file = new File(SDCardUtils.sAppAudioDirPath, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".amr");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file.getAbsolutePath();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new RecorderBinder();
    }

    public class RecorderBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }
}