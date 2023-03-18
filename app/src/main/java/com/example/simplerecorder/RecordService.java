package com.example.simplerecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.example.simplerecorder.utils.SDCardUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordService extends Service {
    private static final String TAG = "RecordService";

    private static final int REQUEST_NOTIFICATION_RECORD = 1;
    private static final int NOTIFICATION_ID_RECORDER = 110;

    private MediaRecorder mMediaRecorder;

    private boolean isRecording = false;
    private int mRecordDuration;

    private RemoteViews mRemoteViews;

    private NotificationManager mNotificationManager;
    private Notification mNotification;

    private OnRefreshUIListener mOnRefreshUIListener;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (mMediaRecorder == null) {
                return false;
            }

            double ratio = (double) mMediaRecorder.getMaxAmplitude() / 100;
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
            }

            mRecordDuration += 1000;

            if (mOnRefreshUIListener != null) {
                String duration = new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(new Date(mRecordDuration - 8 * 3600 * 1000));
                mOnRefreshUIListener.onRefresh((int) db, duration);
                updateNotification(duration);
            }

            return false;
        }
    });

    public void setOnRefreshUIListener(OnRefreshUIListener onRefreshUIListener) {
        mOnRefreshUIListener = onRefreshUIListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initRemoteView();
        initNotification();
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder;
        // https://blog.csdn.net/congcongguniang/article/details/105705271
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, "winway");
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setSmallIcon(R.mipmap.icon_voice)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_voice))
                .setContent(mRemoteViews)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("winway", "android10", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotification = builder.build();
    }

    private void updateNotification(String duration) {
        mRemoteViews.setTextViewText(R.id.notify_record_duration_tv, duration);
        mNotificationManager.notify(NOTIFICATION_ID_RECORDER, mNotification);
    }

    private void closeNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID_RECORDER);
    }

    private void initRemoteView() {
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notify_record);
        Intent intent = new Intent(this, RecordActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, REQUEST_NOTIFICATION_RECORD, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_record_rl, pi);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecord();
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRecording) {
                        mHandler.sendEmptyMessage(0);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder = null;
        }

        closeNotification();

        isRecording = false;
        mRecordDuration = 0;
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

        File file = new File(SDCardUtils.sAppAudioDirPath, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date()) + ".amr");
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

    public interface OnRefreshUIListener {
        public void onRefresh(int db, String duration);
    }

    public class RecorderBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }
}