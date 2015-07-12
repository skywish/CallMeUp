package com.example.skywish.imtest001.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout.LayoutParams;

import com.example.skywish.imtest001.bean.DBCallUp;
import com.example.skywish.imtest001.bean.DBClock;
import com.example.skywish.imtest001.bean.DBRecord;
import com.example.skywish.imtest001.util.MusicAction;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.view.SlidableButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.validation.Validator;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by DYY-PC on 2015/6/30.
 */
public class ClockAlert extends Activity {
    private Vibrator vibrator;
    private Button stopClockBtn;
    private String filePath;
    //    private MediaPlayer mediaPlayer;
    private long clockId;
    private MusicAction musicAction;
    private boolean isGirl;
    private boolean isSystem;
    private ArrayList<DBRecord> recordList;
    private DBCallUp callUp = new DBCallUp();
    private String objectId;
    DBClock clock;
    private SlidableButton myButton;
    WindowManager wm;
    int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alert);

        myButton = (SlidableButton) findViewById(R.id.btn_side);

        wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        vibrator = (Vibrator) getApplicationContext().getSystemService(getApplication().VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000, 2000}, 0);
//        stopClockBtn = (Button) findViewById(R.id.stopClockBtn);
//        stopClockBtn.setOnClickListener(new ButtonListener());
        Intent intent = getIntent();
        clock = (DBClock) intent.getSerializableExtra("clock");
        clockId = clock.getClockID();
        isGirl = clock.getIsGirl();
        isSystem = clock.getIsSystem();
        filePath = clock.getMusicPath();
        clock.setIsOpen(false);
        select(clockId);
        callUp.setUserName(clock.getUserName());
        musicAction = new MusicAction();
//        Toast.makeText(this, clock.getObjectId(), Toast.LENGTH_SHORT).show();
        Log.e("filePath", filePath);
        if(isSystem){
            musicAction.play(filePath, true);
        }else{
            if(isGirl){
                selectObject("女");
                callUp.setCallUpSex("女");
            }
            else{
                selectObject("男");
                callUp.setCallUpSex("男");
            }
        }

    }
    private void select(final long clockId){
        BmobQuery<DBClock> query = new BmobQuery<>();
        query.addWhereEqualTo("clockID", clockId);
        query.findObjects(this, new FindListener<DBClock>() {
            @Override
            public void onSuccess(List<DBClock> list) {
                for (DBClock clock : list) {
                    objectId = clock.getObjectId();
                }
            }

            @Override
            public void onError(int i, String s) {
//                Toast.makeText(ClockAlert.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        musicAction.stop();
        vibrator.cancel();
        updateObject(objectId);
        filePath = null;
        ClockAlert.this.finish();
//        if(mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
    }

    /**
     * 滑动事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //按钮被按下并且被拖动
        if (event.getAction() == MotionEvent.ACTION_UP && myButton.isDragging()) {
            //此处做了一个动画效果，可以去掉
            myButton.setDragging(false);
            LayoutParams lp = (LayoutParams) myButton.getLayoutParams();

            Log.i("Anim", "lp.rightMargin = "  + lp.rightMargin);
            Log.i("Anim", "lp.leftMargin = "  + lp.leftMargin);

            Log.i("Anim", "width = "  + width);

            //开始滑动动画
            //TranslateAnimation (int fromXType, float fromXValue, int toXType, float toXValue,
            // int fromYType, float fromYValue, int toYType, float toYValue)
            TranslateAnimation trans1 = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE,
                    0-lp.leftMargin, Animation.RELATIVE_TO_SELF, 0,
                    Animation.ABSOLUTE, 0);
            trans1.setStartOffset(0);
            //动画持续时间
            trans1.setDuration(500);
            //the animation transformation is applied before the animation has started.
            trans1.setFillBefore(true);

            trans1.setInterpolator(new AccelerateInterpolator());

            trans1.setAnimationListener(new AnimationListener() {

                //动画结束
                @Override
                public void onAnimationEnd(Animation animation) {
                    myButton.setAnimating(false);
                    myButton.setDragging(false);
                    LayoutParams lp = (LayoutParams) myButton.getLayoutParams();
                    lp.leftMargin = 0;
                    myButton.setLayoutParams(lp);

//                    updateObject(objectId);
//                    vibrator.cancel();
//                    musicAction.stop();
//                    filePath = null;
//                    ClockAlert.this.finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                //动画开始
                @Override
                public void onAnimationStart(Animation animation) {
                    myButton.setAnimating(true);
                }

            });
            myButton.startAnimation(trans1);

        } else {
            //定位button的位置
            int min = (int) (width * 0.787);
            myButton.setX(Math.min(min, Math.max(0, (int) event.getX()-30)));
            Log.i("Anim", "myButton = "  + myButton.getX());
            if (myButton.getX() >= min) {
                updateObject(objectId);
                vibrator.cancel();
                musicAction.stop();
                filePath = null;
                ClockAlert.this.finish();
            }

        }
        return false;
    }

    private void selectObject(String sex) {
        BmobQuery<DBRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("sex", sex);
        recordList  = new ArrayList<>();
        query.findObjects(ClockAlert.this, new FindListener<DBRecord>() {
            @Override
            public void onSuccess(List<DBRecord> list) {
                for (DBRecord dbRecord : list) {
                    recordList.add(dbRecord);
                }
                if (list.size() == 1) {
                    filePath = null;
                    filePath = recordList.get(0).getFileUrl();
                    musicAction.play(filePath, true);
                    callUp.setCallUpName(recordList.get(0).getUserName());
                    callUp.setCallUpRecordPath(filePath);
                } else {
                    Random random = new Random(System.currentTimeMillis());
                    int position = random.nextInt(list.size());
                    filePath = null;
                    filePath = recordList.get(position).getFileUrl();
                    callUp.setCallUpName(recordList.get(position).getUserName());
                    callUp.setCallUpRecordPath(filePath);
                    musicAction.play(filePath, true);
                }
                insertObject(callUp);
            }

            @Override
            public void onError(int i, String s) {
                musicAction.play(filePath, true);
                Toast.makeText(getApplicationContext(), "查询失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateObject(String clockId) {
        clock.setIsOpen(false);
        clock.update(ClockAlert.this, clockId, new UpdateListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {
//                Toast.makeText(getApplicationContext(), "修改失败：" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    class ButtonListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            updateObject(objectId);
//            vibrator.cancel();
//            musicAction.stop();
//            filePath = null;
//            ClockAlert.this.finish();
//        }
//    }

    private void insertObject(BmobObject object) {
        object.save(ClockAlert.this, new SaveListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "上传失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}