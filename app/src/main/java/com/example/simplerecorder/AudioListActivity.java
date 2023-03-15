package com.example.simplerecorder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplerecorder.adapter.AudioListAdapter;
import com.example.simplerecorder.bean.AudioBean;
import com.example.simplerecorder.databinding.ActivityAudioListBinding;
import com.example.simplerecorder.dialog.DetailDialog;
import com.example.simplerecorder.dialog.RenameDialog;
import com.example.simplerecorder.utils.AudioUtils;
import com.example.simplerecorder.utils.DialogUtils;
import com.example.simplerecorder.utils.FileUtils;
import com.example.simplerecorder.utils.LaunchSystemActivityUtils;
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
    private static final String TAG = "AudioListActivity";

    private ActivityAudioListBinding mBinding;

    private AudioListAdapter mListAdapter;
    private List<AudioBean> mListAdapterData;

    private AudioService mAudioService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioService.AudioBinder audioBinder = (AudioService.AudioBinder) iBinder;
            mAudioService = audioBinder.getService();
            mAudioService.setPlayList(mListAdapterData);
            Log.i(TAG, "onServiceConnected: ");
            mAudioService.setOnChangeAudioListener(new AudioService.OnChangeAudioListener() {
                @Override
                public void onChangeAudio(int position) {
                    mListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityAudioListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initAudioListAdapter();

        loadAudioList();

        setupAudioListAdapter();

        mBinding.audioListRecordIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAudioService.closeMediaPlayer();
                startActivity(new Intent(AudioListActivity.this, RecordActivity.class));
                finish();
            }
        });

        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LaunchSystemActivityUtils.launchHome(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupAudioListAdapter() {
        mListAdapter.setOnPlayClickListener(new AudioListAdapter.OnPlayClickListener() {
            @Override
            public void OnPlayClick(AudioListAdapter audioListAdapter, View itemView, View playIV, int position) {
                for (int i = 0; i < mListAdapterData.size(); i++) {
                    if (i != position) {
                        mListAdapterData.get(i).setPlaying(false);
                    }
                }
                boolean playing = mListAdapterData.get(position).isPlaying();
                mListAdapterData.get(position).setPlaying(!playing);
                mListAdapter.notifyDataSetChanged();
                mAudioService.play(position);
            }
        });

        mBinding.audioListLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupMenu(view, i);
                return false;
            }
        });
    }

    private void showPopupMenu(View view, int i) {
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.RIGHT);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.audio_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.audio_detail:
                        showDetailDialog(i);
                        break;
                    case R.id.audio_delete:
                        mAudioService.closeMediaPlayer();
                        deleteAudio(i);
                        break;
                    case R.id.audio_rename:
                        showRenameDialog(i);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showDetailDialog(int i) {
        AudioBean audioBean = mListAdapterData.get(i);
        DetailDialog dialog = new DetailDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setContent(audioBean);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void showRenameDialog(int i) {
        AudioBean audioBean = mListAdapterData.get(i);
        RenameDialog dialog = new RenameDialog(this, audioBean.getTitle());
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnOkClickListener(new RenameDialog.OnOkClickListener() {
            @Override
            public void onOkClick(String newName) {
                renameAudio(newName, i);
            }
        });
    }

    private void renameAudio(String newName, int i) {
        AudioBean audioBean = mListAdapterData.get(i);
        if (audioBean.getTitle().equals(newName)) {
            return;
        }

        String srcPath = audioBean.getPath();
        String fileSuffix = audioBean.getFileSuffix();
        File srcFile = new File(srcPath);
        String newPath = srcFile.getParent() + File.separator + newName + fileSuffix;

        FileUtils.renameFileByPath(srcPath, newPath);

        audioBean.setTitle(newName);
        audioBean.setPath(newPath);
        mListAdapter.notifyDataSetChanged();
    }

    private void deleteAudio(int i) {
        AudioBean audioBean = mListAdapterData.get(i);
        String path = audioBean.getPath();
        DialogUtils.showDialog(this, "提示信息", "确认删除文件吗？",
                "确定", new DialogUtils.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClick() {
                        FileUtils.deleteFileByPath(path);
                        mListAdapterData.remove(audioBean);
                        mListAdapter.notifyDataSetChanged();

                        mAudioService.setPlayList(mListAdapterData);
                    }
                },
                "取消", null);
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