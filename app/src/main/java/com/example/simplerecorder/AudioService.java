package com.example.simplerecorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.example.simplerecorder.bean.AudioBean;

import java.util.List;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "AudioService";
    private static final String ACTION_CLOSE = "com.example.simplerecorder.close";
    private static final String ACTION_PREV = "com.example.simplerecorder.prev";
    private static final String ACTION_PLAY = "com.example.simplerecorder.play";
    private static final String ACTION_NEXT = "com.example.simplerecorder.next";
    private static final int REQUEST_NOTIFICATION_CLOSE = 1;
    private static final int REQUEST_NOTIFICATION_PREV = 1;
    private static final int REQUEST_NOTIFICATION_PLAY = 1;
    private static final int REQUEST_NOTIFICATION_NEXT = 1;
    private static final int NOTIFICATION_ID_AUDIO = 100;

    private MediaPlayer mMediaPlayer = null;

    private List<AudioBean> mPlayList;
    private int mPlayPosition = -1;

    private boolean mShouldUpdateProgress = true;

    private AudioReceiver mAudioReceiver;
    private RemoteViews mRemoteViews;
    private NotificationManager mNotificationManager;
    private Notification mNotification;

    private OnChangeAudioListener mOnChangeAudioListener;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            updateAudioListView(mPlayPosition);
            return true;
        }
    });

    public AudioService() {
    }

    public void setShouldUpdateProgress(boolean shouldUpdateProgress) {
        mShouldUpdateProgress = shouldUpdateProgress;
    }

    public void setOnChangeAudioListener(OnChangeAudioListener onChangeAudioListener) {
        mOnChangeAudioListener = onChangeAudioListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initReceiver();
        initRemoteViews();
        initNotification();
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.icon_app_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_app_logo))
                .setContent(mRemoteViews)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_HIGH);
        mNotification = builder.build();
    }

    private void updateNotification(int position) {
        if (mMediaPlayer.isPlaying()) {
            mRemoteViews.setImageViewResource(R.id.notify_audio_play_iv, R.mipmap.red_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.notify_audio_play_iv, R.mipmap.red_play);
        }

        mRemoteViews.setTextViewText(R.id.notify_audio_title_tv, mPlayList.get(position).getTitle());
        mRemoteViews.setTextViewText(R.id.notify_audio_duration_tv, mPlayList.get(position).getDuration());

        mNotificationManager.notify(NOTIFICATION_ID_AUDIO, mNotification);
    }

    private void initRemoteViews() {
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notify_audio);

        PendingIntent closePI = PendingIntent.getBroadcast(this, REQUEST_NOTIFICATION_CLOSE, new Intent(ACTION_CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_audio_close_iv, closePI);

        PendingIntent prevPI = PendingIntent.getBroadcast(this, REQUEST_NOTIFICATION_PREV, new Intent(ACTION_PREV), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_audio_prev_iv, prevPI);

        PendingIntent playPI = PendingIntent.getBroadcast(this, REQUEST_NOTIFICATION_PLAY, new Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_audio_play_iv, playPI);

        PendingIntent nextPI = PendingIntent.getBroadcast(this, REQUEST_NOTIFICATION_NEXT, new Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_audio_next_iv, nextPI);
    }

    private void initReceiver() {
        mAudioReceiver = new AudioReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLOSE);
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_NEXT);
        registerReceiver(mAudioReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return new AudioBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    public void setPlayList(List<AudioBean> playList) {
        if (playList != null) {
            mPlayList = playList;
        }
    }

    public void play(int position) {
        int prevPosition = mPlayPosition;
        if (prevPosition == -1 || prevPosition != position) {
            playChange(prevPosition, position);
        } else {
            pauseOrPlay();
        }
    }

    public void playChange(int fromPosition, int toPosition) {
        if (mPlayList.size() <= 0) {
            return;
        }
        mPlayPosition = toPosition;

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(mPlayList.get(toPosition).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mPlayList.get(toPosition).setPlaying(true);
            if (fromPosition != -1) {
                mPlayList.get(fromPosition).setPlaying(false);
            }

            updateAudioListView(toPosition);

            setShouldUpdateProgress(true);
            updateProgress();

            updateNotification(toPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseOrPlay() {
        AudioBean audioBean = mPlayList.get(mPlayPosition);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            audioBean.setPlaying(false);
        } else {
            mMediaPlayer.start();
            audioBean.setPlaying(true);
        }

        updateAudioListView(mPlayPosition);

        updateNotification(mPlayPosition);
    }

    private void pause() {
        AudioBean audioBean = mPlayList.get(mPlayPosition);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            audioBean.setPlaying(false);
        }

        updateAudioListView(mPlayPosition);

        updateNotification(mPlayPosition);
    }

    public void updateAudioListView(int position) {
        if (mOnChangeAudioListener != null) {
            mOnChangeAudioListener.onChangeAudio(position);
        }
    }

    public void updateProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mShouldUpdateProgress) {
                    if (mPlayList == null || mPlayList.size() < 1 || mPlayPosition > mPlayList.size() - 1) {
                        break;
                    }
                    long durationSeconds = mPlayList.get(mPlayPosition).getDurationSeconds();
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    int progress = (int) (currentPosition * 100 / durationSeconds);
                    mPlayList.get(mPlayPosition).setCurrentProgress(progress);

                    mHandler.sendEmptyMessageDelayed(1, 1000);

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAudioReceiver != null) {
            unregisterReceiver(mAudioReceiver);
        }

        closeMediaPlayer();
    }

    public void closeMediaPlayer() {
        if (mMediaPlayer != null) {
            setShouldUpdateProgress(false);
            pause();
            mNotificationManager.cancel(NOTIFICATION_ID_AUDIO);
            mMediaPlayer.stop();
            mPlayPosition = -1;
        }
    }

    public interface OnChangeAudioListener {
        public void onChangeAudio(int position);
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    class AudioReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_CLOSE:
                    pause();
                    mNotificationManager.cancel(NOTIFICATION_ID_AUDIO);
                    break;
                case ACTION_PREV:
                    playChange(mPlayPosition, mPlayPosition - 1 < 0 ? mPlayList.size() - 1 : mPlayPosition - 1);
                    break;
                case ACTION_PLAY:
                    pauseOrPlay();
                    break;
                case ACTION_NEXT:
                    playChange(mPlayPosition, mPlayPosition + 1 > mPlayList.size() - 1 ? 0 : mPlayPosition + 1);
                    break;
            }
        }
    }
}