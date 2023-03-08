package com.example.simplerecorder.utils;

import android.media.MediaMetadataRetriever;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @PackageName: com.example.simplerecorder.utils
 * @ClassName: AudioUtils
 * @Author: winwa
 * @Date: 2023/3/8 8:17
 * @Description:
 **/
public class AudioUtils {
    private static AudioUtils sAudioUtils;

    private MediaMetadataRetriever mMediaMetadataRetriever;

    private AudioUtils() {
    }

    public static AudioUtils getInstance() {
        if (sAudioUtils == null) {
            synchronized (AudioUtils.class) {
                if (sAudioUtils == null) {
                    sAudioUtils = new AudioUtils();
                }
            }
        }

        return sAudioUtils;
    }

    public long getAudioDuration(String path) {
        long duration = 0;
        if (mMediaMetadataRetriever == null) {
            mMediaMetadataRetriever = new MediaMetadataRetriever();
        }

        mMediaMetadataRetriever.setDataSource(path);
        String s = mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong(s);
        return duration;
    }

    public String getAudioTimeFormatDuration(String format, String path) {
        long duration = getAudioDuration(path);
        return new SimpleDateFormat(format, Locale.CHINA).format(new Date(duration));
    }

    public String getAudioTimeFormatDuration(String path) {
        return getAudioTimeFormatDuration("HH:mm:ss", path);
    }

    public String getAudioArtist(String path) {
        if (mMediaMetadataRetriever == null) {
            mMediaMetadataRetriever = new MediaMetadataRetriever();
        }

        mMediaMetadataRetriever.setDataSource(path);
        String artist = mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        return artist;
    }

    public void releaseMediaMetadataRetriever() {
        if (mMediaMetadataRetriever != null) {
            mMediaMetadataRetriever.release();
            mMediaMetadataRetriever = null;
        }
    }
}
