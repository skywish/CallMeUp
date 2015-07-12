package com.example.skywish.imtest001.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

/**
 * Created by skywish on 2015/7/8.
 */
public class AccountActivity extends BaseActivity implements View.OnClickListener{

    Button btn_verified_phone,btn_get_email;
    LinearLayout email,phone, rl_phone,rl_email;
    Boolean is_email = false;
    Boolean is_phone = false;
    TextView text_email,text_phone,tv_get_code;
    EditText edit_phone,edit_code,edit_email;
    final User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);
        init();
    }

    public void init(){
        initToolbarWithBack("账号绑定");
        btn_verified_phone = (Button)findViewById(R.id.bt_verifier_phone);
        btn_verified_phone.setOnClickListener(this);
        btn_get_email = (Button)findViewById(R.id.btn_get_email);
        btn_get_email.setOnClickListener(this);

        email = (LinearLayout)findViewById(R.id.acount_email);
        email.setOnClickListener(this);
        phone = (LinearLayout)findViewById(R.id.acount_phone);
        phone.setOnClickListener(this);

        rl_phone = (LinearLayout)findViewById(R.id.rl_phone);
        rl_phone.setVisibility(View.GONE);
        rl_email = (LinearLayout)findViewById(R.id.rl_email);
        rl_email.setVisibility(View.GONE);

        text_email = (TextView)findViewById(R.id.textEmail);
        text_phone = (TextView)findViewById(R.id.textPhone);
        tv_get_code = (TextView)findViewById(R.id.tv_get_code);
        tv_get_code.setOnClickListener(this);

        edit_code = (EditText)findViewById(R.id.edit_code);
        edit_phone = (EditText)findViewById(R.id.edit_phone);
        edit_email = (EditText)findViewById(R.id.edit_email);

        if(userManager.getCurrentUser(User.class).getEmailVerified() != null && userManager.getCurrentUser(User.class).getEmailVerified()){
            text_email.setText("已绑定");
            text_email.setTextColor(getResources().getColor(R.color.bind_done));
            is_email = true;
        }
        if(userManager.getCurrentUser(User.class).getMobilePhoneNumberVerified() != null && userManager.getCurrentUser(User.class).getMobilePhoneNumberVerified()){
            text_phone.setText("已绑定");
            text_phone.setTextColor(getResources().getColor(R.color.bind_done));
            is_phone = true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.acount_email:
                if(!is_email){
                    rl_phone.setVisibility(View.GONE);
                    rl_email.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.acount_phone:
                if(!is_phone){
                    rl_email.setVisibility(View.GONE);
                    rl_phone.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_get_code:
                if(edit_phone.getText().length() != 11){
                    showToast("手机号码有错");
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
                    showToast("手机号码有错");
                    return;
                }
                if(edit_code.getText() == null){
                    showToast("请输入验证码");
                    return;
                }
                User u = userManager.getCurrentUser(User.class);
                user.setObjectId(u.getObjectId());
                user.setMobilePhoneNumber(edit_phone.getText().toString());
                BmobSMS.verifySmsCode(AccountActivity.this, edit_phone.getText().toString(),
                        edit_code.getText().toString(), new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {//短信验证码已验证成功
                            user.setMobilePhoneNumberVerified(true);
                            user.update(AccountActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    showToast("手机绑定成功");
                                    text_phone.setText("已绑定");
                                    text_phone.setTextColor(Color.GREEN);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    showToast("手机绑定失败： " + s);
                                }
                            });
                        } else{
                            showToast("验证失败：code ="+e.getErrorCode()+",msg = "+ e.getLocalizedMessage());
                        }
                    }
                });
                break;
            case R.id.btn_get_email:
                if(edit_email.getText() == null){
                    showToast("请输入邮箱");
                    return;
                }
                User u1 = userManager.getCurrentUser(User.class);
                user.setObjectId(u1.getObjectId());
                user.setEmail(edit_email.getText().toString());
                user.update(AccountActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        user.requestEmailVerify(AccountActivity.this, edit_email.getText().toString(),
                                new EmailVerifyListener() {
                            @Override
                            public void onSuccess() {
                                showToast("邮件发送成功，请查收验证");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showToast("邮箱验证失败： " + s);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("邮箱验证失败： " + s);
                    }
                });
                break;
        }
    }
}
