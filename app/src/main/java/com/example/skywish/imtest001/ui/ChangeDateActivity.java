package com.example.skywish.imtest001.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.User;

import java.util.Calendar;
import java.util.Locale;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/7/6.
 */
public class ChangeDateActivity extends BaseActivity {

    DatePicker datePicker;
    TextView textDate;
    User currentUser;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_date);

        initToolbarWithBack("修改生日");

        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        int year         = calendar.get(Calendar.YEAR);
        int monthOfYear  = calendar.get(Calendar.MONTH);
        int dayOfMonth   = calendar.get(Calendar.DAY_OF_MONTH);

        currentUser = userManager.getCurrentUser(User.class);
        final User updateUser = new User();
        updateUser.setObjectId(currentUser.getObjectId());

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        textDate = (TextView)findViewById(R.id.tv_date);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = textDate.getText().toString();
                if (date.isEmpty()) {
                    showToast("请选择日期");
                    return;
                }
                updateUser.setBirthday(date);
                updateUser.update(ChangeDateActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("修改成功");
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("修改失败");
                    }
                });
            }
        });

        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar mCalendar = Calendar.getInstance();
                if(isDateAfter(view)){
                    view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH), this);
                }
                else{
                    textDate.setText(year + "年" + (monthOfYear + 1) + "月" +
                            dayOfMonth + "日");
                }

            }

            private boolean isDateAfter(DatePicker tempView) {
                Calendar mCalendar = Calendar.getInstance();
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(),
                        0, 0, 0);
                if(tempCalendar.after(mCalendar))
                    return true;
                else
                    return false;
            }
        });
    }
}
