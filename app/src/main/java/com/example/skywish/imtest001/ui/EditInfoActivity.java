package com.example.skywish.imtest001.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.config.BombConstants;
import com.example.skywish.imtest001.util.CollectionUtils;
import com.example.skywish.imtest001.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by skywish on 2015/7/6.
 */
public class EditInfoActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout ly_avatar, ly_words, ly_username, ly_nickname,
            ly_birthday, ly_location, ly_gender;
    private ImageView iv_avatar;
    private TextView tv_words, tv_username, tv_nickname, tv_gender, tv_location, tv_birthday;
    private String words, username, nickname, gender, location, birthday;
    boolean bool_gender;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfo);

        user = userManager.getCurrentUser(User.class);
        username = user.getUsername();

//        words = user.getWords();
//        nickname = user.getNick();
//        bool_gender = user.getSex();
//        location = user.getCity();
//        birthday = user.getBirthday();

        initView();
    }

    public void initView() {
        initToolbarWithBack("我的资料");

        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_words = (TextView) findViewById(R.id.tv_words);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        
        ly_avatar = (RelativeLayout) findViewById(R.id.layout_avatar);
        ly_words = (RelativeLayout) findViewById(R.id.layout_words);
        ly_username = (RelativeLayout) findViewById(R.id.layout_username);
        ly_nickname = (RelativeLayout) findViewById(R.id.layout_nickname);
        ly_birthday = (RelativeLayout) findViewById(R.id.layout_birthday);
        ly_location = (RelativeLayout) findViewById(R.id.layout_location);
        ly_gender = (RelativeLayout) findViewById(R.id.layout_gender);

        ly_avatar.setOnClickListener(this);
        ly_location.setOnClickListener(this);
        ly_birthday.setOnClickListener(this);
        ly_nickname.setOnClickListener(this);
        ly_username.setOnClickListener(this);
        ly_words.setOnClickListener(this);
        ly_gender.setOnClickListener(this);

        refreshData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_avatar:
                changeAvatar();
                // 延迟刷新，图片上传需要时间
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refreshData();
                    }
                }, 1000);
                break;
            case R.id.layout_nickname:
                changeNickName();
                break;
            case R.id.layout_gender:
                changeGender();
                break;
            case R.id.layout_location:
                changeLocation();
                break;
            case R.id.layout_birthday:
                changeDate();
                break;
            case R.id.layout_words:
                startActivity(new Intent(EditInfoActivity.this, ChangeWordsActivity.class));
                break;
        }
    }

    private void refreshData() {
        userManager.queryUser(username, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (CollectionUtils.isNotNull(list)) {
                    user = list.get(0);
                    updateUser(user);
                } else {
                    showToast("更新失败");
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("更新失败");
            }
        });
    }

    private void updatePhoto(String photo) {
        if (photo != null && !photo.equals("")) {
            ImageLoader.getInstance().displayImage(photo, iv_avatar,
                    ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.ic_default_profile_head);
        }
    }

    private void updateUser(User user) {
        updatePhoto(user.getAvatar());
        tv_username.setText(username);
        //昵称
        if (user.getNick() == null || user.getNick().isEmpty()) {
            tv_nickname.setText(username);
        } else {
            tv_nickname.setText(user.getNick());
        }
        //地区
        if (user.getCity() == null
                || user.getCity().isEmpty()) {
            tv_location.setText(this.getString(R.string.no_city));
        } else {
            tv_location.setText(user.getCity());
        }
        //生日
        if (user.getBirthday() == null || user.getBirthday().isEmpty()) {
            tv_birthday.setText("");
        } else {
            tv_birthday.setText(user.getBirthday());
        }
        //个性签名
        if (user.getWords() == null || user.getWords().isEmpty()) {
            tv_words.setText(this.getString(R.string.no_words));
        } else {
            tv_words.setText(user.getWords());
        }
        //性别
        Boolean sex = user.getSex();
        if (sex != null) {
            if (sex) {
                tv_gender.setText("男");
            } else {
                tv_gender.setText("女");
            }
        }
    }

    private void changeAvatar() {
        Intent intent;
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, BombConstants.REQUESTCODE_TAKE_LOCAL);
    }

    private void changeDate() {
        startActivity(new Intent(EditInfoActivity.this, ChangeDateActivity.class));
    }

    public void sendImage(String fileName) {
        final ProgressDialog progressDialog = new ProgressDialog(EditInfoActivity.this);
        progressDialog.setMessage("正在上传...");
        progressDialog.show();
        final BmobFile bmobFile = new BmobFile(new File(fileName));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                String url = bmobFile.getFileUrl(EditInfoActivity.this);
                User u = new User();
                u.setAvatar(url);
                u.setObjectId(userManager.getCurrentUser(User.class).getObjectId());
                u.update(getApplicationContext(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "上传失败" + s, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                showToast(s);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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
                                showToast("没有文件，请重新选择。");
                                return;
                            }
                            sendImage(localSelectPath);
                            Log.i("SEND", localSelectPath);
                        }
                    }
                    break;
            }
        }
    }

    private void changeNickName() {
        startActivity(new Intent(EditInfoActivity.this, ChangeNickActivity.class));
    }

    private void changeGender() {
        final User updateUser = new User();
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditInfoActivity.this);
        dialog.setTitle("性别")
                .setItems(new String[]{"男", "女"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                updateUser.setObjectId(user.getObjectId());
                                updateUser.setSex(true);
                                updateUser.update(EditInfoActivity.this, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        showToast("修改成功");
                                        refreshData();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        showToast("修改失败");
                                    }
                                });
                                break;
                            case 1:
                                updateUser.setObjectId(user.getObjectId());
                                updateUser.setSex(false);
                                updateUser.update(EditInfoActivity.this, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        showToast("修改成功");
                                        refreshData();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        showToast("修改失败");
                                    }
                                });
                                break;
                        }
                    }
                }).show();
    }

    private void changeLocation() {
        startActivity(new Intent(EditInfoActivity.this, ChangeCityActivity.class));
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
