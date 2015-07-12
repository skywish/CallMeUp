package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.DBCallUp;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.util.CollectionUtils;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.example.skywish.imtest001.util.MusicAction;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by DYY-PC on 2015/7/5.
 */
public class CallUpListAdapter extends BaseAdapter {
    Context context;
    ArrayList<DBCallUp> callUpList;
    public MusicAction musicAction;
    private int[] clickCount;
    private ArrayList<ImageButton> buttonList;
    public CallUpListAdapter(Context context, ArrayList<DBCallUp> callUpList){
        this.callUpList = callUpList;
        this.context = context;
        clickCount = new int[callUpList.size()];
        musicAction = new MusicAction();
        buttonList = new ArrayList<>();
    }
    BmobUserManager userManager = BmobUserManager.getInstance(context);
    String avatarUrl;

    @Override
    public int getCount() {
        return callUpList.size();
    }

    @Override
    public DBCallUp getItem(int position) {
        return callUpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.call_up_list_item, null);
            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.callUpName = (TextView) convertView.findViewById(R.id.callUpName);
            holder.callUpSex = (TextView) convertView.findViewById(R.id.callUpSex);
            holder.callUpRecordPlay = (ImageButton) convertView.findViewById(R.id.callUpRecordPlayBtn);
            holder.callUpRecordPlay.setId(position);
            buttonList.add(holder.callUpRecordPlay);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final DBCallUp callUp = getItem(position);
//        holder.avatar.setBackgroundResource(callUp.get);

        userManager.queryUserByName(callUp.getCallUpName(), new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                if (CollectionUtils.isNotNull(list))
                    avatarUrl = list.get(0).getAvatar();
            }

            @Override
            public void onError(int i, String s) {
                Log.i("TEST", "CallUpAdapter:查询错误，请重试！");
            }
        });

        if (avatarUrl != null && !avatarUrl.equals("")) {
            ImageLoader.getInstance().displayImage(avatarUrl, holder.avatar,
                    ImageLoadOptions.getOptions());
        } else {
            holder.avatar.setBackgroundResource(R.drawable.ic_default_profile_head);
        }
        holder.callUpName.setText(callUp.getCallUpName());
        holder.callUpSex.setText(callUp.getCallUpSex());
        holder.callUpRecordPlay.setOnClickListener(new ButtonListener());
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
                musicAction.play(getItem(position).getCallUpRecordPath(), false);
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
    class ViewHolder{
        ImageView avatar;
        TextView callUpName;
        TextView callUpSex;
        ImageButton callUpRecordPlay;
    }
}
