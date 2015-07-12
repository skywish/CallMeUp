package com.example.skywish.imtest001.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.FriendAdapter;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by skywish on 2015/6/29.
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener{

    private ImageButton ib_search;
    private EditText et_search;
    private String searchName;
    private RelativeLayout layout_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);

        initView();
    }

    private void initView() {
        initToolbarWithBack("添加好友");

        et_search = (EditText) findViewById(R.id.et_search);
        layout_search = (RelativeLayout) findViewById(R.id.layout_search);
        layout_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_search:
                searchName = et_search.getText().toString();
                if (TextUtils.isEmpty(searchName)) {
                    showToast("请输入用户名");
                    return;
                }
                if (searchName.equals(userManager.getCurrentUser().getUsername())) {
                    showToast("不能添加自己为好友");
                    return;
                }
                Intent intent = new Intent(AddFriendActivity.this, SearchResultActivity.class);
                intent.putExtra("from", "add");
                intent.putExtra("searchName", searchName);
                startActivity(intent);
                break;
        }
    }
}
