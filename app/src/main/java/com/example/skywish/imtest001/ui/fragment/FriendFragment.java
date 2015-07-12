package com.example.skywish.imtest001.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.FriendShowAdapter;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.ui.UserInfoActivity;
import com.example.skywish.imtest001.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/6/30.
 */
public class FriendFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    ListView list_friend;
    //是否有朋友
    TextView tv_tip;
    private List<BmobChatUser> friends = new ArrayList<>();
    private FriendShowAdapter adapter;
    User user;
    String username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initListView();
    }

    private void initListView() {
        user = userManager.getCurrentUser(User.class);
        username = user.getUsername();

        tv_tip = (TextView) findViewById(R.id.tv_no_friend);
        list_friend = (ListView) findViewById(R.id.list_friends);

        friends = BmobDB.create(getActivity()).getContactList();
        Log.i("Fragment", "username:" + username);
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getUsername().equals(username)) {
                friends.remove(i);
                Log.i("Fragment", "friends.remove1");
            }
        }
//        showToast(friends.size() + "");
        Log.i("TEST", "FriendFragemtn:" + "friend size is" + friends.size());

        tv_tip.setVisibility(View.GONE);
        adapter = new FriendShowAdapter(getActivity(), friends);
        list_friend.setAdapter(adapter);
        list_friend.setOnItemClickListener(this);
        list_friend.setOnItemLongClickListener(this);
    }

    /**
     * 点击进入好友详情
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BmobChatUser user = friends.get(i);
        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
        intent.putExtra("username", user.getUsername());
        startActivity(intent);
    }

    /**
     * 长按删除好友
     * @param adapterView
     * @param view
     * @param position
     * @param l
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        final BmobChatUser user = friends.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确认删除好友？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFriend(user, position);
                    }
                })
                .setNegativeButton("否", null)
                .show();
        return true;
    }

    private void deleteFriend(final BmobChatUser user, final int position) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在删除...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        userManager.deleteContact(user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast("删除成功");
                CustomApplication.getInstance().getContactList().remove(user.getUsername());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        friends.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("删除失败，请重试！");
                dialog.dismiss();
            }
        });
    }

    public void queryFriend() {
        Log.i("Fragment", "queryFriend-begin");
        friends = BmobDB.create(getActivity()).getContactList();
        for (int i = 0; i < friends.size(); i++) {
            Log.i("Fragment", i + " : " + friends.get(i).getUsername());
            if (friends.get(i).getUsername().equals(username)) {
                friends.remove(i);
                Log.i("Fragment", "friends.remove2");
            }
        }

        //设置到内存
        CustomApplication.getInstance().setContactList(CollectionUtils.list2map(friends));
        adapter = new FriendShowAdapter(getActivity(), friends);
        list_friend.setAdapter(adapter);
//        if (adapter == null) {
//            adapter = new FriendShowAdapter(getActivity(), friends);
//            list_friend.setAdapter(adapter);
//        } else {
////            adapter = new FriendShowAdapter(getActivity(), friends);
//            Log.i("Fragment", "queryFriend-begin");
//            adapter.notifyDataSetChanged();
//        }
    }

    public void refreshOnline() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在删除...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void refresh(){
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    queryFriend();
                    Log.i("Fragment", "queryFriend-End");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment", "OnResume");
        refresh();
        Log.i("Fragment", "refresh");
    }

}
