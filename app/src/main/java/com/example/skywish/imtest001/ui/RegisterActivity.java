package com.example.skywish.imtest001.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.config.BombConstants;
import com.example.skywish.imtest001.util.CommonUtils;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by skywish on 2015/6/27.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private Button register;
    private ToggleButton showMore;
    private EditText editAccount, editPassword, editPhone, editQQ, editEmail, editPsdAgain;
    private LinearLayout showMoreLayout;

    String username, password, encodedPass, phone, qq, email, psdAgain;

    BmobChatUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        editAccount = (EditText) findViewById(R.id.register_edit_account);
        editPassword = (EditText) findViewById(R.id.register_edit_password);
        editPsdAgain = (EditText) findViewById(R.id.register_edit_passwordAgain);
//        editPhone = (EditText) findViewById(R.id.register_edit_phone);
//        editQQ = (EditText) findViewById(R.id.register_edit_qq);
//        editEmail = (EditText) findViewById(R.id.register_edit_email);
//        showMoreLayout = (LinearLayout) findViewById(R.id.register_layout_more);
//
//        showMore = (ToggleButton) findViewById(R.id.register_button_show);
//        showMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    showMoreLayout.setVisibility(View.VISIBLE);
//                } else {
//                    showMoreLayout.setVisibility(View.GONE);
//                }
//            }
//        });

        register = (Button) findViewById(R.id.register_button_register);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button_register:
                register();
                break;
            default:
                break;
        }
    }

    public void register() {
        username = editAccount.getText().toString();
        password = editPassword.getText().toString();
        psdAgain = editPsdAgain.getText().toString();
//        email = editEmail.getText().toString();
//        phone = editPhone.getText().toString();
//        qq = editQQ.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(RegisterActivity.this, "请输入账号",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "请输入密码",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!psdAgain.equals(password)) {
            Toast.makeText(RegisterActivity.this, "两次密码不匹配，请重新输入密码",
                    Toast.LENGTH_SHORT).show();
            editPsdAgain.setText("");
            editPassword.setText("");
            return;
        }
        encodedPass = CommonUtils.encodePsd(password);

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            Toast.makeText(RegisterActivity.this, "请联网注册！",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        // 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPass);
        user.setDeviceType("android");
        user.setInstallId(BmobInstallation.getInstallationId(this));

        user.setNick(username);
        user.setWords("");
        user.setSex(true);
        user.setBirthday("");
        user.setCity("");

        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(user.getUsername());
                // 发广播通知登陆页面退出
                sendBroadcast(new Intent(BombConstants.ACTION_REGISTER_SUCCESS_FINISH));
                // 启动主页
                Intent intent = new Intent(RegisterActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "注册失败" + s, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
}
