package com.example.skywish.imtest001.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.FriendRecentAdapter;
import com.example.skywish.imtest001.adapter.FriendShowAdapter;
import com.example.skywish.imtest001.config.BombConstants;
import com.example.skywish.imtest001.ui.ChatActivity;
import com.example.skywish.imtest001.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by skywish on 2015/6/29.
 */
public class RecentContactFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    private ListView list_recent_friend;
    private PtrClassicFrameLayout pullToRefreshLayout;
    private FriendRecentAdapter adapter = null;
    private List<BmobRecent> bmobRecentList = new ArrayList<>();
    private ChangeRecentReceiver receiver = new ChangeRecentReceiver();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recentcontact, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {

        list_recent_friend = (ListView) getView().findViewById(R.id.list_recent_friend);
        list_recent_friend.setOnItemLongClickListener(this);
        list_recent_friend.setOnItemClickListener(this);


        bmobRecentList = BmobDB.create(getActivity()).queryRecents();
        Log.i("RECENT", "bmobRecentList:" + BmobDB.create(getActivity()).queryRecents());
        adapter = new FriendRecentAdapter(getActivity(), bmobRecentList);
        list_recent_friend.setAdapter(adapter);

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BombConstants.ACTION_CHAT_FINISH);
        getActivity().registerReceiver(receiver, filter);
        Log.i("RECENT", "registerReceiver");
    }

    private void queryRecent() {
        bmobRecentList = BmobDB.create(getActivity()).queryRecents();
        Log.i("RECENT", "bmobRecentList.size():" + bmobRecentList.size() + "");
        if (adapter == null) {
            adapter = new FriendRecentAdapter(getActivity(), bmobRecentList);
            list_recent_friend.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void pullToRefreshRecent() {
//        refresh();
    }

    public void refresh(){
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    bmobRecentList = BmobDB.create(getActivity()).queryRecents();
                    adapter = new FriendRecentAdapter(getActivity(), bmobRecentList);
                    list_recent_friend.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BmobRecent item = bmobRecentList.get(position);
        BmobDB.create(getActivity()).resetUnread(item.getTargetid());
        //点击进入对话
        BmobChatUser user = new BmobChatUser();
        user.setAvatar(item.getAvatar());
        user.setNick(item.getNick());
        user.setUsername(item.getUserName());
        user.setObjectId(item.getTargetid());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        final BmobRecent item = bmobRecentList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("确认删除？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.remove(position);
                        BmobDB.create(getActivity()).deleteRecent(item.getTargetid());
                        BmobDB.create(getActivity()).deleteMessages(item.getTargetid());
                    }
                })
                .setNegativeButton("否", null)
                .show();
        return true;
    }

    public class ChangeRecentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("RECENT", "RECEIVED BROADCAST1");
            if (intent != null &&
                    BombConstants.ACTION_CHAT_FINISH.equals(intent.getAction())) {
                Log.i("RECENT", "RECEIVED BROADCAST2");
                refresh();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}
