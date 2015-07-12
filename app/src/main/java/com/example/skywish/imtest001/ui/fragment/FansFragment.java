package com.example.skywish.imtest001.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.NewFriendAdapter;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

/**
 * Created by skywish on 2015/6/30.
 */
public class FansFragment extends BaseFragment implements AdapterView.OnItemLongClickListener{

    ListView listview;
    NewFriendAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fans, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listview = (ListView) findViewById(R.id.list_new_friends);
        listview.setOnItemLongClickListener(this);
        // 获取DB中的列表
        adapter = new NewFriendAdapter(getActivity(), BmobDB.create(getActivity()).queryBmobInviteList());
        Log.i("Bmob", "FansFragment-onActivityCreated-获取本地邀请列表大小：" + BmobDB.create(getActivity()).queryBmobInviteList().size());
        Log.i("Bmob", "2.CustomApplication.getInstance()" + CustomApplication.getInstance());
        listview.setAdapter(adapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final BmobInvitation invitation = (BmobInvitation) adapter.getItem(position);
        final int myposition = position;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("确认删除吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("Bmob", "FansFragment-onItemLongClick-删除事件1=============");
                        adapter.remove(myposition);
                        Log.i("Bmob", "FansFragment-onItemLongClick-删除事件2=============");
                        BmobDB.create(getActivity()).deleteInviteMsg(invitation.getFromid(),
                                Long.toString(invitation.getTime()));
                        Log.i("Bmob", "FansFragment-onItemLongClick-删除事件2=============");
                    }
                })
                .setNegativeButton("否", null)
                .show();
        return true;
    }

    public void refresh() {
        adapter = new NewFriendAdapter(getActivity(), BmobDB.create(getActivity()).queryBmobInviteList());
        listview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
