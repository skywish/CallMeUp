package com.example.skywish.imtest001.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.skywish.imtest001.broadcast.MyMessageReceiver;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.MessageAdapter;
import com.example.skywish.imtest001.config.BombConstants;
import com.example.skywish.imtest001.util.CommonUtils;

import java.io.File;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.UploadListener;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by skywish on 2015/6/27.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, EventListener{

    private Button btn_voice, btn_speak, btn_more, btn_send, btn_emoji, btn_keyborad;
    private TextView tv_picture, tv_camera, tv_location;
    private EditText et_message;
    private LinearLayout layout_more, layout_emoji, layout_add;
    //目标用户
    private BmobChatUser targetUser;
    private String targetId, TAG = "CHAT", targetName;
    private int msgPageNum;
    private MessageAdapter messageAdapter;
    private List<BmobMsg> list_message;
    private ListView lv_message;
    private PtrClassicFrameLayout pullToRefreshLayout;
    private String localCameraPath = "";// 拍照后得到的图片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        msgPageNum = 0;
        manager = BmobChatManager.getInstance(this);
        //从UserInfoActivity跳转而来
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
        targetName = targetUser.getUsername();
        Log.i(TAG, "用户id是" + targetId);
        initView();
        initMsgOrRefresh();
    }

    private void initView() {
        initToolbarWithBack(targetName);

        //初始化listview
        lv_message = (ListView) findViewById(R.id.list_chat_message);

        //设置下拉刷新控件
        pullToRefreshLayout = (PtrClassicFrameLayout) findViewById(R.id.pull_to_refresh_layout);
        pullToRefreshLayout.setLastUpdateTimeRelateObject(this);
        pullToRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                pullToRefreshMsg();
            }
        });

        btn_send = (Button) findViewById(R.id.btn_chat_send);
        btn_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_emoji = (Button) findViewById(R.id.btn_chat_emoji);
        btn_speak = (Button) findViewById(R.id.btn_chat_speak);
        btn_more = (Button) findViewById(R.id.btn_chat_more);
        btn_keyborad = (Button) findViewById(R.id.btn_chat_keyboard);

        btn_send.setOnClickListener(this);
        btn_voice.setOnClickListener(this);
        btn_emoji.setOnClickListener(this);
        btn_speak.setOnClickListener(this);
        btn_more.setOnClickListener(this);
        btn_keyborad.setOnClickListener(this);

        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emoji = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);

        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);

        tv_location.setOnClickListener(this);
        tv_picture.setOnClickListener(this);
        tv_camera.setOnClickListener(this);

        et_message = (EditText) findViewById(R.id.et_message_write);
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_message.getText().toString().isEmpty()) {
                    btn_more.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                } else {
                    btn_more.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 点击输入框： 如果下拉栏在，就取消下拉栏
            case R.id.et_message_write:
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_more.setVisibility(View.GONE);
                    layout_add.setVisibility(View.GONE);
                    layout_emoji.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_send:
                sendMsg();
                break;
            //点击左边的语音按钮
            case R.id.btn_chat_voice:
                et_message.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_voice.setVisibility(View.GONE);
                btn_keyborad.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            // 点击表情： 如果emoji在，消失；不在，出现.
            case R.id.btn_chat_emoji:
                if (layout_more.getVisibility() == View.GONE) {
                    hideSoftInputView();
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.GONE);
                    layout_emoji.setVisibility(View.VISIBLE);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emoji.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            // 点击keyboard：按住说话BUTTON消失,EDITVIEW出现,layout_more消失
            case R.id.btn_chat_keyboard:
                hideSoftInputView();
                btn_speak.setVisibility(View.GONE);
                et_message.setVisibility(View.VISIBLE);
                btn_keyborad.setVisibility(View.GONE);
                btn_voice.setVisibility(View.VISIBLE);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_more.setVisibility(View.GONE);
                    layout_add.setVisibility(View.GONE);
                    layout_emoji.setVisibility(View.GONE);
                }
                break;
            // 点击+BUTTON,出现layout_more和add
            case R.id.btn_chat_more:
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emoji.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emoji.getVisibility() == View.VISIBLE) {
                        layout_emoji.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tv_camera:
                takePicture();
                break;
            case R.id.tv_location:
                break;
            case R.id.tv_picture:
                pickPicture();
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    public void takePicture() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(BombConstants.BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, BombConstants.REQUESTCODE_TAKE_CAMERA);
    }

    public void pickPicture() {
        Intent intent;
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, BombConstants.REQUESTCODE_TAKE_LOCAL);
    }

    public void sendImageMessage(String uri) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emoji.setVisibility(View.GONE);
        }
        manager.sendImageMessage(targetUser, uri, new UploadListener() {
            @Override
            public void onStart(BmobMsg bmobMsg) {
                Log.i("Image", "开始上传onStart：" + bmobMsg.getContent() + ",状态："
                        + bmobMsg.getStatus());
                refreshMessage(bmobMsg);
            }

            @Override
            public void onSuccess() {
                Log.i("Image", "上传成功");
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("Image", "上传失败");
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BombConstants.REQUESTCODE_TAKE_CAMERA:
                    sendImageMessage(localCameraPath);
                    break;
                case BombConstants.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        Log.i("runtime", selectedImage + "");
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null || localSelectPath.equals("null")) {
                                showToast("找不到您想要的图片");
                                return;
                            }
                            sendImageMessage(localSelectPath);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 发送消息
     */
    public void sendMsg() {
        final String msg = et_message.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            showToast("请输入消息！");
            return;
        }
        if (!CommonUtils.isNetworkAvailable(this)) {
            showToast("请联网！");
            return;
        }
        //发送消息
        BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
        message.setExtra("Bmob");
        manager.sendTextMessage(targetUser, message);

        //更新会话表
        refreshMessage(message);
    }

    /**
     * 添加消息，刷新adapter
     * @param msg
     */
    private void refreshMessage(BmobMsg msg) {
        messageAdapter.add(msg);
        et_message.setText("");
    }

    /**
     * 初始化消息，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
        //在MymessageReceiver中已经存在数据库中
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId, msgPageNum);
        return list;
    }

    /**
     * 初始化adapter，或者刷新adapter
     */
    private void initMsgOrRefresh() {
        if (messageAdapter != null) {
            if (MyMessageReceiver.mNewNum != 0 ){
                // 当有新消息时，按来的时间添加到adapter。
                int news = MyMessageReceiver.mNewNum;
                int size = initMsgData().size();
                for (int i = news; i > 0; i--) {
                    messageAdapter.add(initMsgData().get(size - i));
                }
            } else {
                messageAdapter.notifyDataSetChanged();
            }
        } else {
            messageAdapter = new MessageAdapter(this, initMsgData());
            lv_message.setAdapter(messageAdapter);
        }
    }

    /**
     * 下拉刷新事件
     */
    private void pullToRefreshMsg() {
        msgHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                msgPageNum++;
                int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
                int currents = messageAdapter.getCount();
                if (total <= currents) {
                    showToast("聊天记录加载完了哦。");
                } else {
                    List<BmobMsg> msgList = initMsgData();
                    messageAdapter.setList(msgList);
                    lv_message.setSelection(messageAdapter.getCount() - currents - 1);
                }
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取MyMessageReceiver中的elList
        initMsgOrRefresh();
        MyMessageReceiver.elList.add(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MyMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.elList.remove(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(BombConstants.ACTION_CHAT_FINISH));
        Log.i("RECENT", "sendBroadcast(new Intent(BombConstants.ACTION_CHAT_FINISH));");
    }

    @Override
    public void onMessage(BmobMsg bmobMsg) {
        Log.i("ChatActivity", "onmessage");
        Message handleMsg = msgHandler.obtainMessage(NEW_MESSAGE);
        handleMsg.obj = bmobMsg;
        msgHandler.sendMessage(handleMsg);
    }

    /**
     * onMessage处理
     */
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg receiveMsg = (BmobMsg) msg.obj;
                String fromId = receiveMsg.getBelongId();
                BmobMsg showMsg = BmobChatManager.getInstance(ChatActivity.this).
                        getMessage(receiveMsg.getConversationId(), receiveMsg.getMsgTime());
                // 消息来源是否与当前谈话对象一致
                if (!fromId.equals(targetId)) {
                    return;
                }
                Log.i("ChatActivity", "收到了");
                // 取消未读标志
                messageAdapter.add(showMsg);
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };

    @Override
    public void onOffline() {
        Log.i("ChatActivity", "下线");
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {
    }

    @Override
    public void onNetChange(boolean b) {
        if (!b) {
            showToast("当前网络不可用");
        }
    }

    @Override
    public void onReaded(String s, String s1) {

    }


}
