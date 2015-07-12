package com.example.skywish.imtest001.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.ClockListAdapter;
import com.example.skywish.imtest001.bean.DBClock;
import com.example.skywish.imtest001.bean.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class MyClockActivity extends BaseActivity {

    Button addClockBtn;
    ListView clockListView;
    private ClockListAdapter adapter;
    private ArrayList<DBClock> clockList;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clock);

        user = userManager.getCurrentUser(User.class);
        queryClockData(user.getUsername());
        addClockBtn = (Button) findViewById(R.id.addClockBtn);
        addClockBtn.setOnClickListener(new ButtonListener());
        clockListView = (ListView)findViewById(R.id.clockListView);
        clockListView.setOnItemClickListener(new ListViewListener());
    }
    class ListViewListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MyClockActivity.this, AddClockActivity.class);
            DBClock clock = clockList.get(position);
            intent.putExtra("clock", clock);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        queryClockData(user.getUsername());
    }

    private void queryClockData(String userName){
        BmobQuery<DBClock> query = new BmobQuery<>();
        query.addWhereEqualTo("userName", userName);
        query.order("-updatedAt");
        query.findObjects(this, new FindListener<DBClock>() {
            @Override
            public void onSuccess(List<DBClock> list) {
                clockList = new ArrayList<>();
                for (DBClock clock : list)
                    clockList.add(clock);
                adapter = new ClockListAdapter(getApplicationContext(), clockList);
                clockListView.setAdapter(adapter);
//                showToast(String.valueOf(clockList.size()));
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
    class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addClockBtn:
                    addClockIntent();
                    break;
            }
        }
    }
    private void addClockIntent(){
        Intent intent = new Intent(this, AddClockActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_clock, menu);
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
