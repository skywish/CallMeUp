package com.example.skywish.imtest001.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.RecordListAdapter;
import com.example.skywish.imtest001.bean.DBRecord;
import com.example.skywish.imtest001.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;


public class MyRecordActivity extends BaseActivity {

    ListView recordListView;
    RecordListAdapter listAdapter;
    final CharSequence[] items = {"删除", "取消"};
    private ArrayList<DBRecord> recordList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_record);

        initToolbarWithBack("我的录音");
        user = userManager.getCurrentUser(User.class);
        queryData(user.getUsername());
        recordListView = (ListView) findViewById(R.id.recordList);
        recordListView.setOnItemLongClickListener(new ItemClickListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listAdapter != null)
            listAdapter.musicAction.stop();
    }

    class ItemClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyRecordActivity.this);
            builder.setTitle("提示");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            dialog.dismiss();
                            DBRecord record = recordList.get(position);
                            delete(record.getObjectId());
                            recordList.remove(position);
                            listAdapter.notifyDataSetChanged();
                            break;
                        case 1:
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
            return false;
        }
    }

    private void queryData(String userName) {
        BmobQuery<DBRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("userName", userName);
        query.order("-createdAt");
        query.findObjects(this, new FindListener<DBRecord>() {
            @Override
            public void onSuccess(List<DBRecord> list) {
                recordList = new ArrayList<>();
                if (list.size() > 0) {
                    for (DBRecord record : list) {
                        recordList.add(record);
                    }
                }
                listAdapter = new RecordListAdapter(getApplicationContext(), recordList);
                recordListView.setAdapter(listAdapter);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(), "查询失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(String objectId) {
        DBRecord record = new DBRecord();
        record.setObjectId(objectId);
        record.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String msg) {
                Toast.makeText(getApplicationContext(), "删除失败" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_my_record, menu);
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