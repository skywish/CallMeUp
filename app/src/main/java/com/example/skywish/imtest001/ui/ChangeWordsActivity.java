package com.example.skywish.imtest001.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/7/6.
 */
public class ChangeWordsActivity extends BaseActivity {

    private EditText et_words;
    private Button btn_save;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_words);

        currentUser = userManager.getCurrentUser(User.class);
        final User updateUser = new User();
        updateUser.setObjectId(currentUser.getObjectId());

        initToolbarWithBack("个性签名");

        et_words = (EditText) findViewById(R.id.et_words);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String words = et_words.getText().toString();
                updateUser.setWords(words);
                updateUser.update(ChangeWordsActivity.this, new UpdateListener() {
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
