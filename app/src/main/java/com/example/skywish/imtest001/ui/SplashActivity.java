package com.example.skywish.imtest001.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.SDKInitializer;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.config.Configs;

import cn.bmob.im.BmobChat;

/**
 * Created by skywish on 2015/6/27.
 */
public class SplashActivity extends BaseActivity{

    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BmobChat.DEBUG_MODE = true;
        BmobChat.getInstance(this).init(Configs.applicationId);
        SDKInitializer.initialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userManager.getCurrentUser() != null) {
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    Intent intent = new Intent(SplashActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                    break;
                case GO_LOGIN:
                    Intent intent2 = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };
}
