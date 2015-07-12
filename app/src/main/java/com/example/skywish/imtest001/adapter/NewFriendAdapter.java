package com.example.skywish.imtest001.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.base.BaseListAdapter;
import com.example.skywish.imtest001.util.CollectionUtils;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.example.skywish.imtest001.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skywish on 2015/7/3.
 */
public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {

    public NewFriendAdapter(Context context, List<BmobInvitation> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friend_add, null);
        }
        final BmobInvitation bmobInvitation = getList().get(position);
        // 初始化界面
        TextView username = ViewHolder.get(convertView, R.id.tv_friends_username);
        CircleImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        TextView time = ViewHolder.get(convertView, R.id.tv_friends_time);
        final Button btn_accept = ViewHolder.get(convertView, R.id.btn_accept);
        final TextView tv_accept = ViewHolder.get(convertView, R.id.tv_accept_ready);

        /**
         * 设置头像
         */
        String avatarUri = bmobInvitation.getAvatar();
        if (avatarUri != null && avatarUri.isEmpty() == false) {
            ImageLoader.getInstance().displayImage(avatarUri, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.ic_default_profile_head);
        }

        /**
         * 设置时间和用户名
         */
        String inviteTime = TimeUtil.getChatTime(bmobInvitation.getTime());
        username.setText(bmobInvitation.getFromname());
        time.setText(inviteTime);

        Log.i("Bmob", " bmobInvitation.getStatus();" + bmobInvitation.getStatus());

        /**
         * 根据状态设置按钮显示
         */
        int status = bmobInvitation.getStatus();
        // 好友请求没被验证或没收到过
        if (status == BmobConfig.INVITE_ADD_NO_VALIDATION || status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED) {
            btn_accept.setVisibility(View.VISIBLE);
            tv_accept.setVisibility(View.GONE);
            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agreeAdd(btn_accept, tv_accept, bmobInvitation);
                }
            });
        } else if (status == BmobConfig.INVITE_ADD_AGREE) {
            btn_accept.setVisibility(View.GONE);
            tv_accept.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void agreeAdd(final Button button, final TextView textView, final BmobInvitation invitation) {
        final ProgressDialog progress = new ProgressDialog(mContext);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        try {
            // 接受好友请求消息
            BmobUserManager.getInstance(mContext).agreeAddContact(invitation, new UpdateListener() {
                @Override
                public void onSuccess() {
                    progress.dismiss();
                    button.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    //因为在MyMessageReceiver中收到消息时先保存在本地DB种，后可直接读取DB中好友请求，放在联系人中
                    CustomApplication.getInstance().setContactList(CollectionUtils.list2map(
                            BmobDB.create(mContext).getContactList()));
                }

                @Override
                public void onFailure(int i, String s) {
                    progress.dismiss();
                    Toast.makeText(mContext, "添加失败" + s, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progress.dismiss();
        }
    }
}
