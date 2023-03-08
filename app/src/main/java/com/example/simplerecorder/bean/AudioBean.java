package com.example.simplerecorder.bean;

/**
 * @PackageName: com.example.simplerecorder.bean
 * @ClassName: AudioBean
 * @Author: winwa
 * @Date: 2023/3/7 8:01
 * @Description:
 **/
public class AudioBean {
    private String id;
    private String title;
    private String time;
    private String duration;

    private String path;
    private String fileSuffix;
    private long size;
    private long durationSeconds;
    private long lastModified;

    private boolean isPlaying = false;
    private int currentProgress = 0;

    public AudioBean() {
    }

    public AudioBean(String id, String title, String time, String duration, String path, String fileSuffix, long size, long durationSeconds, long lastModified) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.duration = duration;
        this.path = path;
        this.fileSuffix = fileSuffix;
        this.size = size;
        this.durationSeconds = durationSeconds;
        this.lastModified = lastModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }
}
