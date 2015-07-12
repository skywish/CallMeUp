package com.example.skywish.imtest001.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
/**
 * Created by skywish on 2015/7/9.
 */
public class ForgetPsdActivity extends BaseActivity implements View.OnClickListener {


    Button btn_verified_phone,btn_get_email,btn_get_code;
    LinearLayout email,phone, rl_phone,rl_email;
    EditText edit_phone,edit_code,edit_email,edit_password;
    final User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        init();
    }

    public void init(){
        initToolbarWithBack("密码找回");
        btn_verified_phone = (Button)findViewById(R.id.btn_verifier_phone);
        btn_verified_phone.setOnClickListener(this);
        btn_get_email = (Button)findViewById(R.id.btn_get_email);
        btn_get_email.setOnClickListener(this);
        btn_get_code = (Button)findViewById(R.id.btn_get_code);
        btn_get_code.setOnClickListener(this);

        email = (LinearLayout)findViewById(R.id.change_from_email);
        email.setOnClickListener(this);
        phone = (LinearLayout)findViewById(R.id.change_from_phone);
        phone.setOnClickListener(this);

        rl_phone = (LinearLayout)findViewById(R.id.rl_phone);
        rl_phone.setVisibility(View.GONE);
        rl_email = (LinearLayout)findViewById(R.id.rl_email);
        rl_email.setVisibility(View.GONE);


        edit_code = (EditText)findViewById(R.id.edit_code);
        edit_phone = (EditText)findViewById(R.id.edit_phone);
        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_password = (EditText)findViewById(R.id.edit_password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_from_email:
                rl_phone.setVisibility(View.GONE);
                rl_email.setVisibility(View.VISIBLE);
                break;
            case R.id.change_from_phone:
                rl_email.setVisibility(View.GONE);
                rl_phone.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_get_code:
                if(edit_phone.getText().length() != 11){
                    showToast("请输入11位手机号");
                    return;
                }
                BmobSMS.requestSMSCode(getApplication(), edit_phone.getText().toString(),
                        "verifySms", new RequestSMSCodeListener() {
                            @Override
                            public void done(Integer integer, BmobException e) {
                                if (e == null) {
                                    showToast("验证码发送成功");
                                }
                            }
                        });
                break;
            case R.id.bt_verifier_phone:
                if(edit_phone.getText().length() != 11){
                    showToast("请输入11位手机号");
                    return;
                }
                if(edit_code.getText() == null){
                    showToast("请输入验证码");
                    return;
                }
                if(edit_password.getText() == null){
                    showToast("请输入新密码");
                    return;
                }
                if(edit_password.getText().length() < 6 || edit_password.getText().length() > 16){
                    showToast("请输入正确长度密码（至少6位）");
                    return;
                }
                BmobUser.resetPasswordBySMSCode(ForgetPsdActivity.this, edit_code.getText().toString(),
                        edit_password.getText().toString(), new ResetPasswordByCodeListener() {
                            @Override
                            public void done(BmobException e) {
                                showToast("验证码发送成功");
                            }
                        });

                break;
            case R.id.btn_get_email:
                if(edit_email.getText() == null){
                    showToast("请输入邮箱");
                    return;
                }
                BmobUser.resetPasswordByEmail(ForgetPsdActivity.this, edit_email.getText().toString(), new ResetPasswordByEmailListener() {
                    @Override
                    public void onSuccess() {
                        showToast("邮件发送成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("邮件发送失败" + s);
                    }
                });
                break;
        }
    }
}
