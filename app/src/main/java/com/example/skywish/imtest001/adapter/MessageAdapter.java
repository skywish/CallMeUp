package com.example.skywish.imtest001.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.base.BaseListAdapter;
import com.example.skywish.imtest001.ui.ImageActivity;
import com.example.skywish.imtest001.ui.UserInfoActivity;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.example.skywish.imtest001.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.DownloadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.im.util.BmobUtils;

/**
 * Created by skywish on 2015/7/2.
 */
public class MessageAdapter extends BaseListAdapter<BmobMsg> {

    //8种Item的类型
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //语音是否播放
//    public static boolean isPlaying = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private AnimationDrawable anim = null;
    String currentObjectId = "";
    DisplayImageOptions options;
    //上一条短信的时间,最近短信时间
    public static long lastMessageTime = 0, newMessageTime;

    private ImageLoadingListener firstDisplayListener = new firstDisplayListener();

    public MessageAdapter(Context context, List<BmobMsg> list) {
        super(context, list);
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();

        options = new DisplayImageOptions.Builder()
//                .showImageForEmptyUri(R.drawable.ic_launcher)
//                .showImageOnFail(R.drawable.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    @Override
    public int getItemViewType(int position) {
        BmobMsg msg = list.get(position);
        switch (msg.getMsgType()) {
            case BmobConfig.TYPE_IMAGE:
                return msg.getBelongId().equals(currentObjectId) ?
                        TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
            case BmobConfig.TYPE_TEXT:
                return msg.getBelongId().equals(currentObjectId) ?
                        TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
            case BmobConfig.TYPE_VOICE:
                return msg.getBelongId().equals(currentObjectId) ?
                        TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
            case BmobConfig.TYPE_LOCATION:
                return msg.getBelongId().equals(currentObjectId) ?
                        TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
            default:
                Log.i("ERROR", "MessageAdapter getItemViewType default");
                return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    private View createViewByType(BmobMsg message, int position) {
        int type = message.getMsgType();
        switch (type) {
            case BmobConfig.TYPE_IMAGE:
                return getItemViewType(position) == TYPE_RECEIVER_IMAGE ?
                        mInflater.inflate(R.layout.chat_received_picture, null) :
                        mInflater.inflate(R.layout.chat_send_picture, null);
            case BmobConfig.TYPE_VOICE:
                return getItemViewType(position) == TYPE_RECEIVER_VOICE ?
                        mInflater.inflate(R.layout.chat_receive_voice, null) :
                        mInflater.inflate(R.layout.chat_send_voice, null);
            case BmobConfig.TYPE_LOCATION:
                //TODO
                return null;
            default:
//                默认为文本
//            case BmobConfig.TYPE_TEXT:
                return getItemViewType(position) == TYPE_RECEIVER_TXT ?
                        mInflater.inflate(R.layout.chat_received_text, null) :
                        mInflater.inflate(R.layout.chat_send_text, null);
        }
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        final BmobMsg item = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(item, position);
        }

        //通用
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.head_imageView);     //头像
        ImageView iv_fail = ViewHolder.get(convertView, R.id.msg_error_imageView);  //错误标志
        final ProgressBar pgb_load = ViewHolder.get(convertView, R.id.msg_sending_progressBar);   //转圈
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);        //时间

        // 文本类型
        TextView tv_message = ViewHolder.get(convertView, R.id.reply_textView);     //对话内容

        //图片
        ImageView iv_imagemessage = ViewHolder.get(convertView, R.id.reply_imageView);      //图像

        //语音
        final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
        final ImageView iv_anim_voice = ViewHolder.get(convertView, R.id.iv_anim_voice);
        final TextView tv_voice_length = ViewHolder.get(convertView, R.id.tv_voice_length);

        /**
         * 头像
         */
        String avatar = item.getBelongAvatar();
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar,
                    ImageLoadOptions.getOptions(), firstDisplayListener);
        } else {
            iv_avatar.setImageResource(R.drawable.ic_default_profile_head);
        }
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 根据谁的消息进入相关的activity,false来自对方，true来自我方
                if (fromWho(getItemViewType(position))) {
                    //TODO
                } else {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    intent.putExtra("username", item.getBelongUsername());
                    mContext.startActivity(intent);
                }
            }
        });

        /**
         * 设置回复时间
         */
        tv_time.setVisibility(View.VISIBLE);
        tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));

        /**
         * 设定控件显示规则，发送状态改变 progress 和 fail image
         */
        if (getItemViewType(position) == TYPE_SEND_TXT || getItemViewType(position) == TYPE_SEND_VOICE ||
                getItemViewType(position) == TYPE_SEND_LOCATION) {
            switch (item.getStatus()) {
                //开始发送
                case BmobConfig.STATUS_SEND_START:
                    pgb_load.setVisibility(View.VISIBLE);
                    iv_fail.setVisibility(View.INVISIBLE);
                    if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                        tv_voice_length.setVisibility(View.VISIBLE);
                    }
                    break;
                //发送成功
                case BmobConfig.STATUS_SEND_SUCCESS:
                    pgb_load.setVisibility(View.INVISIBLE);
                    iv_fail.setVisibility(View.INVISIBLE);
                    if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                        tv_voice_length.setVisibility(View.GONE);
                    }
                    break;
                //发送失败
                case BmobConfig.STATUS_SEND_FAIL:
                    pgb_load.setVisibility(View.INVISIBLE);
                    iv_fail.setVisibility(View.VISIBLE);
                    if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
                        tv_voice_length.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }

        /**
         * 根据类型显示内容
         */
        final String text = item.getContent();
        switch (item.getMsgType()) {
            /**
             * 显示文本
             */
            case BmobConfig.TYPE_TEXT:
                try {
                    //TODO 表情
                    tv_message.setText(text);
                } catch (Exception e) {
                }
                break;
            /**
             * 显示图片
             */
            case BmobConfig.TYPE_IMAGE:
                try {
                    if (text != null && !text.equals("")) {
                        //设定显示规则
                        dealWithImage(position, pgb_load, iv_fail, iv_imagemessage, item);
                    }
                    //点击图片事件
                    iv_imagemessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO MessageAdapter 点击图片事件
                            Intent intent = new Intent(mContext, ImageActivity.class);
                            String imageUri = getImageUrl(item);
                            intent.putExtra("image", imageUri);
                            mContext.startActivity(intent);
                        }
                    });
                } catch (Exception e) {

                }
                break;
            case BmobConfig.TYPE_LOCATION:
                break;
            /**
             * 语音
             */
            case BmobConfig.TYPE_VOICE:
                try {
                    if (text != null && !text.equals("")) {
                        String content = item.getContent();
                        Log.i("Test", "voice content is:" + content);
                        if (item.getBelongId().equals(currentObjectId)) {
                            //通过content获取长度
                            if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {
                                tv_voice_length.setVisibility(View.VISIBLE);
                                String length = content.split("&")[2];
                                tv_voice_length.setText(length + "\''");
                            } else {
                                tv_voice_length.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            //检测指定的文件是否存在
                            boolean isExist = BmobDownloadManager.checkTargetPathExist(currentObjectId, item);
                            if (!isExist) {
                                String uri = content.split("&")[0];
                                final String length = content.split("&")[1];
                                BmobDownloadManager downloadManager = new BmobDownloadManager(mContext, item, new DownloadListener() {
                                    @Override
                                    public void onStart() {
                                        pgb_load.setVisibility(View.VISIBLE);
                                        tv_voice_length.setVisibility(View.GONE);
                                        iv_voice.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        pgb_load.setVisibility(View.GONE);
                                        tv_voice_length.setVisibility(View.VISIBLE);
                                        tv_voice_length.setText(length + "\''");
                                        iv_voice.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError(String s) {
                                        pgb_load.setVisibility(View.GONE);
                                        tv_voice_length.setVisibility(View.GONE);
                                        iv_voice.setVisibility(View.INVISIBLE);
                                    }
                                });
                                downloadManager.execute(uri);
                            } else {
                                String length = content.split("&")[2];
                                tv_voice_length.setText(length + "\''");
                            }
                        }
                    }
                    String localPath = "";
                    if (item.getBelongId().equals(currentObjectId)) {// 如果是自己发送的语音消息，则播放本地地址
                        localPath = item.getContent().split("&")[0];
                    } else {
                        // 如果是收到的消息，则需要先下载后播放
                        localPath = getDownLoadFilePath(mContext, item);
                        Log.i("voice", "收到的语音存储的地址:" + localPath);
                    }
                    initMediaPlayer(localPath);
                    //播放语音
                    iv_voice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //如果正在播放就停止播放
                            if (mediaPlayer.isPlaying()) {
                                stopAnimation(item, iv_voice, iv_anim_voice);
                                mediaPlayer.reset();
                                mediaPlayer.release();
                            } else {
                                //如果停止播放就开始播放
                                startAnimation(item, iv_voice, iv_anim_voice);
                                mediaPlayer.start();
                            }
                        }
                    });
                } catch (Exception e) {

                }

                break;
            default:
                break;
        }
        return convertView;
    }

    /**
     * 语音相关
     */
    public void stopAnimation(BmobMsg item, ImageView iv_voice, ImageView iv_anim_voice) {
        iv_voice.setVisibility(View.VISIBLE);
        iv_anim_voice.setVisibility(View.INVISIBLE);
        if (anim != null) {
            anim.stop();
        }
    }

    public void startAnimation(BmobMsg item, ImageView iv_voice, ImageView iv_anim_voice) {
        iv_voice.setVisibility(View.INVISIBLE);
        iv_anim_voice.setVisibility(View.VISIBLE);
        anim = (AnimationDrawable) iv_anim_voice.getDrawable();
        anim.start();
    }

    private void initMediaPlayer(String mediaPath) {
        try {
            FileInputStream fis = new FileInputStream(new File(mediaPath));
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDownLoadFilePath(Context context, BmobMsg msg) {
        String accountDir = BmobUtils.string2MD5(BmobUserManager.getInstance(context).getCurrentUserObjectId());
        File dir = new File(BmobConfig.BMOB_VOICE_DIR + File.separator
                + accountDir + File.separator + msg.getBelongId());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 在当前用户的目录下面存放录音文件
        File audioFile = new File(dir.getAbsolutePath() + File.separator
                + msg.getMsgTime() + ".amr");
        try {
            if (!audioFile.exists()) {
                audioFile.createNewFile();
            }
        } catch (IOException e) {
        }
        return audioFile.getAbsolutePath();
    }

    /**
     * 图片的显示，根据不同的对象，progressbar的显示与否，自己不需要显示
     * @param position
     * @param progressBar
     * @param iv_fail_resend
     * @param iv_picture
     * @param item
     */
    public void dealWithImage(int position,final ProgressBar progressBar,ImageView iv_fail_resend,
                              ImageView iv_picture,BmobMsg item) {
        String text = item.getContent();
        //自己发送的直接显示
        if (getItemViewType(position) == TYPE_SEND_IMAGE) {
            switch (item.getStatus()) {
                case BmobConfig.STATUS_SEND_START:
                    progressBar.setVisibility(View.VISIBLE);
                    iv_fail_resend.setVisibility(View.INVISIBLE);
                    break;
                case BmobConfig.STATUS_SEND_SUCCESS:
                    progressBar.setVisibility(View.INVISIBLE);
                    iv_fail_resend.setVisibility(View.INVISIBLE);
                    break;
                case BmobConfig.STATUS_SEND_FAIL:
                    progressBar.setVisibility(View.INVISIBLE);
                    iv_fail_resend.setVisibility(View.VISIBLE);
                    break;
            }
            //如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
            String showUrl = "";
            if(text.contains("&")){
                showUrl = text.split("&")[0];
            }else{
                showUrl = text;
            }
            ImageLoader.getInstance().displayImage(showUrl, iv_picture);
        } else {
            //别人的从网上下载
            ImageLoader.getInstance().displayImage(text, iv_picture, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    /**
     * 获取图片的地址--
     * @Description:
     * @param @param item
     * @param @return
     * @return String
     * @throws
     */
    private String getImageUrl(BmobMsg item){
        String showUrl = "";
        String text = item.getContent();
        if(item.getBelongId().equals(currentObjectId)){//
            if(text.contains("&")){
                showUrl = text.split("&")[0];
            }else{
                showUrl = text;
            }
        }else{//如果是收到的消息，则需要从网络下载
            showUrl = text;
        }
        return showUrl;
    }

    private boolean fromWho(int type) {
        switch (type) {
            case TYPE_RECEIVER_IMAGE:
            case TYPE_RECEIVER_TXT:
            case TYPE_RECEIVER_LOCATION:
            case TYPE_RECEIVER_VOICE:
                return false;
//            case TYPE_SEND_IMAGE:
//            case TYPE_SEND_LOCATION:
//            case TYPE_SEND_TXT:
//            case TYPE_SEND_VOICE:
            default:
                return true;
        }
    }

    private static class firstDisplayListener extends SimpleImageLoadingListener {
        // synchronizedList()
        // Returns a wrapper on the specified List which synchronizes all access to the List.
        // LinkedList是没有同步方法的，当多线程同时去访问List中的数据时，如果不同步会出错。
        // 之所以用linkedlist不用arraylist，是因为linkedlist插入更快速。虽然查找麻烦。
        // 因为下载是异步的，如果不用synchronizedList，可能会造成表重复add，判断失误。
        static final List<String> displayImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            // 检测是否已经有了，有了就不在添加动画。
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayImages.contains(imageUri);
                if (firstDisplay) {
                    // Displays image with "fade in" animation
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayImages.add(imageUri);
                }
            }
        }
    }
}
