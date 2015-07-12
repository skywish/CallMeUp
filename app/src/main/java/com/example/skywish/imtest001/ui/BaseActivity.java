package com.example.skywish.imtest001.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.util.CommonUtils;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * Created by skywish on 2015/6/27.
 */
public class BaseActivity extends ActionBarActivity {

    BmobUserManager userManager;
    BmobChatManager manager;

    CustomApplication mApplication;
    //百度地址
    LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
    }

    Toast mToast;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    public void initToolbarWithBack(String title) {
        Toolbar toolbar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(title); // 标题的文字需在setSupportActionBar之前，不然会无效
        toolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     *
     * @param photoUrl 本地地址或网上地址
     * @param imageView
     */
    public void showPhoto(String photoUrl, ImageView imageView) {
        // 没有网显示本地头像
        if (photoUrl != null && !photoUrl.equals("")) {
            ImageLoader.getInstance().displayImage(photoUrl, imageView,
                    ImageLoadOptions.getOptions());
        } else {
            imageView.setImageResource(R.drawable.ic_default_profile_head);
        }
    }

    /**
     * 隐藏软输入
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
