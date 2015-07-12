package com.example.skywish.imtest001.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.FriendAdapter;
import com.example.skywish.imtest001.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by skywish on 2015/7/1.
 */
public class SearchResultActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView list_friends;
    private List<BmobChatUser> chatUserList = new ArrayList<>();
    private TextView tv_no;
    FriendAdapter adapter;
    ProgressDialog dialog;
    String searchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcchresult);

        Intent intent = getIntent();
        searchName = intent.getStringExtra("searchName");

        initView();
        beginSearch();
    }

    private void initView() {
        initToolbarWithBack("搜索结果");
        tv_no = (TextView) findViewById(R.id.tv_no_find);
        list_friends = (ListView) findViewById(R.id.list_friends);
        adapter = new FriendAdapter(SearchResultActivity.this, R.layout.item_searchfriend, chatUserList);
        list_friends.setOnItemClickListener(this);
    }

    private void beginSearch() {
        dialog = new ProgressDialog(SearchResultActivity.this);
        dialog.setMessage("正在搜索...");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        userManager.queryUserByName(searchName, new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                chatUserList.clear();
                if (CollectionUtils.isNotNull(list)) {
                    dialog.dismiss();
                    adapter.addAll(list);
                    list_friends.setAdapter(adapter);
                } else {
                    dialog.dismiss();
                    tv_no.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("查询错误，请重试！");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BmobChatUser user = chatUserList.get(i);
        Intent intent = new Intent(this, UserInfoActivity.class);
        intent.putExtra("from", "add");
        intent.putExtra("username", user.getUsername());
        Log.i("TEST", "SearchResult--onItemClick ： " + user.getUsername());
        startActivity(intent);
        finish();
    }

}
