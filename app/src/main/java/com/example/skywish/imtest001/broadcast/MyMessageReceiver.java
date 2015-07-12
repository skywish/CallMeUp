package com.example.skywish.imtest001.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.skywish.imtest001.CustomApplication;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.ui.MyFriendActivity;
import com.example.skywish.imtest001.ui.fragment.FansFragment;
import com.example.skywish.imtest001.util.CollectionUtils;
import com.example.skywish.imtest001.util.CommonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by skywish on 2015/6/28.
 */
public class MyMessageReceiver extends BroadcastReceiver {

    public static ArrayList<EventListener> elList = new ArrayList<>();
    public static int mNewNum = 0;
    BmobChatUser currentUser;
    BmobUserManager userManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("msg");
        Log.i("MyMessageReceiver", "收到的message = " + json);

        userManager = BmobUserManager.getInstance(context);
        currentUser = userManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
        if(isNetConnected){
            // 如果有网络连接,转化Json
            parseMessage(context, json);
        }else{
            for (int i = 0; i < elList.size(); i++)
                ((EventListener) elList.get(i)).onNetChange(isNetConnected);
        }
    }

    /**
     * 解析Json
     * @param context
     * @param json
     */
    public void parseMessage(final Context context, String json) {
        JSONObject jo;
        try {
            jo = new JSONObject(json);
            // 这些都是msg数据库中的属性 tag，fromId，toId
            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
            // 接收到服务器发来的下线通知，强制该用户下线
            if (tag.equals(BmobConfig.TAG_OFFLINE)) {
                if (currentUser != null) {
                    if (elList.size() > 0) {
                        for (EventListener handler : elList) {
                            handler.onOffline();
                        }
                    } else {
                        CustomApplication.getInstance().logout();
                    }
                }
            } else {
                String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
                // 增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
                final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
                String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
                if (fromId != null) {
                    switch (tag) {
                        /**
                         * 不带标签，普通的message，可接受陌生人消息
                         */
                        case "":
                            // 创建收到的消息
                            BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
                                @Override
                                public void onSuccess(BmobMsg bmobMsg) {
                                    if (elList.size() > 0) {
                                        // 有监听的时候传递下去，使elList中的Activity或fragement能够接收到bmobMsg
                                        for (int i = 0; i < elList.size(); i++) {
                                            // 获取事件，传递到ChatActivity中的msgHandler处理
                                            ((EventListener) elList.get(i)).onMessage(bmobMsg);
                                            Log.i("MyMessageReceiver", "bmobMsg" + i);
                                        }
                                    } else {
                                        // 如果此时没有任何Activity监听，记录下新消息数量
                                        if (currentUser!=null && currentUser.getObjectId().equals(toId)) {
                                            mNewNum++;
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Log.i("MyMessageReceiver", "获取消息失败");
                                }
                            });
                            break;
                        /**
                         * 好友请求
                         */
                        case BmobConfig.TAG_ADD_CONTACT:
                            BmobInvitation message = BmobChatManager.getInstance(context).
                                    saveReceiveInvite(json, toId);
                            Log.i("Bmob", "MyMessageReceiver--收到好友请求：" + message.getFromname());
                            if (currentUser != null) {
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (elList.size() > 0) {
                                        for (EventListener handler : elList) {
                                            //在mainactivity或chatactivity实现
                                            handler.onAddUser(message);
                                        }
                                    } else {
                                        showOtherNotify(context, message.getFromname(), toId,
                                                message.getFromname()+"请求添加好友", MyFriendActivity.class);
                                    }
                                }
                            }
                            break;
                        /**
                         * 同意好友请求
                         */
                        case BmobConfig.TAG_ADD_AGREE:
                            String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
                            //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
                            BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {

                                @Override
                                public void onError(int arg0, final String arg1) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onSuccess(List<BmobChatUser> arg0) {
                                    // TODO Auto-generated method stub
                                    //保存到内存中
                                    Log.i("Bmob", "3.CustomApplication.getInstance()" + CustomApplication.getInstance());
                                    CustomApplication.getInstance().setContactList(
                                            CollectionUtils.list2map(BmobDB.create(context).getContactList()));
                                }
                            });
                            //显示通知
                            showOtherNotify(context, username, toId, username+"同意添加您为好友", MyFriendActivity.class);
                            //创建一个临时验证会话--用于在会话界面形成初始会话
//                            BmobMsg.createAndSaveRecentAfterAgree(context, json);
                            break;
                        default:
                            Log.i("MyMessageReceiver", "tag is default");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示其他Tag的通知
     * showOtherNotify
     */
    public void showOtherNotify(Context context,String username,String toId,String ticker,Class<?> cls){
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowPushNotify();
        boolean isAllowVoice = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
        if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
            //同时提醒通知
            BmobNotifyManager.getInstance(context).showNotify(isAllowVoice, isAllowVibrate,
                    R.drawable.ic_launcher, ticker, username, ticker.toString(), cls);
        }
    }
}
