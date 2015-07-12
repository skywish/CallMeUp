package com.example.skywish.imtest001.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.skywish.imtest001.R;
import com.example.skywish.imtest001.bean.DBClock;
import com.example.skywish.imtest001.bean.User;
import com.example.skywish.imtest001.broadcast.ClockReceiver;

import java.util.Calendar;
import java.util.TimeZone;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class AddClockActivity extends BaseActivity {
    Button submitBtn;
    TimePicker timePicker;
    Button chooseRingBtn;
    Button repeatBtn;
    Button deleteClockBtn;
    RadioGroup radioGroup;
    private static final long DAY = 1000L * 60 * 60 * 24;
    private int mHour = -1;
    private int mMinute = -1;
    private String musicPath;
    private String musicName;
    private int repeatTime;
    private final int everyDay = 0;
    private final int oneTime = 1;
    private DBClock clock;
    private AlarmManager alarmManager;
    private Calendar calendar;
    private DBClock newClock;
    private boolean isGirl;
    private boolean isSystem;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clock);

        user = userManager.getCurrentUser(User.class);
        Log.i("Test", "Add " + user + ":" + user.getUsername());

        init();
        initToolbarWithBack("添加闹钟");
        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new ButtonListener());
        chooseRingBtn = (Button) findViewById(R.id.chooseRingBtn);
        chooseRingBtn.setOnClickListener(new ButtonListener());
        repeatBtn = (Button) findViewById(R.id.repeatBtn);
        repeatBtn.setOnClickListener(new ButtonListener());
        deleteClockBtn = (Button) findViewById(R.id.deleteClockBtn);
        Intent intent = getIntent();
        newClock = (DBClock) intent.getSerializableExtra("clock");
        deleteClockBtn.setOnClickListener(new ButtonListener());
        if (newClock != null) {
            deleteClockBtn.setVisibility(View.VISIBLE);
        }
        isGirl = true;
        clock.setIsGirl(isGirl);
        clock.setIsSystem(isSystem);
        radioGroup = (RadioGroup) findViewById(R.id.sexRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.isGirl:
                        isGirl = true;
                        clock.setIsGirl(isGirl);
                        break;
                    case R.id.isBoy:
                        isGirl = false;
                        clock.setIsGirl(isGirl);
                        break;
                    case R.id.isSystem:
                        isSystem = true;
                        clock.setIsSystem(isSystem);
                        break;
                    default:
                        clock.setIsGirl(false);
                        clock.setIsSystem(false);
                        break;
                }
            }
        });
    }

    private void updateObject(String id) {
        newClock.setIsOpen(true);
        newClock.update(this, id, new UpdateListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "修改失败：" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        clock = new DBClock();
        repeatTime = 0;
        clock.setUserName(user.getUsername());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        if (mHour == -1 && mMinute == -1) {
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        }
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setCurrentHour(mHour);
        timePicker.setCurrentMinute(mMinute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
            }
        });
        timePicker.setCurrentHour(mHour);
        timePicker.setCurrentMinute(mMinute);

        musicPath = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE).getPath();
        clock.setMusicPath(musicPath);
        clock.setRepeatTime(repeatTime);
    }

    private void insertObject(BmobObject object) {
        object.save(AddClockActivity.this, new SaveListener() {
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

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submitBtn:
                    addClock(repeatTime);
                    if (newClock != null) {
                        if (!clock.equals(newClock)) {
                            clock.setObjectId(newClock.getObjectId());
                            newClock = clock;
                            updateObject(newClock.getObjectId());
                            setResult(0, getIntent());
                        }
                    } else {
                        insertObject(clock);
                    }
                    break;
                case R.id.chooseRingBtn:
                    chooseRing();
                    break;
                case R.id.repeatBtn:
                    showRepeatList();
                    break;
                case R.id.deleteClockBtn:
                    deleteClock();
                    break;
            }
        }
    }

    private void addClock(int repeatTime) {
        long systemTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        clock.setHour(mHour);
        clock.setMinute(mMinute);
        clock.setIsOpen(true);
        clock.setClockID( calendar.getTimeInMillis());
        long selectTime = calendar.getTimeInMillis();
        Intent intent = new Intent(AddClockActivity.this, ClockReceiver.class);
        intent.putExtra("clock", clock);

        PendingIntent sender = PendingIntent.getBroadcast(AddClockActivity.this, clock.getClockID().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        Long time = selectTime - systemTime;
        int hour = time.intValue() / 3600000;
        int minute = (time.intValue() % 3600000) / 60000 + 1;
        if (isSystem) {
            if (minute <= 1) {
                Toast.makeText(this, "还有不到1分钟,闹铃将会响起", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "还有" + hour + "小时" + minute + "分钟,闹铃将会响起", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isGirl) {
                if (minute <= 1) {
                    Toast.makeText(this, "还有不到1分钟,将会有个神秘的她叫醒你", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "还有" + String.valueOf(hour) + "小时" + minute + "分钟，将会有个神秘的她叫醒你", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (minute <= 1) {
                    Toast.makeText(this, "还有不到1分钟,将会有个神秘的她叫醒你", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "还有" + String.valueOf(hour) + "小时" + minute + "分钟，将会有个神秘的他叫醒你", Toast.LENGTH_SHORT).show();
                }
            }
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, sender);
//        switch (repeatTime) {
//            case 0:
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectTime, DAY, sender);
//                break;
//            case 1:
//                alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, sender);
//                break;
//            default:
//                alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, sender);
//                break;
//        }
        AddClockActivity.this.finish();
    }

    private void chooseRing() {
        Intent intent = new Intent(this, ChooseRingActivity.class);
        intent.putExtra("musicPath", musicPath);
        intent.putExtra("musicName", musicName);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            musicName = data.getStringExtra("musicName");
            musicPath = data.getStringExtra("musicPath");
            clock.setMusicPath(musicPath);
            chooseRingBtn.setText(musicName);
        }
    }

    private CharSequence[] items = {"只响一次", "每天", "工作日", "周末"};

    private void showRepeatList() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddClockActivity.this);
        builder.setTitle("Repeat");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        repeatTime = oneTime;
                        break;
                    case 1:
                        repeatTime = everyDay;
                        break;
                    case 2:
                        repeatTime = oneTime;
                        break;
                    case 3:
                        repeatTime = oneTime;
                        break;
                    default:
                        repeatTime = everyDay;
                        break;
                }
            }
        }).show();
        clock.setRepeatTime(repeatTime);
    }

    private void deleteClock() {
        Intent intent = new Intent(AddClockActivity.this, ClockReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(AddClockActivity.this, newClock.getClockID().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);
        deleteObject(newClock.getObjectId());
        setResult(0, getIntent());
        finish();
    }

    private void deleteObject(String id) {
        clock.delete(this, id, new DeleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(AddClockActivity.this, "已删除", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_clock, menu);
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
