package com.example.skywish.imtest001.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.util.CollectionUtils;
import com.example.skywish.imtest001.util.CommonUtils;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Method;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by skywish on 2015/6/29.
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_location, tv_words, tv_username, tv_nickname;
    private String location, blog, username, from;
//    private RelativeLayout btn_add, btn_chat;
    private Button btn_add, btn_chat, btn_edit;
    private User user;
    private CircleImageView iv_userinfo_head;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("TEST", "UserInfoActivity--onCreate ： " + "begin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo2);

        from = getIntent().getStringExtra("from");
        // 从MessageAdapter中的头像点击而来
        username = getIntent().getStringExtra("username");
        Log.i("TEST", "UserInfoActivity--onCreate ： " + username);

        initView();
        initData(username);
    }

    public void initView() {
        initToolbarWithBack("详细信息");
        iv_userinfo_head = (CircleImageView) findViewById(R.id.iv_userinfo_head);

        tv_words = (TextView) findViewById(R.id.tv_words);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_username = (TextView) findViewById(R.id.tv_username);

        btn_add = (Button) findViewById(R.id.btn_add);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_chat.setOnClickListener(this);

        if (from != null && from.equals("me")) {
            btn_add.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_edit.setVisibility(View.VISIBLE);
        } else {
            btn_add.setVisibility(View.VISIBLE);
            btn_chat.setVisibility(View.VISIBLE);
            btn_edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                if (!CommonUtils.isNetworkAvailable(this)) {
                    showToast(this.getString(R.string.no_network));
                    return;
                }
                addFriend();
                break;
            case R.id.btn_chat:
                if (!CommonUtils.isNetworkAvailable(this)) {
                    showToast(this.getString(R.string.no_network));
                    return;
                }
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_edit:
                Intent intent1 = new Intent(this, EditInfoActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //如果不是来自我，显示
        if (from != null && !from.equals("me")) {
            getMenuInflater().inflate(R.menu.menu_userinfo, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteFriend();
                finish();
                break;
            case R.id.action_addnote:
                // TODO
                showToast("备注...");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(String name) {
        userManager.queryUser(name, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (CollectionUtils.isNotNull(list)) {
                    user = list.get(0);
                    updateUser(user);
                } else {
                    showToast("查无此人");
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("查询出错");
            }
        });
    }

    private void updateUser(User user) {
        updatePhoto(user.getAvatar());
        //设置用户名
        tv_username.setText(username);
        //设置昵称
        if (user.getNick() == null || user.getNick().isEmpty()) {
            tv_nickname.setText(username);
        } else {
            tv_nickname.setText(user.getNick());
        }
        //设置地区
        if (user.getCity() == null
                || user.getCity().isEmpty()) {
            tv_location.setText(this.getString(R.string.no_city));
        } else {
            tv_location.setText(user.getCity());
        }
        //设置个性签名
        if (user.getWords() == null || user.getWords().isEmpty()) {
            tv_words.setText(this.getString(R.string.no_words));
        } else {
            tv_words.setText(user.getWords());
        }
    }

    private void updatePhoto(String photo) {
        if (photo != null && !photo.equals("")) {
            ImageLoader.getInstance().displayImage(photo, iv_userinfo_head,
                    ImageLoadOptions.getOptions());
        } else {
            iv_userinfo_head.setImageResource(R.drawable.ic_default_profile_head);
        }
    }

    /**
     * 通过发送TagMessage：BmobConfig.TAG_ADD_CONTACT，来让Bmob推送好友申请消息
     */
    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //发送好友请求TAG
        BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
                user.getObjectId(), new PushListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                showToast("请求发送成功，等待对方验证！");
            }

            @Override
            public void onFailure(int i, String s) {
                progress.dismiss();
                showToast("发送失败。请重试！");
            }
        });
    }

    /**
     * 使Menu overflow 栏一直存在
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void deleteFriend() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在删除...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        userManager.deleteContact(user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast("删除成功");
                CustomApplication.getInstance().getContactList().remove(user.getUsername());
                dialog.dismiss();
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("删除失败，请重试！");
                dialog.dismiss();
            }
        });
    }
    
}
