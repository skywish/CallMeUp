package com.example.skywish.imtest001.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.skywish.imtest001.CustomApplication;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * Created by skywish on 2015/7/3.
 */
public class BaseFragment extends Fragment {

    public BmobUserManager userManager;
    public BmobChatManager manager;
    public CustomApplication mApplication;
    public LayoutInflater mInflater;
    Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = CustomApplication.getInstance();
        userManager = BmobUserManager.getInstance(getActivity());
        manager = BmobChatManager.getInstance(getActivity());
        mInflater = LayoutInflater.from(getActivity());
    }

    public View findViewById(int paramInt) {
        return getView().findViewById(paramInt);
    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
