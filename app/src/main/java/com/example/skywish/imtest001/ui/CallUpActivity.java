package com.example.skywish.imtest001.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.CallUpListAdapter;
import com.example.skywish.imtest001.bean.DBCallUp;
import com.example.skywish.imtest001.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;


public class CallUpActivity extends BaseActivity {
    Button randomCallBtn;
    ListView callUpListView;
    private CallUpListAdapter listAdapter;
    private ArrayList<DBCallUp> callUpList;
    final CharSequence[] items = {"删除", "取消"};
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_up);

        initToolbarWithBack("叫醒列表");
        user = userManager.getCurrentUser(User.class);
        queryData(user.getUsername());
        callUpListView = (ListView) findViewById(R.id.callUpList);
        callUpListView.setOnItemLongClickListener(new ItemLongClickListener());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listAdapter != null)
            listAdapter.musicAction.stop();
    }

    class ItemLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CallUpActivity.this);
            builder.setTitle("提示");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            dialog.dismiss();
                            DBCallUp callUp = callUpList.get(position);
                            delete(callUp.getObjectId());
                            callUpList.remove(position);
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
        BmobQuery<DBCallUp> query = new BmobQuery<>();
        query.addWhereEqualTo("userName", userName);
        query.findObjects(this, new FindListener<DBCallUp>() {
            @Override
            public void onSuccess(List<DBCallUp> list) {
                callUpList = new ArrayList<>();
                if (list.size() > 0) {
                    for (DBCallUp callUp : list) {
                        callUpList.add(callUp);
                    }
                }
                listAdapter = new CallUpListAdapter(getApplicationContext(), callUpList);
                callUpListView.setAdapter(listAdapter);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getApplicationContext(), "查询失败" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delete(String objectId) {
        DBCallUp callUp = new DBCallUp();
        callUp.setObjectId(objectId);
        callUp.delete(this, new DeleteListener() {
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
    private void recordIntent(){
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
