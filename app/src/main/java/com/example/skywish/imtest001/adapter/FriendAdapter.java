package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.skywish.imtest001.R;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skywish on 2015/6/27.
 */
public class FriendAdapter extends ArrayAdapter<BmobChatUser> {

    private int resourceId;

    /**
     * 构造器
     * @param context 在哪个activity
     * @param resource item_searchfriend layout
     * @param objects
     */
    public FriendAdapter(Context context, int resource, List<BmobChatUser> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobChatUser chatUser = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.head = (CircleImageView) view.findViewById
                    (R.id.iv_profile_image);
            viewHolder.username = (TextView) view.findViewById
                    (R.id.tv_friend_username);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
//        viewHolder.head.setImageBitmap(chatUser.getUsername());
        viewHolder.username.setText(chatUser.getUsername());
        return view;
    }

    class ViewHolder {
        CircleImageView head;
        TextView username;
    }
}
