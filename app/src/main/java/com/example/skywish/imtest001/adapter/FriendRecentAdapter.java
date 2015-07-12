package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.base.BaseListAdapter;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.example.skywish.imtest001.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skywish on 2015/7/5.
 */
public class FriendRecentAdapter extends BaseListAdapter<BmobRecent> {

    public FriendRecentAdapter(Context context, List<BmobRecent> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friend_recent, null);
        }
        final BmobRecent item = getList().get(position);

        // 初始化界面
        TextView username = ViewHolder.get(convertView, R.id.tv_recent_username);
        TextView message = ViewHolder.get(convertView, R.id.tv_recent_message);
        TextView time = ViewHolder.get(convertView, R.id.tv_recent_time);
        TextView unread = ViewHolder.get(convertView, R.id.tv_recent_unread);
        CircleImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);

        /**
         * 显示头像
         */
        String avatarUri = item.getAvatar();
        if (avatarUri != null && avatarUri.isEmpty() == false) {
            ImageLoader.getInstance().displayImage(avatarUri, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setBackgroundResource(R.drawable.ic_default_profile_head);
        }

        /**
         * 设置用户名
         */
        username.setText(item.getUserName());

        time.setText(TimeUtil.getChatTime(item.getTime()));
        //显示subtitle
        switch (item.getType()) {
            case BmobConfig.TYPE_TEXT:
                message.setText(item.getMessage());
                break;
            case BmobConfig.TYPE_IMAGE:
                message.setText("[图片]");
                break;
            case BmobConfig.TYPE_VOICE:
                message.setText("[声音]");
                break;
            default:
                break;
        }

        int unReadNum = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
        if (unReadNum > 0) {
            unread.setVisibility(View.VISIBLE);
            unread.setText(unReadNum + "");
        } else {
            unread.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
