package com.example.skywish.imtest001.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.adapter.UserFriendShowAdapter;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/7/7.
 */
public class NearPeopleActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private Button btn_search;
    private RelativeLayout layout_info;
    private ListView list_near;
    private List<User> nearUser = new ArrayList<>();
    private UserFriendShowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearpeople);

        initToolbarWithBack("附近的人");

        list_near = (ListView) findViewById(R.id.list_near_people);
        list_near.setOnItemClickListener(this);
        layout_info = (RelativeLayout) findViewById(R.id.layout_info);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    public void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示")
                .setMessage("查看附近的人功能将获取你的位置信息，你的位置信息会被保留一段时间哦。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showPeople();
                    }
                }).setNegativeButton("取消", null).show();
    }

    public void showPeople() {
        final ProgressDialog dialog = new ProgressDialog(NearPeopleActivity.this);
        dialog.setMessage("正在查询，请稍等...");
        dialog.show();
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
//                option.setScanSpan(1000);// ���÷���λ����ļ��ʱ��Ϊ1000ms:����1000Ϊ�ֶ���λһ�Σ����ڻ����1000��Ϊ��ʱ��λ
//                option.setIsNeedAddress(false);// ����Ҫ������ַ��Ϣ
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                double latitude = bdLocation.getLatitude();
                double longtitude = bdLocation.getLongitude();
                BmobGeoPoint mpoint = null;
                mpoint = new BmobGeoPoint(longtitude, latitude);
                User u = userManager.getCurrentUser(User.class);
                final User user = new User();
                user.setLocation(mpoint);

                user.setObjectId(u.getObjectId());
                user.update(NearPeopleActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("位置更新成功", "");

                        //取消提示，显示列表
                        layout_info.setVisibility(View.GONE);
                        list_near.setVisibility(View.VISIBLE);

                        userManager.queryKiloMetersListByPage(false, 0, "location",
                                userManager.getCurrentUser(User.class).getLocation().getLongitude(),
                                userManager.getCurrentUser(User.class).getLocation().getLatitude(),
                                true, 10, null, false, new FindListener<User>() {
                                    @Override
                                    public void onSuccess(List<User> list) {
                                        showToast("查询成功");
                                        dialog.dismiss();
                                        if (!CollectionUtils.isNotNull(list)) {
                                            showToast("周围没有人哦!");
                                        } else {
                                            nearUser = list;
                                            adapter = new UserFriendShowAdapter(NearPeopleActivity.this, nearUser);
                                            list_near.setAdapter(adapter);
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        dialog.dismiss();
                                        showToast("查询错误" + s);
                                    }
                                });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        dialog.dismiss();
                        showToast("位置更新失败" + s);
                        Log.e("位置更新失败", "");
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        User user = nearUser.get(i);
        Intent intent = new Intent(this, UserInfoActivity.class);
//        intent.putExtra("from", "other");
        intent.putExtra("username", user.getUsername());
        Log.i("TEST", "SearchResult--onItemClick ： " + user.getUsername());
        startActivity(intent);
    }
}
