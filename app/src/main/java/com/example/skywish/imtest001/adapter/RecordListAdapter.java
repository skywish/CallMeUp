package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.skywish.imtest001.bean.DBRecord;
import com.example.skywish.imtest001.util.MusicAction;
import com.example.skywish.imtest001.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DYY-PC on 2015/7/5.
 */
public class RecordListAdapter extends BaseAdapter{
    Context context;
    ArrayList<DBRecord> records;
    private int[] clickCount;
    public MusicAction musicAction;
    private ArrayList<ImageButton> buttonList;
    public RecordListAdapter(Context context, ArrayList<DBRecord> records){
        this.records = records;
        this.context = context;
        clickCount = new int[records.size()];
        musicAction = new MusicAction();
        buttonList = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public DBRecord getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.record_list_item, null);
            holder = new ViewHolder();
            holder.tapeDate = (TextView) convertView.findViewById(R.id.dateView);
            holder.tapeTimes = (TextView) convertView.findViewById(R.id.timesView);
            holder.playBtn = (ImageButton) convertView.findViewById(R.id.playBtn);
            holder.playBtn.setId(position);
            buttonList.add(holder.playBtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DBRecord record = getItem(position);
        holder.tapeDate.setText(record.getDate());
        holder.tapeTimes.setText(String.valueOf(record.getTimes()) + "\''");
        holder.playBtn.setOnClickListener(new ButtonListener());
        return convertView;
    }
    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final int position = v.getId();

            for (int i = 0; i < buttonList.size(); i++) {
                if (i != position) {
                    buttonList.get(i).setBackgroundResource(R.drawable.ic_music_start);
                }
            }
            if (clickCount[position] == 0) {
                buttonList.get(position).setBackgroundResource(R.drawable.ic_music_stop);
                musicAction.play(getItem(position).getFileUrl(), false);
                clickCount[position]=1;
            } else {
                buttonList.get(position).setBackgroundResource(R.drawable.ic_music_start);
                musicAction.stop();
                clickCount[position] = 0;
            }
            if (musicAction.mediaPlayer != null) {
                musicAction.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        clickCount[position] = 0;
                        musicAction.mediaPlayer.release();
                        musicAction.mediaPlayer =null;
                        buttonList.get(position).setBackgroundResource(R.drawable.ic_music_start);
                    }
                });
            }
        }
    }
    class ViewHolder {
        TextView tapeTimes;
        TextView tapeDate;
        ImageButton playBtn;
    }
}
