package com.example.skywish.imtest001.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.broadcast.MyMessageReceiver;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.ui.fragment.AlarmFragment;
import com.example.skywish.imtest001.ui.fragment.FindFragment;
import com.example.skywish.imtest001.ui.fragment.RecentContactFragment;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;

/**
 * Created by skywish on 2015/7/7.
 */
public class MainActivity2 extends BaseActivity implements View.OnClickListener, EventListener{

    private RelativeLayout layout_alarm, layout_find, layout_message;
    private FrameLayout layout_fragment;
    private TextView tv_alarm, tv_find, tv_message;
    private ImageView iv_alarm, iv_find, iv_message;
    private FragmentManager fragmentManager;
    private AlarmFragment alarmFragment;
    private FindFragment findFragment;
    private RecentContactFragment contactFragment;
    Toolbar toolbar;
    User user;
    String avatarUrl, nickName, words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        user = userManager.getCurrentUser(User.class);
        initView();
        initDrawer();
        fragmentManager = getFragmentManager();
        setTabSelection(0);

    }

    private void initView() {
        toolbar = (android.support.v7.widget.Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("主页"); // 标题的文字需在setSupportActionBar之前，不然会无效
        toolbar.setTitleTextColor(Color.WHITE);
        this.setSupportActionBar(toolbar);

        layout_fragment = (FrameLayout) findViewById(R.id.layout_fragment);

        layout_alarm = (RelativeLayout) findViewById(R.id.layout_alarm);
        layout_find = (RelativeLayout) findViewById(R.id.layout_find);
        layout_message = (RelativeLayout) findViewById(R.id.layout_message);

        tv_alarm = (TextView) findViewById(R.id.tv_alarm);
        tv_find = (TextView) findViewById(R.id.tv_find);
        tv_message = (TextView) findViewById(R.id.tv_message);

        iv_alarm = (ImageView) findViewById(R.id.iv_alarm);
        iv_find = (ImageView) findViewById(R.id.iv_find);
        iv_message = (ImageView) findViewById(R.id.iv_message);

        layout_alarm.setOnClickListener(this);
        layout_find.setOnClickListener(this);
        layout_message.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_alarm:
                setTabSelection(0);
                break;
            case R.id.layout_find:
                setTabSelection(1);
                break;
            case R.id.layout_message:
                setTabSelection(2);
                break;
        }
    }

    public void initDrawer() {
        final IProfile profile;
        nickName = user.getNick();
        avatarUrl = user.getAvatar();
        Log.i("TEST", "avatarURL:" + avatarUrl);
        words = user.getWords();
        Log.i("DEBUG", "USER:" + user + ":" + user.getNick() + user.getAvatar());
        if (words == null || words.isEmpty()) {
            words = getResources().getString(R.string.no_words);
        }
        if (nickName == null || nickName.isEmpty()) {
            nickName = user.getUsername();
        }
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            profile = new ProfileDrawerItem().withName(nickName).withEmail(words).
                    withIcon(getResources().getDrawable(R.drawable.ic_default_profile_head)).withIdentifier(0);
        } else {
            profile = new ProfileDrawerItem().withName(nickName).withEmail(words).
                    withIcon(avatarUrl).withIdentifier(0);
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg_drawer)
                .addProfiles(profile)
                .build();

        //Now create your drawer and pass the AccountHeader.Result
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("我的录音").withIcon(
                                R.drawable.ic_mic_black_24dp).withIdentifier(1),
                        new PrimaryDrawerItem().withName("我的收藏").withIcon(
                                R.drawable.ic_stars_black_24dp).withIdentifier(2),
                        new PrimaryDrawerItem().withName("我的好友").withIcon(
                                R.drawable.ic_people_black_24dp).withIdentifier(3),
                        new PrimaryDrawerItem().withName("叫醒列表").withIcon(
                                R.drawable.ic_list_black_24dp).withIdentifier(6),
                        new PrimaryDrawerItem().withName("账号绑定").withIcon(
                                R.drawable.ic_insert_link_black_24dp).withIdentifier(7),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("设置").withIcon(
                                R.drawable.ic_settings_black_24dp).withIdentifier(4),
                        new PrimaryDrawerItem().withName("退出").withIcon(
                                R.drawable.ic_exit_to_app_black_24dp).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            switch (drawerItem.getIdentifier()) {
                                case 0:
                                    intent = new Intent(MainActivity2.this, EditInfoActivity.class);
                                    break;
                                //我的录音
                                case 1:
                                    intent = new Intent(MainActivity2.this, MyRecordActivity.class);
                                    break;
                                //我的收藏
                                case 2:
                                    break;
                                //我的好友
                                case 3:
                                    intent = new Intent(MainActivity2.this, MyFriendActivity.class);
                                    break;
                                //设置
                                case 4:
                                    intent = new Intent(MainActivity2.this, EditInfoActivity.class);
                                    break;
                                //退出
                                case 5:
                                    CustomApplication.getInstance().logout();
                                    intent = new Intent(MainActivity2.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                //叫醒列表
                                case 6:
                                    intent = new Intent(MainActivity2.this, CallUpActivity.class);
                                    break;
                                //账号绑定
                                case 7:
                                    intent = new Intent(MainActivity2.this, AccountActivity.class);
                                    break;
                                default:
                                    break;
                            }
                            if (intent != null) {
                                startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSelectedItem(-1)
                .build();
    }

    private void setTabSelection(int index) {
        clearSelection();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                this.getSupportActionBar().setTitle("闹钟");
                iv_alarm.setBackgroundResource(R.drawable.ic_alarm_press);
                tv_alarm.setTextColor(getResources().getColor(R.color.bottom_press));
                if (alarmFragment == null) {
                    alarmFragment = new AlarmFragment();
                    transaction.add(R.id.layout_fragment, alarmFragment);
                } else {
                    transaction.show(alarmFragment);
                }
                break;
            case 1:
                this.getSupportActionBar().setTitle("发现");
                iv_find.setBackgroundResource(R.drawable.ic_find_press);
                tv_find.setTextColor(getResources().getColor(R.color.bottom_press));
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    transaction.add(R.id.layout_fragment, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                break;
            case 2:
                this.getSupportActionBar().setTitle("信息");
                iv_message.setBackgroundResource(R.drawable.ic_message_press);
                tv_message.setTextColor(getResources().getColor(R.color.bottom_press));
                if (contactFragment == null) {
                    contactFragment = new RecentContactFragment();
                    transaction.add(R.id.layout_fragment, contactFragment);
                } else {
                    transaction.show(contactFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /**
     */
    private void clearSelection() {
        iv_alarm.setBackgroundResource(R.drawable.ic_alarm_normal);
        tv_alarm.setTextColor(getResources().getColor(R.color.bottom_font_color));
        iv_find.setBackgroundResource(R.drawable.ic_find_normal);
        tv_find.setTextColor(getResources().getColor(R.color.bottom_font_color));
        iv_message.setBackgroundResource(R.drawable.ic_message_normal);
        tv_message.setTextColor(getResources().getColor(R.color.bottom_font_color));
    }

    private void refreshProfile() {

    }
    /**
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (alarmFragment != null) {
            transaction.hide(alarmFragment);
        }
        if (contactFragment != null) {
            transaction.hide(contactFragment);
        }
        if (findFragment != null) {
            transaction.hide(findFragment);
        }
    }

    /**
     * 加入EventListener队列，表示消息有activity接受
     */
    @Override
    protected void onResume() {
        super.onResume();
        MyMessageReceiver.elList.add(this);// 监听推送的消息

    }

    /**
     * 退出EventListener队列，表示消息没人接受
     */
    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.elList.remove(this);// 取消监听推送的消息
    }

    /**
     * EventListener接口实现
     * @param bmobMsg
     */
    @Override
    public void onMessage(BmobMsg bmobMsg) {
        refreshNewMsg(bmobMsg);
    }

    /**
     * 新消息 到来的处理
     * @param message
     */
    private void refreshNewMsg(BmobMsg message){
        // 声音提示
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            CustomApplication.getInstance().getMediaPlayer().start();
        }
//        iv_recent_tips.setVisibility(View.VISIBLE);
        //也要存储起来
        if(message != null){
            BmobChatManager.getInstance(MainActivity2.this).saveReceiveMessage(true, message);
        }
    }

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {
        Log.i("Bmob", "MainActivity-onAddUser-收到好友请求：" + bmobInvitation.getFromname());
        refreshInvite(bmobInvitation);
    }

    /**
     * 新邀请 到来的处理
     * @param message
     */
    private void refreshInvite(BmobInvitation message){
        Log.i("Bmob", "MainActivity-refreshInvite-收到好友请求1：" + message.getFromname());
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        if(isAllow){
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        String tickerText = message.getFromname()+"请求添加好友";
        boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
        Log.i("Bmob", "MainActivity-refreshInvite-收到好友请求2：" + message.getFromname());
        BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, R.drawable.ic_launcher,
                tickerText, message.getFromname(), tickerText.toString(), MyFriendActivity.class);
        Log.i("Bmob", "MainActivity-refreshInvite-收到好友请求3：" + message.getFromname());
    }

    @Override
    public void onNetChange(boolean b) {
        if(b){
            showToast("请检查网络");
        }
    }

    @Override
    public void onOffline() {

    }
}
