package com.example.skywish.imtest001.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.config.BombConstants;
import com.example.skywish.imtest001.config.Configs;
import com.example.skywish.imtest001.util.CommonUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.OtherLoginListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/6/27.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_register, tv_forget, tv_phone_login;
    private Button btn_login, btn_qq, btn_weixin;
    private EditText et_account, et_password, et_phone, et_smscode;
    private Bitmap bitmap;
    private String username, password, encodedPass, nickName, gender, country, province, city, ret,
            nicknameString, avatarUrl;
    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();
    Boolean isPhone = false;
    Boolean isSend = false;
    //是否手机登陆
    String phone = "", SMSCode = "";
    private ProgressDialog progressDialog_qq;
    private Tencent mTencent;

    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        et_account = (EditText) findViewById(R.id.login_edit_account);
        et_password = (EditText) findViewById(R.id.login_edit_password);
        et_phone = (EditText) findViewById(R.id.login_edit_phone);
        et_smscode = (EditText) findViewById(R.id.login_edit_smscode);

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isSend = false;
                btn_login.setText("发送验证码");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_login = (Button) findViewById(R.id.login_button_login);
        btn_qq = (Button) findViewById(R.id.btn_qq);
        tv_register = (TextView) findViewById(R.id.login_text_register);
        tv_forget = (TextView) findViewById(R.id.login_text_forget);
        tv_phone_login = (TextView) findViewById(R.id.tv_phone_login);

        btn_login.setOnClickListener(this);
        btn_qq.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forget.setOnClickListener(this);
        tv_phone_login.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BombConstants.ACTION_REGISTER_SUCCESS_FINISH);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button_login:
                //如果不是手机登陆,正常登陆
                if (!isPhone) {
                    login();
                } else {
                    //是手机登录，手机号填写正确
//                    if (btn_login.getText().equals("验证登录") && et_phone.getText().length() == 11) {
//                        verifyCode();
//                    } else if (btn_login.getText().equals("验证登录") && et_phone.getText().length() < 11) {
//                        showToast("请输入");
//                    }
                    if(et_phone.getText().length() < 11){
                        showToast("请输入正确的手机号");
                        return;
                    }
                    if(!isSend){
                        sendMessage();
                        btn_login.setText("验证登录");
                    } else {
                        verifyCode();
                    }
                }
                break;
            case R.id.login_text_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_text_forget:
                Intent intent2 = new Intent(LoginActivity.this, ForgetPsdActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_phone_login:
                phoneLogin();
                break;
            case R.id.btn_qq:
                qqLogin();
                break;
            default:
                break;
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null &&
                    BombConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    }

    public void login() {
        username = et_account.getText().toString();
        password = et_password.getText().toString();
        encodedPass = CommonUtils.encodePsd(password);

        if (TextUtils.isEmpty(username)) {
            showToast("请输入用户名");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("请输入密码");
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            showToast("没有网络连接");
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请等待...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPass);
        userManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {
//                updateUserInfos();
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                showToast("登录失败");
            }
        });
    }

    public void qqLogin() {
        progressDialog_qq = new ProgressDialog(this);
        progressDialog_qq.setMessage("请等待...");
        progressDialog_qq.setCanceledOnTouchOutside(false);
        progressDialog_qq.show();
        //Toast.makeText(getApplicationContext(),"请等待...",Toast.LENGTH_LONG).show();
        //第一个参数就是上面所说的申请的APPID，第二个是全局的Context上下文，这句话实现了调用QQ登录
        mTencent = Tencent.createInstance(Configs.mAppid, getApplicationContext());
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */

        mTencent.login(this, "all", new BaseUiListener());

    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            progressDialog_qq.dismiss();
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            progressDialog_qq.dismiss();
            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.e(TAG, "-------------" + response.toString());
                String openidString = ((JSONObject) response).getString("openid");
//                openidTextView.setText(openidString);
                Log.e(TAG, "-------------"+openidString);
                String access_token= ((JSONObject) response).getString("access_token");
                String expires_in = ((JSONObject) response).getString("expires_in");
                BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(BmobUser.BmobThirdUserAuth.SNS_TYPE_QQ,access_token, expires_in,openidString);
                loginWithAuth(authInfo);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError arg0) {
            // TODO Auto-generated method stub
            progressDialog_qq.dismiss();
        }
    }

    private void phoneLogin() {
        //如果不是手机登录，转成手机登陆
        if (!isPhone) {
            et_account.setVisibility(View.GONE);
            et_password.setVisibility(View.GONE);
            et_phone.setVisibility(View.VISIBLE);
            et_smscode.setVisibility(View.VISIBLE);
            et_smscode.setText("");
            et_phone.setText("");
            btn_login.setText("发送验证码");
            isPhone = true;
        } else {
            et_account.setVisibility(View.VISIBLE);
            et_password.setVisibility(View.VISIBLE);
            et_smscode.setVisibility(View.GONE);
            et_phone.setVisibility(View.GONE);
            et_account.setText("");
            et_password.setText("");
            btn_login.setText("登录");
            isPhone = false;
        }
    }

    private void verifyCode() {
        SMSCode = et_smscode.getText().toString();
        if (SMSCode != null && !SMSCode.isEmpty()) {
            BmobUser.signOrLoginByMobilePhone(LoginActivity.this, phone, SMSCode, new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    // 启动主页
                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            showToast("请输入验证码！");
        }

    }

    private void sendMessage() {
        phone = et_phone.getText().toString();
        if(phone.length() != 11){
            Toast.makeText(LoginActivity.this, "输入11位手机号码", Toast.LENGTH_LONG).show();
            return;
        }
        BmobSMS.requestSMSCode(getApplication(), phone, "verifySms", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_login.setText("验证登录");
        isSend = true;
    }

    public void loginWithAuth(final BmobUser.BmobThirdUserAuth authInfo){
        BmobUser.loginWithAuthData(LoginActivity.this, authInfo, new OtherLoginListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (userManager.getCurrentUser() != null) {
//                    showToast("当前用户：" + userManager.getCurrentUser().getUsername());
                    Log.e("当前用户：", userManager.getCurrentUser().getUsername());
                } else {
                    showToast("没有用户");
                    Log.e("当前用户：", "没有用户");
                }
                /**
                 * 获取QQ个人信息
                 */
                QQToken qqToken = mTencent.getQQToken();
                UserInfo info = new UserInfo(getApplicationContext(), qqToken);
                info.getUserInfo(new IUiListener() {

                    public void onComplete(final Object response) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "---------------111111");
                        Message msg = new Message();
                        msg.obj = response;
                        //what = 0 获取文字信息
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                        Log.e(TAG, "-----111---" + response.toString());
                    }

                    public void onCancel() {
                        Log.e(TAG, "--------------111112");
                        // TODO Auto-generated method stub

                    }
                    public void onError(UiError arg0) {
                        // TODO Auto-generated method stub
                        Log.e(TAG, "-111113" + ":" + arg0);
                    }
                });

//                Toast.makeText(getApplicationContext(), "third login success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "third login failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                JSONObject response = (JSONObject) msg.obj;
                if (response.has("nickname")) {
                    try {
                        nicknameString = response.getString("nickname");
                        avatarUrl = response.getString("figureurl_qq_2");
                        Log.e(TAG, "--" + nicknameString);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            final User user = userManager.getCurrentUser(User.class);
            User u = new User();
            if (userManager.getCurrentUser().getNick() == null) {
                u.setNick(nicknameString);
            }
            if (userManager.getCurrentUser().getAvatar() == null) {
                u.setAvatar(avatarUrl);
            }
            u.setObjectId(user.getObjectId());
            u.update(getApplication(), new UpdateListener() {
                @Override
                public void onSuccess() {
//                    Toast.makeText(getApplicationContext(), "QQ登录成功", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(getApplicationContext(), "error "+ s, Toast.LENGTH_LONG).show();
                }
            });

            Intent intent1 = new Intent();
            intent1.setClass(LoginActivity.this, MainActivity2.class);
            startActivity(intent1);

            finish();
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
