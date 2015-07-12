package com.example.skywish.imtest001.ui;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.DBRecord;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.view.MasterLayout;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


public class RecordActivity extends BaseActivity {
    static MasterLayout masterLayout;
    private String fileName = null;
    private MediaRecorder mediaRecorder = null;
    private Date startTime;
    private Date endTime;
    private boolean isSDcardExit;
    private SimpleDateFormat dateFormat;
    private static TextView secondShowView;
    private RecordTask task;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initToolbarWithBack("上传录音");

        user = userManager.getCurrentUser(User.class);

        masterLayout = (MasterLayout) findViewById(R.id.addRecordBtn);
        secondShowView = (TextView) findViewById(R.id.secondShowView);
        masterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                masterLayout.animation();
                switch (masterLayout.flg_frmwrk_mode) {
                    case 1:
                        startRecord();
                        task = new RecordTask();
                        task.execute();
                        secondShowView.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        secondShowView.setVisibility(View.GONE);
                        Toast.makeText(RecordActivity.this, "声音正在上传...", Toast.LENGTH_SHORT).show();
                        task.cancel(true);
                        masterLayout.reset();
                        stopRecord();
                        break;
                    case 3:
                        Toast.makeText(RecordActivity.this, "声音正在上传...", Toast.LENGTH_SHORT).show();
                        task.cancel(true);
                        stopRecord();
                        masterLayout.setClickable(false);
                        break;
                }
            }
        });
    }

    static class RecordTask extends AsyncTask<String, Integer, String> {
        private int myProgress;
        @Override
        protected void onPreExecute() {
            myProgress = 0;
            secondShowView.setText("60s");
        }

        @Override
        protected String doInBackground(String... params) {
            while(myProgress< 60){
                myProgress++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    secondShowView.setVisibility(View.INVISIBLE);
                    myProgress =0;
                    masterLayout.cusview.reset();
                    publishProgress(0);
                    onProgressUpdate(0);
                    e.printStackTrace();
                }
                publishProgress(myProgress);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            masterLayout.cusview.setupprogress(progress[0]);
            if(progress[0] == 60){
                secondShowView.setVisibility(View.INVISIBLE);
            }
            secondShowView.setText(String.valueOf(60 - progress[0]) + "s");
        }

        @Override
        protected void onCancelled() {
            myProgress = 0;
            publishProgress(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(task!=null)
            task.cancel(true);
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void startRecord() {
        try {
            isSDcardExit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (isSDcardExit) {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mediaRecorder.setMaxDuration(60000);
                startTime = new Date(System.currentTimeMillis());
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                fileName += "/" + dateFormat.format(startTime) + ".amr";
                mediaRecorder.setOutputFile(fileName);
                mediaRecorder.prepare();
                mediaRecorder.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        endTime = new Date(System.currentTimeMillis());
        long totalTime = (endTime.getTime() - startTime.getTime()) / 1000;
        if (totalTime >= 60) {
            totalTime = 60;
        }
        File file = new File(fileName);
        if (totalTime < 3) {
            file.delete();
            Toast.makeText(this, "录音时间小于3秒，请重新录音", Toast.LENGTH_SHORT).show();
        }else {
            final String formatTime = dateFormat.format(new Date());
            final BmobFile bmobFile = new BmobFile(file);
            final long finalTotalTime = totalTime;
            String sex = "";
            if (user.getSex()) {
                sex = "男";
            } else {
                sex = "女";
            }
            final String trueSex = sex;
            bmobFile.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    String recordUrl = bmobFile.getFileUrl(RecordActivity.this);
                    insertObject(new DBRecord(user.getUsername(), trueSex, recordUrl, formatTime, finalTotalTime));
                    Toast.makeText(RecordActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    if(finalTotalTime == 60){
                        RecordActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    Toast.makeText(getApplicationContext(), "上传失败" + arg1, Toast.LENGTH_SHORT).show();
                }
            });
        }
        fileName = null;
    }

    private void insertObject(final BmobObject obj) {
        obj.save(RecordActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
//                Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
//                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_record, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
