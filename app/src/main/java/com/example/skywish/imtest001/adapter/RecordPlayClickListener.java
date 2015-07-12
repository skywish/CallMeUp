package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;

/**
 * Created by skywish on 2015/7/5.
 */
public class RecordPlayClickListener implements View.OnClickListener{

    BmobMsg bmobMsg;
    ImageView iv_voice;
    // 逐帧动画
    private AnimationDrawable anim = null;
    Context context;
    String currentId;
    MediaPlayer mediaPlayer;
    public static Boolean isplaying = false;
    static BmobMsg currentMsg = null;
    BmobUserManager userManager;



    @Override
    public void onClick(View view) {

    }

    public void startPlay() {
    }
}
