package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.base.BaseListAdapter;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.example.skywish.imtest001.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skywish on 2015/7/4.
 */
public class FriendShowAdapter extends BaseListAdapter<BmobChatUser> {

    public FriendShowAdapter(Context context, List<BmobChatUser> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friend_show2, null);
        }
        final BmobChatUser user = getList().get(position);
        // 初始化界面
        TextView username = ViewHolder.get(convertView, R.id.tv_friends_username);
        CircleImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        TextView words = ViewHolder.get(convertView, R.id.tv_words);
        /**
         * 设置头像
         */
        String avatarUri = user.getAvatar();
        if (avatarUri != null && avatarUri.isEmpty() == false) {
            ImageLoader.getInstance().displayImage(avatarUri, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.ic_default_profile_head);
        }

        /**
         * 设置个性签名和用户名
         */
        username.setText(user.getUsername());
//        words.setText(user.);
        return convertView;
    }

}
