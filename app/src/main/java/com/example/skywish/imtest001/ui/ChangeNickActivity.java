package com.example.skywish.imtest001.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.ui.BaseActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/7/6.
 */
public class ChangeNickActivity extends BaseActivity {

    private EditText et_nick;
    private Button btn_save;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick);

        currentUser = userManager.getCurrentUser(User.class);
        final User updateUser = new User();
        updateUser.setObjectId(currentUser.getObjectId());

        initToolbarWithBack("修改昵称");

        et_nick = (EditText) findViewById(R.id.et_nickname);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nick = et_nick.getText().toString();
                if (nick.isEmpty()) {
                    showToast("请输入昵称");
                    return;
                }
                updateUser.setNick(nick);
                updateUser.update(ChangeNickActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("修改成功");
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("修改失败");
                    }
                });
            }
        });
    }
}
