package com.example.simplerecorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.simplerecorder.R;
import com.example.simplerecorder.bean.AudioBean;
import com.example.simplerecorder.databinding.LvitemAudioBinding;

import java.util.List;

/**
 * @PackageName: com.example.simplerecorder.adapter
 * @ClassName: AudioListAdapter
 * @Author: winwa
 * @Date: 2023/3/7 8:06
 * @Description:
 **/
public class AudioListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AudioBean> mData;

    public AudioListAdapter(Context context, List<AudioBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.lvitem_audio, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AudioBean audioBean = mData.get(i);
        holder.mItemBinding.lvitemAudioTitleTv.setText(audioBean.getTitle());
        holder.mItemBinding.lvitemAudioTimeTv.setText(audioBean.getTime());
        holder.mItemBinding.lvitemAudioDurationTv.setText(audioBean.getDuration() + "");
        if (audioBean.isPlaying()) {
            holder.mItemBinding.lvitemAudioControllerLl.setVisibility(View.VISIBLE);
            holder.mItemBinding.lvitemAudioControllerPb.setMax(100);
            holder.mItemBinding.lvitemAudioControllerPb.setProgress(audioBean.getCurrentProgress());
            holder.mItemBinding.lvitemAudioPlayIv.setImageResource(R.mipmap.red_pause);
        } else {
            holder.mItemBinding.lvitemAudioControllerLl.setVisibility(View.GONE);
            holder.mItemBinding.lvitemAudioPlayIv.setImageResource(R.mipmap.red_play);
        }

        return view;
    }

    class ViewHolder {
        LvitemAudioBinding mItemBinding;

        public ViewHolder(View view) {
            mItemBinding = LvitemAudioBinding.bind(view);
        }
    }
}
