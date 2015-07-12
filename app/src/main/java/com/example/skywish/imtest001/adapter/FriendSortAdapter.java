package com.example.skywish.imtest001.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skywish on 2015/7/4.
 */
public class FriendSortAdapter extends BaseAdapter implements SectionIndexer{

    private List<User> list = null;
    private Context mContext;

    public FriendSortAdapter(Context mContext, List<User> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<User> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_show, null);
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
            viewHolder.username = (TextView) view.findViewById(R.id.tv_friends_username);
            viewHolder.letter = (TextView) view.findViewById(R.id.tv_letter);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        /**
         * 获取头像和用户名
         */
        final User user = list.get(position);
        String name = user.getUsername();
        String avatarUri = user.getAvatar();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            ImageLoader.getInstance().displayImage(avatarUri, viewHolder.avatar,
                    ImageLoadOptions.getOptions());
        } else {
            viewHolder.avatar.setImageResource(R.drawable.ic_default_profile_head);
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.letter.setVisibility(View.VISIBLE);
            viewHolder.letter.setText(user.getSortLetters());
        } else {
            viewHolder.letter.setVisibility(View.GONE);
        }

        return view;
    }



    final static class ViewHolder {
        TextView letter;
        CircleImageView avatar;
        TextView username;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    @Override
    public Object[] getSections() {
        return null;
    }
}
