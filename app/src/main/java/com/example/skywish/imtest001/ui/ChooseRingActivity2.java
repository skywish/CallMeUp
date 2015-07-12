package com.example.skywish.imtest001.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.Music;
import com.example.skywish.imtest001.util.MusicAction;

import java.util.ArrayList;

/**
 * Created by skywish on 2015/7/8.
 */
public class ChooseRingActivity2 extends BaseActivity {
    Button musicBtn;
    private ListView musicListView;
    private ListAdapter listAdapter;
    private ArrayList<Music> musicList = new ArrayList<>();
    //    private MediaPlayer mediaPlayer;
    private int[] clickCount;
    final CharSequence[] items = {"设为铃声", "取消"};
    static Boolean isPlay = false;
    static int playPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ring);
//        musicBtn = (Button) findViewById(R.id.musicBtn);
//        musicBtn.setOnClickListener(new ButtonListener());

        initToolbarWithBack("本地音乐");
        queryMusics();
        musicListView = (ListView) findViewById(R.id.list);
        listAdapter = new ListAdapter(this);
        musicListView.setAdapter(listAdapter);
        musicListView.setOnItemClickListener(new ItemClickListener());
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ChooseRingActivity2.this);
            builder.setTitle(musicList.get(position).getTITLE());
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            dialog.dismiss();
                            Music music = musicList.get(position);
                            Intent intent = getIntent();
                            intent.putExtra("musicPath", music.getDATA());
                            intent.putExtra("musicName", music.getTITLE());
                            setResult(1, intent);
                            Toast.makeText(getApplicationContext(), "选择了" + music.getTITLE(), Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case 1:
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listAdapter.musicAction.stop();
    }

//    class ButtonListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.musicBtn:
//                    showMusicList();
//                    break;
//            }
//        }
//    }

    private void queryMusics() {
        ContentResolver contentResolver = this.getContentResolver();
        Cursor musics = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
        }, MediaStore.Audio.Media.IS_MUSIC + " = 1 AND " + MediaStore.Audio.Media.DURATION
                + " > 60000", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        musics.moveToFirst();
        while (!musics.isAfterLast()) {
            Music temp = new Music();
            temp.setTITLE(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            temp.setDURATION(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            temp.setDATA(musics.getString(musics.getColumnIndex(MediaStore.Audio.Media.DATA)));
            temp.setExist(true);
            musicList.add(temp);
            musics.moveToNext();
        }
        musics.close();
        clickCount = new int[musicList.size()];
    }

    private void showMusicList() {
        listAdapter = new ListAdapter(this);
        musicListView.setAdapter(listAdapter);
    }

    private class ListAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<ImageButton> buttonList;
        public MusicAction musicAction;

        public ListAdapter(Context context) {
            this.context = context;
            this.layoutInflater = layoutInflater.from(context);
            musicAction = new MusicAction();
            buttonList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Music getItem(int position) {
            return musicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.music_list_item, null);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.musicName = (TextView) convertView.findViewById(R.id.musicNameView);
            holder.musicTime = (TextView) convertView.findViewById(R.id.musicTime);
            holder.musicPlay = (ImageButton) convertView.findViewById(R.id.playMusicBtn);
            final Music music = getItem(position);
//            Toast.makeText(context, music.getTITLE(), Toast.LENGTH_SHORT).show();
            holder.musicName.setText(music.getTITLE());
            if (Integer.parseInt(music.getDURATION()) / 60000 > 0) {
                holder.musicTime.setText(String.valueOf(Integer.parseInt(music.getDURATION()) / 60000) + "分" + String.valueOf((Integer.parseInt(music.getDURATION()) % 60000) / 1000) + "秒");
            } else {
                holder.musicTime.setText(String.valueOf((Integer.parseInt(music.getDURATION()) % 60000) / 1000) + "秒");
            }

            holder.musicPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isPlay) {
                        //开始播放
                        playPosition = position;
//                        holder.musicPlay.setImageResource(R.drawable.ic_music_start);
                    }
                }
            });

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
                    musicAction.play(getItem(position).getDATA(), false);
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
            TextView musicName;
            TextView musicTime;
            ImageButton musicPlay;
        }
    }
}
