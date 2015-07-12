package com.example.skywish.imtest001.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.ClockListAdapter;
import com.example.skywish.imtest001.bean.DBClock;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.ui.AddClockActivity;
import com.example.skywish.imtest001.ui.MyClockActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by skywish on 2015/7/7.
 */
public class AlarmFragment extends Fragment {

    Button addClockBtn;
    ListView clockListView;
    private ClockListAdapter adapter;
    private ArrayList<DBClock> clockList;
    BmobUserManager userManager = BmobUserManager.getInstance(getActivity());
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_clock, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = userManager.getCurrentUser(User.class);

        initView();
    }

    private void initView() {
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("闹钟");

        addClockBtn = (Button) getView().findViewById(R.id.addClockBtn);
        addClockBtn.setOnClickListener(new ButtonListener());
        clockListView = (ListView) getView().findViewById(R.id.clockListView);
        clockListView.setOnItemClickListener(new ListViewListener());
    }

    class ListViewListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), AddClockActivity.class);
            DBClock clock = clockList.get(position);
            intent.putExtra("clock", clock);
            startActivityForResult(intent, 0);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == requestCode){
            queryClockData(user.getUsername());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        queryClockData(user.getUsername());
    }

    private void queryClockData(String userName){
        BmobQuery<DBClock> query = new BmobQuery<>();
        query.addWhereEqualTo("userName", userName);
        query.order("-updatedAt");
        query.findObjects(getActivity(), new FindListener<DBClock>() {
            @Override
            public void onSuccess(List<DBClock> list) {
                clockList = new ArrayList<>();
                for (DBClock clock : list)
                    clockList.add(clock);
                adapter = new ClockListAdapter(getActivity(), clockList);
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
        Intent intent = new Intent(getActivity(), AddClockActivity.class);
        startActivity(intent);
    }

}
