package com.example.simplerecorder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplerecorder.adapter.AudioListAdapter;
import com.example.simplerecorder.bean.AudioBean;
import com.example.simplerecorder.databinding.ActivityAudioListBinding;
import com.example.simplerecorder.utils.AudioUtils;
import com.example.simplerecorder.utils.SDCardUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AudioListActivity extends AppCompatActivity {

    private ActivityAudioListBinding mBinding;

    private AudioListAdapter mListAdapter;
    private List<AudioBean> mListAdapterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityAudioListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initAudioListAdapter();

        loadAudioList();
    }

    private void loadAudioList() {
        File audioDir = new File(SDCardUtils.sAppAudioDirPath);
        File[] audioFiles = audioDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if (new File(file, s).isDirectory()) {
                    return false;
                }
                if (s.endsWith(".mp3") || s.endsWith(".amr")) {
                    return true;
                }
                return false;
            }
        });

        AudioUtils audioUtils = AudioUtils.getInstance();
        for (int i = 0; i < audioFiles.length; i++) {
            String id = i + "";
            File file = audioFiles[i];
            String name = file.getName();
            String title = name.substring(0, name.lastIndexOf("."));
            String fileSuffix = name.substring(name.lastIndexOf("."));
            long lastModified = file.lastModified();
            String time = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA).format(lastModified);
            String path = file.getAbsolutePath();
            String duration = audioUtils.getAudioTimeFormatDuration(path);
            long size = file.length();
            long durationSeconds = audioUtils.getAudioDuration(path);

            AudioBean audioBean = new AudioBean(id, title, time, duration, path, fileSuffix, size, durationSeconds, lastModified);
            mListAdapterData.add(audioBean);
        }
        audioUtils.releaseMediaMetadataRetriever();

        Collections.sort(mListAdapterData, new Comparator<AudioBean>() {
            @Override
            public int compare(AudioBean audioBean, AudioBean t1) {
                if (audioBean.getLastModified() > t1.getLastModified()) {
                    return 1;
                } else if (audioBean.getLastModified() == t1.getLastModified()) {
                    return 0;
                }
                return -1;
            }
        });

//        Collections.sort(mListAdapterData, Collections.reverseOrder(new Comparator<AudioBean>() {
//            @Override
//            public int compare(AudioBean audioBean, AudioBean t1) {
//                if (audioBean.getLastModified() > t1.getLastModified()) {
//                    return 1;
//                } else if (audioBean.getLastModified() == t1.getLastModified()) {
//                    return 0;
//                }
//                return -1;
//            }
//        }));

        mListAdapter.notifyDataSetChanged();
    }

    private void initAudioListAdapter() {
        mListAdapterData = new ArrayList<>();
        mListAdapter = new AudioListAdapter(this, mListAdapterData);
        mBinding.audioListLv.setAdapter(mListAdapter);
    }
}